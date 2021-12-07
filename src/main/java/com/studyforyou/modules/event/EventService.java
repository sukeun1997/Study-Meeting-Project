package com.studyforyou.modules.event;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Event createEvent(Event event, Account account, Study study) {
        event.setStudy(study);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        eventPublisher.publishEvent(new StudyUpdatedEvent(study,study.getTitle() + "에 새로운 모임이 생성되었습니다."));
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Set<Event> getAllEvents() {
        return eventRepository.findAll().stream().collect(Collectors.toSet());
    }

    public void updateForm(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingEnrollment();
        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(),event.getTitle() + " 모임의 정보가 수정되었습니다."));
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(),event.getTitle()+" 모임이 취소되었습니다."));
        // TODO event 에 해당하는 enrollment 정보가 있을시 enrollment 정보도 같이 삭제되는지 확인하기 -> 삭제 안되서 CASCADE 처리함 추후 다르게 처리하는지 확인하기

    }

    public void enrollEvent(Account account, Event event) {

        if (!enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setAccount(account);
            enrollment.setAccepted(event.isAbleToAcceptFCFS());
            enrollment.setEnrolledAt(LocalDateTime.now());
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }

    }

    public void disenrollEvent(Account account, Event event) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (enrollment != null) {
            if(!enrollment.isAttended()) { // 체크인이 아니여야 삭제
                event.removeEnrollment(enrollment);
                enrollmentRepository.delete(enrollment);
                event.acceptNextWaitingEnrollment();
            }
        }
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.acceptEnrollment(enrollment);
        eventPublisher.publishEvent(new StudyEventUpdatedEvent(enrollment,event.getTitle() + " 모임에 대한 참가 신청이 수락 되었습니다."));
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.rejectEnrollment(enrollment);
        eventPublisher.publishEvent(new StudyEventUpdatedEvent(enrollment,event.getTitle() + " 모임에 대한 참가 신청이 거절 되었습니다."));
    }

    public void checkinEnrollment(Event event, Enrollment enrollment) {
        event.checkinEnrollment(enrollment);
    }

    public void checkoutEnrollment(Event event, Enrollment enrollment) {
        event.checkoutEnrollment(enrollment);
    }
}


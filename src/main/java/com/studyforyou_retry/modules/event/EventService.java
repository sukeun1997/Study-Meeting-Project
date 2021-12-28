package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.study.Study;
import com.studyforyou_retry.modules.study.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Event createEvent(Account account, Study study, EventForm eventForm) {

        Event event = modelMapper.map(eventForm, Event.class);
        event.createEvent(account, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study,study.getTitle() + "스터디에 새로운 모임이 생성되었습니다."));
        return eventRepository.save(event);
    }

    public void enrollEvent(Account account, Event event) {

        if (!isExistsByEventAndAccount(account, event)) {
            Enrollment enrollment = Enrollment.createEnrollment(account, event);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
            return;
        }
        throw new RuntimeException("enrollEvent not activate");

    }

    public void disenrollEvent(Account account, Event event) {

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (enrollment != null) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingFCFS();
        }
        throw new RuntimeException("disenrollEvent not activate");
    }

    private boolean isExistsByEventAndAccount(Account account, Event event) {
        return enrollmentRepository.existsByEventAndAccount(event, account);
    }

    public void rejectEnroll(Event event, Enrollment enrollment) {

        if (event.isRejectable(enrollment)) {
            enrollment.rejectEnroll();
            eventPublisher.publishEvent(new EnrollmentUpdateEvent(enrollment,event.getTitle() + " 모임 참여 신청이 거절 되었습니다."));
            return;
        }

        throw new RuntimeException("rejectEnroll 오류");

    }

    public void acceptEnroll(Event event, Enrollment enrollment) {
        if (event.isAcceptable(enrollment)) {
            enrollment.acceptEnroll();
            eventPublisher.publishEvent(new EnrollmentUpdateEvent(enrollment,event.getTitle() + " 모임 참여 신청이 수락 되었습니다."));
            return;
        }
        throw new RuntimeException("acceptEnroll 오류");
    }

    public void checkinEnroll(Enrollment enrollment) {

        if (!enrollment.isAttend()) {
            enrollment.checkin();
            return;
        }
        throw new RuntimeException("checkinEnroll 오류");
    }

    public void cancelCheckinEnroll(Enrollment enrollment) {
        if (enrollment.isAttend()) {
            enrollment.cancelCheckin();
            return;
        }
        throw new RuntimeException("cancel-checkinEnroll 오류");
    }

    public void updateEvent(EventForm eventForm, Event event) {
        eventForm.setEventType(event.getEventType());
        modelMapper.map(eventForm, event);
        event.acceptWaitingList();
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}

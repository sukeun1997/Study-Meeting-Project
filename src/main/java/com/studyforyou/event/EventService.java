package com.studyforyou.event;

import com.studyforyou.domain.Account;
import com.studyforyou.domain.Enrollment;
import com.studyforyou.domain.Event;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.EventForm;
import com.studyforyou.repository.EnrollmentRepository;
import com.studyforyou.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    public Event createEvent(Event event, Account account, Study study) {
        event.setStudy(study);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
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

        // TODO 참여자 제한 수가 증가했을시 참여 대기중인 사람 확정으로 변경
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);

        // TODO event 에 해당하는 enrollment 정보가 있을시 enrollment 정보도 같이 삭제되는지 확인하기 -> 삭제 안되서 CASCADE 처리함 추후 다르게 처리하는지 확인하기

    }

    public void enrollEvent(Account account, Event event) {

        if (!enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setAccount(account);
            enrollment.setAccepted(event.isAbleToAccept());
            enrollment.setEnrolledAt(LocalDateTime.now());
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }

    }

    public void disenrollEvent(Account account, Event event) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (enrollment != null) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
            //TODO 첫번째 대기자 확정
        }
    }

}


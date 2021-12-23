package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;

    public Event createEvent(Account account, Study study, EventForm eventForm) {

        Event event = modelMapper.map(eventForm, Event.class);
        event.createEvent(account, study);

        return eventRepository.save(event);
    }

    public void enrollEvent(Account account, Event event) {

        if (!isExistsByEventAndAccount(account, event)) {
            Enrollment enrollment = new Enrollment();

            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccount(account);
            enrollment.setAccepted(event.isAcceptableFCFS(enrollment));
            enrollment.setEvent(event);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }

    }

    public void disenrollEvent(Account account, Event event) {

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (enrollment != null) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            // TODO 선착순일시 다음 선착순 자동 확정 
        }
    }

    private boolean isExistsByEventAndAccount(Account account, Event event) {
        return enrollmentRepository.existsByEventAndAccount(event, account);
    }
}

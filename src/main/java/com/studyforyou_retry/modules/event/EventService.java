package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            Enrollment enrollment = Enrollment.createEnrollment(account, event);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }

    }

    public void disenrollEvent(Account account, Event event) {

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (enrollment != null) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingFCFS();
        }
    }

    private boolean isExistsByEventAndAccount(Account account, Event event) {
        return enrollmentRepository.existsByEventAndAccount(event, account);
    }

    public void rejectEnroll(Event event, Enrollment enrollment) {

        if (event.isRejectable(enrollment)) {
            enrollment.rejectEnroll();
            return;
        }

        throw new RuntimeException("rejectEnroll 오류");

    }

    public void acceptEnroll(Event event, Enrollment enrollment) {
        if (event.isAcceptable(enrollment)) {
            enrollment.acceptEnroll();
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
}

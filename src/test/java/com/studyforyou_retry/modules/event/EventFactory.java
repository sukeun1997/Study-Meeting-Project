package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventFactory {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private final EventService eventService;

    public Event createEventFCFS(Account account, Study study, String name) {

        EventForm eventForm = new EventForm();
        eventForm.setTitle(name);
        eventForm.setDescription(name);
        eventForm.setStartDateTime(NOW.plusDays(1));
        eventForm.setEndDateTime(NOW.plusDays(3));
        eventForm.setEndEnrollmentDateTime(NOW.plusDays(2));
        eventForm.setLimitOfEnrollments(2);
        eventForm.setEventType(EventType.FCFS);

        return eventService.createEvent(account, study, eventForm);
    }
}

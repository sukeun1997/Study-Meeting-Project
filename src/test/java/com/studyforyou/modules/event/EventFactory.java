package com.studyforyou.modules.event;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class EventFactory {

    @Autowired
    EventRepository eventRepository;

    public Event createEvent(Account account , Study study) {

        Event event = Event.builder().eventType(EventType.FCFS)
                .description("gd")
                .createdBy(account)
                .title("gd")
                .study(study)
                .limitOfEnrollments(3)
                .enrollments(new ArrayList<>())
                .createdDateTime(LocalDateTime.now())
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .endEnrollmentDateTime(LocalDateTime.now().plusHours(3)).build();
        return eventRepository.save(event);
    }
}

package com.studyforyou.event;

import com.studyforyou.domain.Account;
import com.studyforyou.domain.Event;
import com.studyforyou.domain.Study;
import com.studyforyou.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;


    public Event createEvent(Event event, Account account, Study study) {
        event.setStudy(study);
        event.setCreateBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }
}

package com.studyforyou.modules.event;

import com.studyforyou.modules.study.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.AsyncListener;
import java.util.concurrent.Executor;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class StudyEventListener  {

    @EventListener
    public void handleStudyCreateEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyCreatedEvent.getStudy();
        log.info(study.getTitle() + "is created");

        // TODO 이메일을 전송 / DB에 Notifications 저장

        throw new RuntimeException();
    }

}


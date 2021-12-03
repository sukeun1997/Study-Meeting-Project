package com.studyforyou.modules.event;

import com.studyforyou.modules.study.Study;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudyCreatedEvent {

    Study study;

    public StudyCreatedEvent(Study newStudy) {
        this.study = newStudy;
    }
}


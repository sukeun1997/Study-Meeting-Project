package com.studyforyou.modules.event;

import com.studyforyou.modules.study.Study;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyUpdatedEvent {

    private final Study study;
    private final String message;


}


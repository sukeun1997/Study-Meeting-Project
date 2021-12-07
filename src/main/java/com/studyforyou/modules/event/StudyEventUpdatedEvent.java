package com.studyforyou.modules.event;

import com.studyforyou.modules.study.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyEventUpdatedEvent {

    private final Enrollment enrollment;
    private final String message;


}


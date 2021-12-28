package com.studyforyou_retry.modules.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@AllArgsConstructor
public class EnrollmentUpdateEvent {
    private Enrollment enrollment;
    private String message;
}

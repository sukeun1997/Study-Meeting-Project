package com.studyforyou_retry.modules.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Component
@RequestMapping
public class EventFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EventForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;

        if (isNotValidEndEnrollmentDateTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong value", "모임 신청 마감 시간은 모임 종료 시간 이전 이여야 합니다.");
        }
        if (isNotValidStartTime(eventForm)) {
            errors.rejectValue("startDateTime","wrong value", "모임 시작 시간은 현재 시간 이후로 가능합니다.");
        }

        if (isNotValidEndDateTime(eventForm)) {
            errors.rejectValue("endDateTime", "wrong value", "모임 종료 시간은 시작 시간과 모집 종료 시간 이후여야 합니다.");
        }
    }

    private boolean isNotValidStartTime(EventForm eventForm) {
        return isFirstBeforeSecond(eventForm.getStartDateTime(), LocalDateTime.now()) && eventForm.getStartDateTime() == null;
    }

    private boolean isNotValidEndDateTime(EventForm eventForm) {
        return isFirstBeforeSecond(eventForm.getEndDateTime(), eventForm.getStartDateTime()) || isFirstBeforeSecond(eventForm.getEndDateTime(), eventForm.getEndEnrollmentDateTime());
    }

    private boolean isFirstBeforeSecond(LocalDateTime startDateTime, LocalDateTime now) {
        return startDateTime.isBefore(now);
    }

    private boolean isNotValidEndEnrollmentDateTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isAfter(eventForm.getEndDateTime());
    }
}

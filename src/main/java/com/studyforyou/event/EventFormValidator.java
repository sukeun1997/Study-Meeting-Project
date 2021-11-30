package com.studyforyou.event;

import com.studyforyou.domain.Event;
import com.studyforyou.dto.EventForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EventForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;

        if (isNotValidStartTime(eventForm)) {
            errors.rejectValue("startDateTime","wrong time","현재 시간 이후로 가능합니다.");
        }

        if (isNotValidEndTime(eventForm)) {
            errors.rejectValue("endDateTime","wrong time","모임 종료시간은 모임 시작시간보다 하루 이상이여야 합니다.");
        }

        if (!isValidEndEnrollmentTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime","wrong time", "등록 마감 시간은 모임 시작시간과 모임 종료시간 사이여야 합니다.");
        }
    }

    private boolean isValidEndEnrollmentTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isAfter(eventForm.getStartDateTime()) && eventForm.getEndEnrollmentDateTime().isBefore(eventForm.getEndDateTime());
    }

    private boolean isNotValidEndTime(EventForm eventForm) {
        return eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime().plusDays(1));
    }

    private boolean isNotValidStartTime(EventForm eventForm) {
        return eventForm.getStartDateTime().isBefore(eventForm.getStartDateTime());
    }


    public void isValidEnrollmentSize(EventForm eventForm, Event event, BindingResult bindingResult) {
        if (eventForm.getLimitOfEnrollments() < event.getAcceptedCount()) {
            bindingResult.rejectValue("limitOfEnrollments","wrong number", "확정된 참여자 수 보다 값이 더 커야합니다.");
        }
        // TODO 참여신청 구현후 제대로 작동하는지 확인
    }
}

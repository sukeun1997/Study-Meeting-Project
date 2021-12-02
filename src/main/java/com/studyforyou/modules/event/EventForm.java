package com.studyforyou.modules.event;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class EventForm {

    @Length(min = 1, max = 50, message = "50자 이내로 입력해주세요.")
    private String title;

    private EventType eventType;

    @Min(value = 2, message = "2명 이상 입력 해 주세요")
    private int limitOfEnrollments;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    @NotEmpty(message = "모임 설명은 필수 입력 값 입니다.")
    private String description;
}

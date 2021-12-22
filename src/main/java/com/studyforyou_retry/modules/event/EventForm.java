package com.studyforyou_retry.modules.event;

import lombok.Data;
import org.checkerframework.checker.formatter.qual.Format;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Lob;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class EventForm {

    @NotBlank(message = "제목을 입력 해주세요")
    @Length(max = 50, message = "50자 이내로 입력 해주세요")
    private String title;

    @Min(value = 2, message = "최소 2명 이상 입력 해주세요")
    private int limitOfEnrollments;

    @NotNull(message = "모임 신청 마감 일시를 입력하세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime;

    @NotNull(message = "모임 시작 일시를 입력하세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;


    @NotNull(message = "모임 종료 일시를 입력하세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    private EventType eventType;

    @NotBlank(message = "모임 설명을 입력 하세요.")
    private String description;

    private String files;
}

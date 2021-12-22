package com.studyforyou_retry.modules.study;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class StudyNewPathForm {

    @Pattern(regexp = "^[a-zA-z가-힣0-9-_]{3,20}$", message = "공백없이 문자, 숫자, 대시(-)와 언더바(_)만 3자 이상 20자 이내로 입력하세요.")
    private String newPath;
}

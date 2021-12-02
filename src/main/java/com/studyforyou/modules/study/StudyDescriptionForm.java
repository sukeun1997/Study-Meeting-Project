package com.studyforyou.modules.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class StudyDescriptionForm {

    @Length(max = 100, message = "100자 이내로 써주세요")
    private String shortDescription;

    @NotBlank(message = "필수 입력 값 입니다.")
    private String fullDescription;

}

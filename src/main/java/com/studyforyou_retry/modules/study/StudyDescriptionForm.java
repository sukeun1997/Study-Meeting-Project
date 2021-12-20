package com.studyforyou_retry.modules.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class StudyDescriptionForm {

    @NotBlank(message = "짧은 소개를 입력하세요.")
    @Length(max = 100, message = "100자 이내로 입력하세요.")
    private String shortDescription;

    @NotBlank(message = "상세 소개를 입력하세요.")
    private String fullDescription;


}

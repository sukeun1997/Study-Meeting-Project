package com.studyforyou_retry.modules.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudyForm {

    @Pattern(regexp = "^[a-zA-z가-힣0-9-_]{2,20}$", message = "공백없이 문자, 숫자, 대시(-)와 언더바(_)만 2자 이상 20자 이내로 입력하세요.")
    private String path;

    @Length(max = 50 , message = "50자 이내로 입력해주세요")
    @NotBlank(message = "이름을 입력해주세요")
    private String title;

    @Length(max = 100 , message = "100자 이내로 입력해주세요")
    private String shortDescription;

    @NotBlank(message = "상세 설명은 필수 입력 값 입니다.")
    private String fullDescription;


}

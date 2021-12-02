package com.studyforyou.modules.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudyForm {

    @Length(max = 50, message = "50자 이내로 입력해 주세요")
    private String title;

    @Length(max = 100, message = "100자 이내로 써주세요")
    private String shortDescription;

    @NotBlank(message = "필수 입력 값 입니다.")
    private String fullDescription;

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")
    private String path;
}

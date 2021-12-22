package com.studyforyou_retry.modules.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class StudyNewTitleForm {

    @Length(max = 50 , message = "50자 이내로 입력해주세요")
    @NotBlank(message = "이름을 입력해주세요")
    private String newTitle;
}

package com.studyforyou.modules.study;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TitleForm {

    @Length(min = 1, max = 50, message = "1자 이상 50자 이내로 입력해주세요")
    private String newTitle;
}

package com.studyforyou.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;

@Data
public class PasswordForm {

    @Length(min = 8, max = 50 , message = "8자이상 50자 이하로 입력 해 주세요")
    private String newPassword;

    @Length(min = 8, max = 50 , message = "8자이상 50자 이하로 입력 해 주세요")
    private String newPasswordConfirm;
}

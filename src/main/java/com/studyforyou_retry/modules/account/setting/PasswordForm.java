package com.studyforyou_retry.modules.account.setting;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @Length(min = 8, max = 50, message = "8자 이상 50자 이하로 입력 해주세요")
    private String newPassword;

    @Length(min = 8, max = 50, message = "8자 이상 50자 이하로 입력 해주세요")
    private String newPasswordConfirm;
}

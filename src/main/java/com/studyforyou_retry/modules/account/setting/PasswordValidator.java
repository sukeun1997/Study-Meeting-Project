package com.studyforyou_retry.modules.account.setting;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;

        if (!isPasswordSame(passwordForm)) {
            errors.rejectValue("newPasswordConfirm","wrong value", "입력된 패스워드가 동일하지 않습니다.");
        }
    }

    private boolean isPasswordSame(PasswordForm passwordForm) {
        return passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm());
    }
}

package com.studyforyou.settings;

import com.studyforyou.dto.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;



public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
       PasswordForm passwordForm = (PasswordForm) target;

        if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "worng.value", "입력한 새 패스워드가 일치하지 않습니다.");
        }
    }
}

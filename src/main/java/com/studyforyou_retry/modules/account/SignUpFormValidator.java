package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        SignUpForm signUpForm = (SignUpForm) target;

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "wrong name", "이미 중복되는 닉네임이 있습니다.");
        }

        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "wrong name", "이미 중복되는 이메일이 있습니다.");
        }

    }
}

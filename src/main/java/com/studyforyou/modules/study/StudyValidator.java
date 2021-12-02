package com.studyforyou.modules.study;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(StudyForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        StudyForm studyForm = (StudyForm) target;

        if (studyRepository.existsByPath(studyForm.getPath())) {
            errors.rejectValue("path","wrong input", "해당 경로는 사용이 불가능 합니다.");
        }

    }
}

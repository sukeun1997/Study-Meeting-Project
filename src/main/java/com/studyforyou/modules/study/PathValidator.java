package com.studyforyou.modules.study;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PathValidator implements Validator {

    private final StudyRepository studyRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PathForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PathForm pathForm = (PathForm) target;

        if (studyRepository.existsByPath(pathForm.getNewPath())) {
            errors.rejectValue("newPath","wrong-path","현재 주소는 사용하실 수 없습니다.");
        }
    }
}

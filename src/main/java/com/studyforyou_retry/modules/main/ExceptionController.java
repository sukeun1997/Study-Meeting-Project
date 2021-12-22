package com.studyforyou_retry.modules.main;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({ Exception.class })
    public String handleException(final Exception ex, Model model) {
        log.info(ex.getClass().getName());
        log.error("error", ex);

        model.addAttribute("error", "잘못된 접근 또는 오류 입니다.");
        return "error";
    }
}

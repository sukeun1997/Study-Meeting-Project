package com.studyforyou.modules.main;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.CurrentAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public String  RuntimeExceptionHandler(@CurrentAccount Account account ,HttpServletRequest httpServletRequest, RuntimeException e){

        if (account != null) {
            log.info("{} 계정이 {} 에 잘못된 요청을 보냈습니다.",account.getNickname(),httpServletRequest.getRequestURI());
        } else {
            log.info("익명 계정이 {} 에 잘못된 요청을 보냈습니다.", httpServletRequest.getRequestURI());
        }

        return "error";

    }


}

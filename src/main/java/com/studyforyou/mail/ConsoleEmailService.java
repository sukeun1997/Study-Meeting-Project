package com.studyforyou.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local") // 로컬환경 ( 개발환경 )
@Component // 빈주입
@Slf4j
public class ConsoleEmailService implements EmailService{

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email: {} ", emailMessage.getMessage());
    }
}

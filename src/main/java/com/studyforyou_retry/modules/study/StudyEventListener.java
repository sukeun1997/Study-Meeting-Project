package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.infra.config.AppProperties;
import com.studyforyou_retry.infra.mail.EmailMessage;
import com.studyforyou_retry.infra.mail.HtmlEmailService;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.setting.AccountPredicate;
import com.studyforyou_retry.modules.account.setting.Notifications;
import com.studyforyou_retry.modules.notification.Notification;
import com.studyforyou_retry.modules.notification.NotificationRepository;
import com.studyforyou_retry.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyEventListener {

    private final AccountRepository accountRepository;
    private final HtmlEmailService htmlEmailService;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final StudyRepository studyRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {

        log.info("start");
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> iterable = accountRepository.findAll(AccountPredicate.findAccountWithTagsAndZones(study.getTags(), study.getZones()));

        iterable.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                sendEmail(study, account);
            }

            if (account.isStudyCreatedByWeb()) {
                createNotification(study, account);
            }
        });
    }

    private void createNotification(Study study, Account account) {
        Notification notification = Notification.builder()
                .title("새로운 스터디 알림")
                .message("회원님의 관심에 맞는 새로운 스터디가 생겼습니다.")
                .to(account)
                .notificationType(NotificationType.NEW_STUDY)
                .checked(false)
                .createdDateTime(LocalDateTime.now())
                .link(appProperties.getHost() + "/study/" + study.getPath())
                .build();

        notificationRepository.save(notification);
    }

    private void sendEmail(Study study, Account account) {


        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", study.getTitle() + " 스터디가 생겼어요");
        context.setVariable("link", "/study/" + study.getPath());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("linkName", study.getTitle() + " 스터디 둘러보기");

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디 포유 - 회원님 관심에 맞는 스터디가 새로 생겼어요 !")
                .message(message)
                .build();

        htmlEmailService.sendEmail(emailMessage);
    }
}

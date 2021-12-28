package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.infra.config.AppProperties;
import com.studyforyou_retry.infra.mail.EmailMessage;
import com.studyforyou_retry.infra.mail.HtmlEmailService;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.setting.AccountPredicate;
import com.studyforyou_retry.modules.notification.Notification;
import com.studyforyou_retry.modules.notification.NotificationRepository;
import com.studyforyou_retry.modules.notification.NotificationType;
import com.studyforyou_retry.modules.study.Study;
import com.studyforyou_retry.modules.study.StudyCreateEvent;
import com.studyforyou_retry.modules.study.StudyRepository;
import com.studyforyou_retry.modules.study.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
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
    public void handleStudyCreateEvent(StudyCreateEvent studyCreateEvent) {

        log.info("study publish event");
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreateEvent.getStudy().getId());
        Iterable<Account> iterable = accountRepository.findAll(AccountPredicate.findAccountWithTagsAndZones(study.getTags(), study.getZones()));

        iterable.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                sendEmail(study, account, "회원님의 관심에 맞는 " + study.getTitle() + " 스터디가 새로 생겼습니다.", "스터디 포유 - 관심에 맞는 신규 스터디 알림");
            }

            if (account.isStudyCreatedByWeb()) {
                sendNotification(study, account, "회원님의 관심에 맞는 새로운 스터디가 생겼습니다.", NotificationType.NEW_STUDY);
            }
        });
    }

    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
        log.info("study update event");

        Set<Account> accounts = new HashSet<>();
        Study study = studyRepository.findStudyWithMembersAndManagersById(studyUpdateEvent.getStudy().getId());

        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        String content = studyUpdateEvent.getMessage();

        accounts.stream().forEach(account -> {
            if (account.isStudyUpdatedByEmail()) {
                sendEmail(study, account, content, "스터디 포유 - " + study.getTitle() + " 스터디 새로운 알림");
            }

            if (account.isStudyUpdatedByWeb()) {
                sendNotification(study, account, content, NotificationType.JOIN_STUDY);
            }
        });
    }

    @EventListener
    public void handleEnrollmentUpdateEvent(EnrollmentUpdateEvent enrollmentUpdateEvent) {
        log.info("enrollment update event");
        Account account = enrollmentUpdateEvent.getEnrollment().getAccount();
        Event event = enrollmentUpdateEvent.getEnrollment().getEvent();
        String content = enrollmentUpdateEvent.getMessage();
        Study study = event.getStudy();

        if (account.isStudyEnrollmentResultByEmail()) {
            sendEmail(study, account, content, "스터디 포유 - " + study.getTitle() + " 스터디 "+event.getTitle()+" 모임 참가 신청 결과");
        }

        if (account.isStudyEnrollmentResultByWeb()) {
            sendNotification(study,account,content,NotificationType.EVENT);
        }
    }

    private void sendNotification(Study study, Account account, String content, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .title(study.getTitle() + " 스터디 알림")
                .message(content)
                .to(account)
                .notificationType(notificationType)
                .checked(false)
                .createdDateTime(LocalDateTime.now())
                .link(appProperties.getHost() + "/study/" + study.getPath())
                .build();

        notificationRepository.save(notification);
    }

    private void sendEmail(Study study, Account account, String content, String subject) {


        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", content);
        context.setVariable("link", "/study/" + study.getPath());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("linkName", study.getTitle() + " 스터디 바로가기");

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject(subject)
                .message(message)
                .build();

        htmlEmailService.sendEmail(emailMessage);
    }
}

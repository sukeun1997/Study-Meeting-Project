package com.studyforyou.modules.event;

import com.querydsl.core.types.Predicate;
import com.studyforyou.infra.config.AppProperties;
import com.studyforyou.infra.mail.EmailMessage;
import com.studyforyou.infra.mail.EmailService;
import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.AccountPredicates;
import com.studyforyou.modules.account.AccountRepository;
import com.studyforyou.modules.account.settings.Notifications;
import com.studyforyou.modules.notification.Notification;
import com.studyforyou.modules.notification.NotificationRepository;
import com.studyforyou.modules.notification.NotificationType;
import com.studyforyou.modules.study.Study;
import com.studyforyou.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreateEvent(StudyCreatedEvent studyCreatedEvent) {
//        Study study = studyCreatedEvent.getStudy(); // Detached 상태 , mangers 정보만 가지고 있음
        Study study = studyRepository.findZonesWithTagsById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> iterable = accountRepository.findAll(AccountPredicates.findTagsWithZonesById(study.getTags(), study.getZones()));

        iterable.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                sendCreatedStudyEmail(study, account);
            }

            if (account.isStudyCreatedByWeb()) {
                sendCreatedStudyNotification(study, account);
            }
        });

    }

    private void sendCreatedStudyNotification(Study study, Account account) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationType.STUDY_CREATED);
        notification.setAccount(account);
        notification.setTitle(study.getTitle());
        notification.setLink("study/"+ study.getEncodedPath());
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(study.getShortDescription());
        notificationRepository.save(notification);
    }

    private void sendCreatedStudyEmail(Study study, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", "새로운 스터디가 생겼습니다");
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디 포유 '"+ study.getTitle()+ "' 이 생겼습니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

}


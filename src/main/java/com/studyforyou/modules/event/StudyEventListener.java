package com.studyforyou.modules.event;

import com.studyforyou.infra.config.AppProperties;
import com.studyforyou.infra.mail.EmailMessage;
import com.studyforyou.infra.mail.EmailService;
import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.AccountPredicates;
import com.studyforyou.modules.account.AccountRepository;
import com.studyforyou.modules.notification.Notification;
import com.studyforyou.modules.notification.NotificationRepository;
import com.studyforyou.modules.notification.NotificationType;
import com.studyforyou.modules.study.Study;
import com.studyforyou.modules.study.StudyCreatedEvent;
import com.studyforyou.modules.study.StudyRepository;
import com.studyforyou.modules.study.StudyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
                sendCreatedStudyEmail(study, account, "새로운 스터디가 생겼습니다","스터디 포유 '"+ study.getTitle()+ "' 이 생겼습니다.");
            }

            if (account.isStudyCreatedByWeb()) {
                createNotification(study, account ,study.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        });

    }
    @EventListener
    public void handleStudyUpdateEvent(StudyUpdatedEvent StudyUpdatedEvent) {
        Study study = studyRepository.findMembersWithManagersById(StudyUpdatedEvent.getStudy().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        String message = StudyUpdatedEvent.getMessage();

        accounts.forEach(account -> {
            if (account.isStudyUpdatedByEmail()) {
                sendCreatedStudyEmail(study, account, message,"스터디 포유 '"+ study.getTitle()+ "' 에 새로운 소식이 있습니다.");
            }

            if (account.isStudyUpdatedByWeb()) {
                createNotification(study, account, message, NotificationType.STUDY_UPDATED);
            }
        });
    }

    @EventListener
    public void handleEventUpdateEvent(StudyEventUpdatedEvent studyEventUpdatedEvent) {

        Enrollment enrollment = studyEventUpdatedEvent.getEnrollment();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();
        Account account = enrollment.getAccount();
        String message = studyEventUpdatedEvent.getMessage();


            if (account.isStudyEnrollmentResultByEmail()) {
                sendCreatedStudyEmail(study, account, message,"스터디 포유 '"+ study.getTitle()+ "' "+event.getTitle()+" 모임 신청 결과 입니다.");
            }

            if (account.isStudyEnrollmentResultByWeb()) {
                createNotification(study, account, message, NotificationType.EVENT_ENROLLMENT);
            }
    }

    private void createNotification(Study study, Account account, String description, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setTitle(study.getTitle());
        notification.setLink("/study/"+ study.getEncodedPath());
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(description);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendCreatedStudyEmail(Study study, Account account,String description,String subject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", description);
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(subject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

}


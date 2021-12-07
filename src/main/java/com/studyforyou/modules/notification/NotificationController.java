package com.studyforyou.modules.notification;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GetMapping("/notifications")
    public String NotificationsView(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);

        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
        long count = notificationRepository.countByAccountAndChecked(account, true);


        setNotificationAttribute(model, notifications);


        model.addAttribute("numberOfNotChecked", notifications.size());
        model.addAttribute("numberOfChecked", count);
        model.addAttribute("isNew", true);

        notificationService.checkNotification(notifications);

        return "notification/list";
    }

    @GetMapping("/notifications/old")
    public String NotificationsOldView(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);

        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
        long count = notificationRepository.countByAccountAndChecked(account, false);


        setNotificationAttribute(model, notifications);


        model.addAttribute("numberOfNotChecked", count);
        model.addAttribute("numberOfChecked", notifications.size());
        model.addAttribute("isNew", false);

        return "notification/list";
    }

    @DeleteMapping("/notifications")
    public String deleteNotifications(@CurrentAccount Account account) {
        notificationRepository.deleteAllByAccountAndChecked(account,true);
        return "redirect:/notifications";
    }

    private void setNotificationAttribute(Model model, List<Notification> notifications) {
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotifications = new ArrayList<>();

        for (Notification notification : notifications) {
            switch (notification.getNotificationType()) {
                case STUDY_CREATED:
                    newStudyNotifications.add(notification);
                    break;
                case STUDY_UPDATED:
                    watchingStudyNotifications.add(notification);
                    break;
                case EVENT_ENROLLMENT:
                    eventEnrollmentNotifications.add(notification);
                    break;
            }
        }
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
    }
}

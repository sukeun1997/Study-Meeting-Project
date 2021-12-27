package com.studyforyou_retry.modules.notification;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
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

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    private String notificationList(@CurrentAccount Account account, Model model) {

        List<Notification> notifications = notificationRepository.findByToAndCheckedOrderByCreatedDateTime(account, false);
        long count = notificationRepository.countByToAndChecked(account, true);

        setAttributeValue(model, notifications);

        model.addAttribute("isNew", true);
        model.addAttribute("numberOfNotChecked", notifications.size());
        model.addAttribute("numberOfChecked", count);

        notificationService.readNotification(notifications);

        return "notification/list";
    }

    @GetMapping("/notifications/old")
    private String notificationListOld(@CurrentAccount Account account, Model model) {

        List<Notification> notifications = notificationRepository.findByToAndCheckedOrderByCreatedDateTime(account, true);
        long count = notificationRepository.countByToAndChecked(account, false);

        setAttributeValue(model, notifications);

        model.addAttribute("isNew", false);
        model.addAttribute("numberOfNotChecked", count);
        model.addAttribute("numberOfChecked", notifications.size());

        return "notification/list";
    }

    @DeleteMapping("/notifications")
    private String deleteReadNotification(@CurrentAccount Account account) {

        notificationService.deleteReadNotification(account);

        return "redirect:/notifications";
    }

    private void setAttributeValue(Model model, List<Notification> notifications) {
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotifications = new ArrayList<>();
        notifications.stream().forEach(notification -> {
            if (notification.getNotificationType() == NotificationType.NEW_STUDY) {
                newStudyNotifications.add(notification);
            }
            if (notification.getNotificationType() == NotificationType.EVENT) {
                eventEnrollmentNotifications.add(notification);
            }
            if (notification.getNotificationType() == NotificationType.JOIN_STUDY) {
                watchingStudyNotifications.add(notification);
            }
        });
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
    }

}

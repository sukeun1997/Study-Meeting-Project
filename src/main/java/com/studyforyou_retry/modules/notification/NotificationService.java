package com.studyforyou_retry.modules.notification;

import com.studyforyou_retry.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public void readNotification(List<Notification> notifications) {
        notifications.stream().forEach(notification -> notification.setChecked(true));
    }

    public void deleteReadNotification(Account account) {
        List<Notification> notifications = notificationRepository.findByToAndCheckedOrderByCreatedDateTime(account, true);
        notificationRepository.deleteAll(notifications);
    }
}

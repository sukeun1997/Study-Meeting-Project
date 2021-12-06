package com.studyforyou.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void checkNotification(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setChecked(true));
        notificationRepository.saveAll(notifications);
    }
}

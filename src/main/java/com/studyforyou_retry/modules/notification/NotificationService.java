package com.studyforyou_retry.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {


    public void readNotification(List<Notification> notifications) {
        notifications.stream().forEach(notification -> notification.setChecked(true));
    }
}

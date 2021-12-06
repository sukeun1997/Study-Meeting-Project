package com.studyforyou.modules.notification;

import com.studyforyou.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByAccountAndChecked(Account account, boolean checked);

    List<Notification> findByAccountAndCheckedOrderByCreatedDateTime(Account account, boolean checked);

    @Transactional
    void deleteAllByAccountAndChecked(Account account, boolean checked);
}

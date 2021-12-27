package com.studyforyou_retry.modules.notification;

import com.studyforyou_retry.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    boolean existsByToAndChecked(Account account, boolean checked);

    long countByToAndChecked(Account account, boolean checked);

}

package com.studyforyou_retry.modules.notification;

import com.studyforyou_retry.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @Builder
@EqualsAndHashCode(of = "id") @AllArgsConstructor @NoArgsConstructor
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private LocalDateTime createdDateTime;

    private boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account to;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;


}

package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @OneToOne
    private Account account;

    private boolean accepted;
    private boolean attended;
    private LocalDateTime enrolledAt;

}

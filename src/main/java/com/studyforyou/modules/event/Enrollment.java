package com.studyforyou.modules.event;

import com.studyforyou.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER) // TODO LAZY , EAGER 차이 비교하기
    private Account account;

    private LocalDateTime enrolledAt; // 신청 시간

    private boolean accepted; // 확정 여부

    private boolean attended; // 참석 여부부
}

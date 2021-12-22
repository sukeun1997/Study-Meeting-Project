package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import lombok.*;

import javax.persistence.*;

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

    @OneToOne(fetch = FetchType.LAZY)
    private Account account;
}

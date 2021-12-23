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


    private boolean accepted;
    private boolean attended;
    private LocalDateTime enrolledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @OneToOne
    private Account account;

    public static Enrollment createEnrollment(Account account, Event event) {
        Enrollment enrollment = new Enrollment();
        enrollment.account = account;
        enrollment.event = event;
        enrollment.enrolledAt = LocalDateTime.now();
        enrollment.accepted = event.isAcceptableFCFS(enrollment);
        return enrollment;
    }

    public void rejectEnroll() {
        this.accepted = false;
    }

    public void acceptEnroll() {
        this.accepted = true;
    }

    public boolean isAttend() {
        return accepted && attended;
    }

    public void checkin() {
        attended = true;
    }

    public void cancelCheckin() {
        attended = false;
    }
}

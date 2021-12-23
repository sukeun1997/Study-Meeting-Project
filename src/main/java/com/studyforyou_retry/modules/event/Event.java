package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.UserAccount;
import com.studyforyou_retry.modules.study.Study;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private int limitOfEnrollments;

    @Lob
    private String files;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @OneToOne
    private Account createdBy;

    @OneToOne(fetch = FetchType.LAZY)
    private Study study;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private Set<Enrollment> enrollments = new HashSet<>();

    public void createEvent(Account account, Study study) {
        this.createdBy = account;
        this.study = study;
        this.createdDateTime = LocalDateTime.now();
    }

    public int remainOfEnrollments() {
        return (int) (limitOfEnrollments - enrollments.stream().filter(Enrollment::isAccepted).count());
    }

    private boolean canEnrollTime() {
        return LocalDateTime.now().isBefore(endEnrollmentDateTime)
                && startDateTime.isBefore(LocalDateTime.now())
                && endDateTime.isAfter(LocalDateTime.now());
    }


    public boolean isEnrollableFor(UserAccount userAccount) {
        return !isEnrollment(userAccount) && canEnrollTime() && !isAttended(userAccount);
    }

    public boolean isDisenrollableFor(UserAccount userAccount) {
        return isEnrollment(userAccount) && startDateTime.isBefore(LocalDateTime.now())
                && endDateTime.isAfter(LocalDateTime.now()) && !isAttended(userAccount);
    }

    public boolean isAttended(UserAccount userAccount) {

        Optional<Enrollment> optional = enrollments.stream().filter(enrollment -> enrollment.getAccount().equals(userAccount.getAccount())).findAny();

        if (optional.isPresent()) {
            return optional.get().isAttended();
        }
        return false;
    }

    public boolean isAcceptable(Enrollment enrollment) {
        return !enrollment.isAccepted() && canEnrollTime() && remainOfEnrollments() > 0 && !enrollment.isAttended();
    }

    public boolean isAcceptableFCFS(Enrollment enrollment) {
        return !enrollment.isAccepted() && canEnrollTime() && remainOfEnrollments() > 0 && isFCFS() && !enrollment.isAttended();
    }

    private boolean isFCFS() {
        return eventType.equals(EventType.FCFS);
    }

    public boolean isRejectable(Enrollment enrollment) {
        return enrollment.isAccepted() && canEnrollTime() && !enrollment.isAttended();
    }

    private boolean isEnrollment(UserAccount userAccount) {
        return enrollments.stream().map(Enrollment::getAccount).anyMatch(account -> account.equals(userAccount.getAccount()));
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }

}

package com.studyforyou.domain;


import com.studyforyou.account.UserAccount;
import com.studyforyou.constant.EventType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NamedEntityGraph(name = "eventJoin", attributeNodes = {
        @NamedAttributeNode("enrollments")
})


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event",cascade = CascadeType.REMOVE)
    private List<Enrollment> enrollments = new ArrayList<>();

    public boolean isEnrollableFor(UserAccount userAccount) {

        if (!isNotEndEnrollmentTime() && !isEnrollment(userAccount)) {
            return true;
        }
        return false;
    }

    public boolean isDisenrollableFor(UserAccount userAccount) {

        if (!isNotEndEnrollmentTime() && isEnrollment(userAccount)) {
            return true;
        }
        return false;
    }

    public boolean isAttended(UserAccount userAccount) {

        Optional<Enrollment> optional = enrollments.stream().filter(enrollment -> enrollment.containAccount(userAccount.getAccount())).findAny();

        if (optional.isPresent()) {
            return optional.get().isAttended();
        }
        return false;
    }

    private boolean isNotEndEnrollmentTime() {
        return endEnrollmentDateTime.isBefore(LocalDateTime.now());
    }

    private boolean isEnrollment(UserAccount userAccount) {
        return enrollments.stream().anyMatch(enrollment -> enrollment.containAccount(userAccount.getAccount()));
    }

    public int numberOfRemainSpots() {
        return limitOfEnrollments - (int) getAcceptedCount();
    }

    public long getAcceptedCount() {
        return enrollments.stream().map(Enrollment::isAccepted).count();
    }

    public boolean isAcceptable(Enrollment enrollment) {
        return !enrollment.isAccepted();
    }

    public boolean isRejectable(Enrollment enrollment) {
        return enrollment.isAccepted();
    }

    public boolean isAbleToAccept() {
        return getEventType() == EventType.FCFS && numberOfRemainSpots() > 0;
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollment.setEvent(this);
        enrollments.add(enrollment);
    }

}

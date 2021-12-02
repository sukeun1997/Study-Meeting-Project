package com.studyforyou.modules.event;


import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.UserAccount;
import com.studyforyou.modules.study.Study;
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
@Builder @NoArgsConstructor @AllArgsConstructor
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
    @OrderBy(value = "enrolledAt")
    private List<Enrollment> enrollments = new ArrayList<>();

    public boolean isEnrollableFor(UserAccount userAccount) {

        if (!isNotEndEnrollmentTime() && !isEnrollment(userAccount) && !isAttended(userAccount)) {
            return true;
        }
        return false;
    }

    public boolean isDisenrollableFor(UserAccount userAccount) {

        if (!isNotEndEnrollmentTime() && isEnrollment(userAccount) && !isAttended(userAccount)) {
            return true;
        }
        return false;
    }

    public boolean isAttended(UserAccount userAccount) {

        Optional<Enrollment> optional = enrollments.stream().filter(enrollment -> enrollment.getAccount().equals(userAccount.getAccount())).findAny();

        if (optional.isPresent()) {
            return optional.get().isAttended();
        }
        return false;
    }

    private boolean isNotEndEnrollmentTime() {
        return endEnrollmentDateTime.isBefore(LocalDateTime.now());
    }

    private boolean isEnrollment(UserAccount userAccount) {
        return enrollments.stream().anyMatch(enrollment -> enrollment.getAccount().equals(userAccount.getAccount()));
    }

    public int numberOfRemainSpots() {
        return limitOfEnrollments - (int) getAcceptedCount();
    }

    public long getAcceptedCount() {
        return enrollments.stream().filter(enrollment -> enrollment.isAccepted()).count();
    }

    public boolean isAcceptable(Enrollment enrollment) {
        return !enrollment.isAccepted() && numberOfRemainSpots() > 0;
    }

    public boolean isRejectable(Enrollment enrollment) {
        return enrollment.isAccepted() && !enrollment.isAttended();
    }

    public boolean isAbleToAcceptFCFS() {
        return getEventType() == EventType.FCFS && numberOfRemainSpots() > 0;
    }
    public boolean isAbleToAcceptCONFIMATIVE() {
        return getEventType() == EventType.CONFIRMATIVE && numberOfRemainSpots() > 0;
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollment.setEvent(this);
        enrollments.add(enrollment);
    }


    public List<Enrollment> getWaitingList() {
        return enrollments.stream().filter(enrollment -> !enrollment.isAccepted()).collect(Collectors.toList());
    }

    public void acceptWaitingEnrollment() {

        if (isAbleToAcceptFCFS()) {
            List<Enrollment> waitingList = getWaitingList();
            int count = Math.min(numberOfRemainSpots(), waitingList.size());
            waitingList.subList(0, count).forEach(enrollment -> enrollment.setAccepted(true));
        }

    }

    public void acceptNextWaitingEnrollment() {
        if (isAbleToAcceptFCFS()) {
            if (!getWaitingList().isEmpty()) {
                Enrollment enrollment = getWaitingList().get(0);
                enrollment.setAccepted(true);
            }
        }
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }


    public void acceptEnrollment(Enrollment enrollment) {
        if (isAbleToAcceptCONFIMATIVE()) {
            enrollment.setAccepted(true);
        }
    }

    public void rejectEnrollment(Enrollment enrollment) {
        if (eventType == EventType.CONFIRMATIVE) {
            enrollment.setAccepted(false);
        }
    }

    public void checkinEnrollment(Enrollment enrollment) {
        if (canAttended(enrollment)) {
            enrollment.setAttended(true);
        }
    }
    public void checkoutEnrollment(Enrollment enrollment) {
        if (canDisAttended(enrollment)) {
            enrollment.setAttended(false);
        }
    }

    private boolean canDisAttended(Enrollment enrollment) {
        return enrollment.isAttended() && enrollment.isAccepted();
    }

    private boolean canAttended(Enrollment enrollment) {
        return !enrollment.isAttended() && enrollment.isAccepted();
    }
}

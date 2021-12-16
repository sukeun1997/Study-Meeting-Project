package com.studyforyou_retry.modules.account;

import com.studyforyou_retry.modules.account.setting.Notifications;
import com.studyforyou_retry.modules.account.setting.Profile;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @Builder @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    private String bio;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private boolean emailVerified;

    private LocalDateTime joinedAt;

    private String location;

    private String occupation;

    @Lob
    private String profileImage;

    @Column(nullable = false)
    private String password;

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;
    private String url;


    public void GenerateCheckToken() {

        String uuid = UUID.randomUUID().toString();
        this.emailCheckToken = uuid;
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void verifiedEmailToken() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean canResendEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public void updateProfile(Profile profile) {
        this.bio = profile.getBio();
        this.url = profile.getUrl();
        this.location = profile.getLocation();
        this.occupation = profile.getOccupation();
        this.profileImage = profile.getProfileImage();

    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNotifications(Notifications notifications) {
        this.studyCreatedByEmail = notifications.isStudyCreatedByEmail();
        this.studyCreatedByWeb = notifications.isStudyCreatedByWeb();
        this.studyEnrollmentResultByEmail = notifications.isStudyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = notifications.isStudyEnrollmentResultByWeb();
        this.studyUpdatedByEmail= notifications.isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = notifications.isStudyUpdatedByWeb();
    }
}

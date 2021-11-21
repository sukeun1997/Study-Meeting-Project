package com.studyforyou.domain;

import com.studyforyou.settings.Notifications;
import com.studyforyou.settings.Profile;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.ui.ModelMap;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    private String bio; // 소개

    private String url;

    private String occupation;

    private String location;

    @Lob // Text 로 입력가능
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token, Account account) {
       return account.getEmailCheckToken().equals(token);
    }

    public boolean canResendEmail() {
        return this.getEmailCheckTokenGeneratedAt().isBefore(LocalDateTime.now().minusHours(1));
    }


    public void profileUpdate(Profile profile) {
        this.bio = profile.getBio();
        this.occupation = profile.getOccupation();
        this.url = profile.getUrl();
        this.location = profile.getLocation();
        this.profileImage = profile.getProfileImage();
    }

    public void notificationsUpdate(Notifications notifications) {
        this.studyCreatedByEmail = notifications.isStudyCreatedByEmail();
        this.studyCreatedByWeb = notifications.isStudyCreatedByWeb();
        this.studyEnrollmentResultByEmail = notifications.isStudyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = notifications.isStudyEnrollmentResultByWeb();
        this.studyUpdatedByEmail = notifications.isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = notifications.isStudyUpdatedByWeb();
    }

    public boolean canSendConfirmEmail() {
        return this.getEmailCheckTokenGeneratedAt().isBefore(LocalDateTime.now().minusHours(1));
    }
}

package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.UserAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.zones.Zone;
import lombok.*;

import javax.persistence.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor @Builder
public class Study {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String path;
    private String shortDescription;
    private String fullDescription;
    private boolean useBanner;
    private boolean published;
    private boolean closed;
    private boolean recruiting;


    private LocalDateTime closedDateTime;

    private LocalDateTime recruitDateTime;
    @Lob
    private String image;


    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    public void addManagers(Account account) {
        managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        return published && !closed && recruiting && !members.contains(userAccount.getAccount())
                && !managers.contains(userAccount.getAccount());

    }

    public boolean isMember(UserAccount userAccount) {
        return members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return managers.contains(userAccount.getAccount());
    }

    public void updateDescription(StudyDescriptionForm studyDescriptionForm) {
        this.shortDescription = studyDescriptionForm.getShortDescription();
        this.fullDescription = studyDescriptionForm.getFullDescription();
    }

    public String getEncodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    public void updateBanner(String image) {
        this.image = image;
    }

    public void enableBanner() {
        this.useBanner = true;
    }

    public void disableBanner() {
        this.useBanner = false;
    }

    public void addTags(Tag tag) {
        this.getTags().add(tag);
    }

    public void removeTags(Tag tag) {
        this.getTags().remove(tag);
    }

    public void addZones(Zone zone) {
        getZones().add(zone);
    }

    public void removeZones(Zone zone) {
        getZones().remove(zone);
    }


    public boolean isRemovable() {
        return !published && closed && !recruiting;
        //todo 모임 했던 여부 시간으로 체크
    }

    public void publish() {
        if (canPublish()) {
            published = true;
            closed = false;
        }
    }

    private boolean canPublish() {
        return !published && !closed;
    }

    public void close() {
        if (canClose()) {
            published = false;
            closed = true;
        }
    }

    private boolean canClose() {
        return published && !closed;
    }

    public void updatePath(String newPath) {
        this.path = newPath;
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public void recruitStart() {
        if (!closed && published && !recruiting) {
            this.recruiting = true;
            this.recruitDateTime = LocalDateTime.now();
        }
    }

    public void recruitStop() {
        if (!closed && published && recruiting) {
            this.recruiting = false;
            recruitDateTime = LocalDateTime.now();
        }
    }

    public void joinStudy(Account account) {
        if (isJoinable(new UserAccount(account))) {
            getMembers().add(account);
        }
    }

    public void leaveStudy(Account account) {
        if (canLeave(account)) {
            getMembers().remove(account);
        }
    }

    private boolean canLeave(Account account) {
        return members.contains(account) && !closed;
    }

}

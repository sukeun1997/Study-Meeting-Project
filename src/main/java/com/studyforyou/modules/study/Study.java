package com.studyforyou.modules.study;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.UserAccount;
import com.studyforyou.modules.tag.Tag;
import com.studyforyou.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@NamedEntityGraph(name = "studyAllGraph", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("members"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("zones")
})


@NamedEntityGraph(name = "studyTagsGraph", attributeNodes = {
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("tags")
})

@NamedEntityGraph(name = "studyZonesGraph", attributeNodes = {
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("zones")
})

@NamedEntityGraph(name = "studyManagersGraph", attributeNodes = {
        @NamedAttributeNode("managers")
})

@NamedEntityGraph(name = "studyMembersGraph", attributeNodes = {
        @NamedAttributeNode("members")
})


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Study {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;


    private boolean useBanner;

    private int memberCount;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> managers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> members = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Zone> zones = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    public void addMangers(Account byNickname) {
        this.managers.add(byNickname);
    }

    public boolean isJoinable(UserAccount account) {
        return this.published && this.recruiting &&
                !this.managers.contains(account.getAccount()) && !this.members.contains(account.getAccount());
    }

    public boolean isMember(UserAccount account) {
        return this.members.contains(account.getAccount());
    }

    public boolean isManager(UserAccount account) {
        return this.managers.contains(account.getAccount());
    }


    public boolean isRemovable() {
        return !this.isPublished(); // TODO 모임 여부도 추가 해야함
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
        memberCount--;
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
        memberCount++;
    }

    public String getEncodedPath() {
        return URLEncoder.encode(getPath(), StandardCharsets.UTF_8);
    }

    public boolean isManagedBy(Account account) {
        return getManagers().contains(account);
    }
}



package com.studyforyou.domain;

import com.studyforyou.account.UserAccount;
import lombok.*;

import javax.persistence.*;
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
                !this.managers.contains(account) && !this.members.contains(account);
    }

    public boolean isMember(UserAccount account) {
        return this.members.contains(account.getAccount());
    }

    public boolean isManager(UserAccount account) {
        return this.managers.contains(account.getAccount());
    }

}



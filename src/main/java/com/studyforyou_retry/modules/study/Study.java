package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.UserAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.zones.Zone;
import lombok.*;

import javax.persistence.*;
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
        return published && !closed && recruiting && !members.contains(userAccount.getAccount());
    }

    public boolean isMember(UserAccount userAccount) {
        return members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return managers.contains(userAccount.getAccount());
    }

}

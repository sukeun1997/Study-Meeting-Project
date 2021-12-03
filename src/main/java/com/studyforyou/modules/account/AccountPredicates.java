package com.studyforyou.modules.account;

import com.querydsl.core.types.Predicate;
import com.studyforyou.modules.tag.Tag;
import com.studyforyou.modules.zone.Zone;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findTagsWithZonesById(Set<Tag> tags , Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.tags.any().in(tags).and(account.zones.any().in(zones));
    }
}

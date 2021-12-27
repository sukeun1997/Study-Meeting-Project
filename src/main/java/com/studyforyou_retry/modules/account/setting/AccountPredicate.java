package com.studyforyou_retry.modules.account.setting;

import com.querydsl.core.types.Predicate;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.QAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.zones.Zone;

import java.util.Set;


public class AccountPredicate {

    public static Predicate findAccountWithTagsAndZones(Set<Tag> tag, Set<Zone> zone) {
        return QAccount.account.tags.any().in(tag).and(QAccount.account.zones.any().in(zone));
    }
}

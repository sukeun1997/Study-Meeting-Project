package com.studyforyou_retry.modules.account;

import com.studyforyou_retry.modules.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account,Long> {

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Account findByEmail(String email);

    Account findByNickname(String nickName);

    long countByEmailVerified(boolean verified);

}

package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment ,Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);

    @EntityGraph(attributePaths = {"event","event.study"})
    Set<Enrollment> findFirst4ByAccountOrderByEnrolledAtDesc(Account account);
}

package com.studyforyou.modules.event;

import com.studyforyou.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);

    @EntityGraph(value = "enrollmentEventAndStudySubGraph")
    List<Enrollment> findEnrollmentWithEventAndStudyByAccepted(boolean accepted);
}

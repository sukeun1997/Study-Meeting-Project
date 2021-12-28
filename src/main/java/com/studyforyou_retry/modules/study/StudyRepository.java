package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study,Long> , StudyCustomRepository {

    @EntityGraph(attributePaths = {"tags","members","zones","managers"})
    Study findStudyWithAllByPath(String path);

    @EntityGraph(attributePaths = {"managers"}, type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithManagerByPath(String path);

    @EntityGraph(attributePaths = {"managers","tags"})
    Study findStudyWithManagerAndTagsByPath(String path);

    @EntityGraph(attributePaths = {"managers","zones"})
    Study findStudyWithManagerAndZonesByPath(String path);

    boolean existsByPath(String newPath);

    boolean existsByTitle(String newTitle);

    Study findByPath(String path);

    @EntityGraph(attributePaths = {"tags","zones"})
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members","managers"})
    Study findStudyWithMembersAndManagersById(Long id);

    @EntityGraph(attributePaths = {"tags","zones"})
    Set<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    @EntityGraph(attributePaths = {"managers"})
    Set<Study> findFirst10ByManagersContainingOrderByPublishedDateTimeDesc(Account account);

    @EntityGraph(attributePaths = {"members"})
    Set<Study> findFirst10ByMembersContainingOrderByPublishedDateTimeDesc(Account account);
}

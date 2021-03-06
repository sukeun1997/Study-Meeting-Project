package com.studyforyou.modules.study;

import com.studyforyou.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {


    boolean existsByPath(String path);

    @EntityGraph(value = "studyAllGraph", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "studyTagsGraph", type = EntityGraph.EntityGraphType.FETCH)
    Study findAccountWithTagsByPath(String path);

    @EntityGraph(value = "studyZonesGraph", type = EntityGraph.EntityGraphType.FETCH)
    Study findAccountWithZonesByPath(String path);

    @EntityGraph(value = "studyManagersGraph", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithMangersByPath(String path);

    @EntityGraph(value = "studyMembersGraph", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithMembersByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"}, type = EntityGraph.EntityGraphType.FETCH)
    Study findZonesWithTagsById(Long Id);

    @EntityGraph(attributePaths = {"managers","members"}, type = EntityGraph.EntityGraphType.FETCH)
    Study findMembersWithManagersById(Long id);

    Study findOnlyByPath(String path);

    List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTime(Account account,boolean closed);
    List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTime(Account account,boolean closed);

}

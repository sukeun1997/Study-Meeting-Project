package com.studyforyou.repository;

import com.studyforyou.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {

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
}

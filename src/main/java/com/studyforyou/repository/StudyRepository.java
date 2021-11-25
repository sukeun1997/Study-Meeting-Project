package com.studyforyou.repository;

import com.studyforyou.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByPath(String path);

    @EntityGraph(value = "studyGraph", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "studyTagsGraph", type = EntityGraph.EntityGraphType.LOAD)
    Study findAccountWithTagsByPath(String path);

    @EntityGraph(value = "studyZonesGraph", type = EntityGraph.EntityGraphType.LOAD)
    Study findAccountWithZonesByPath(String path);
}

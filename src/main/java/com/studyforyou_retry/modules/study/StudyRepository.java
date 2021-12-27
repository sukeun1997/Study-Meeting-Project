package com.studyforyou_retry.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study,Long> {

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
}

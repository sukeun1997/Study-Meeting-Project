package com.studyforyou_retry.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study,Long> {

    @EntityGraph(attributePaths = {"tags","members","zones","managers"})
    Study findStudyWithAllByPath(String path);

    @EntityGraph(attributePaths = {"managers"})
    Study findStudyWithManagerByPath(String path);

    @EntityGraph(attributePaths = {"managers","tags"})
    Study findStudyWithManagerAndTagsByPath(String path);

    @EntityGraph(attributePaths = {"managers","zones"})
    Study findStudyWithManagerAndZonesByPath(String path);
}

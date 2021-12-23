package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.study.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = {"enrollments"},type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findEventByStudyOrderByCreatedDateTime(Study study);


}

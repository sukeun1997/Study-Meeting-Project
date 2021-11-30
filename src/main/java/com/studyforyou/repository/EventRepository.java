package com.studyforyou.repository;

import com.studyforyou.domain.Event;
import com.studyforyou.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {


    @EntityGraph(value = "eventJoin", type = EntityGraph.EntityGraphType.LOAD)
    Set<Event> findByStudyOrderByStartDateTime(Study study);
}

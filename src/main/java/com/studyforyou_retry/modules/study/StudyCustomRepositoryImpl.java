package com.studyforyou_retry.modules.study;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyforyou_retry.modules.tags.QTag;
import com.studyforyou_retry.modules.zones.QZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

public class StudyCustomRepositoryImpl implements StudyCustomRepository {

    private JPAQueryFactory queryFactory;

    public StudyCustomRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;
        QTag tag = QTag.tag;
        QZone zone = QZone.zone;
        QueryResults<Study> results = queryFactory.selectFrom(study)
                .where(study.published.isTrue().and(study.closed.isFalse())
                        .and(study.title.containsIgnoreCase(keyword)
                                .or(study.tags.any().title.containsIgnoreCase(keyword))))
                .leftJoin(study.tags, tag).fetchJoin()
                .leftJoin(study.zones, zone).fetchJoin()
                .orderBy(study.publishedDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Study> studyList = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(studyList, pageable, total);
    }
}

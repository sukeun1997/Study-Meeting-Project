package com.studyforyou.modules.study;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyforyou.modules.account.QAccount;
import com.studyforyou.modules.tag.QTag;
import com.studyforyou.modules.zone.QZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {

    private final JPAQueryFactory jpaQueryFactory;

    public StudyRepositoryExtensionImpl(EntityManager entityManager) {
        super(Study.class);
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;
        QZone qZone = QZone.zone;
        QAccount qAccount = QAccount.account;
        QTag qTag = QTag.tag;

        QueryResults<Study> query = jpaQueryFactory.selectFrom(study)
                .where(study.published.isTrue()
                        .and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(study.members ,qAccount).fetchJoin()
                .leftJoin(study.tags, qTag).fetchJoin()
                .leftJoin(study.zones, qZone).fetchJoin()
                .distinct()
                .orderBy(pageable.getSort().toString().contains("publishedDateTime") ? study.publishedDateTime.desc() : study.memberCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();


        List<Study> content = query.getResults();

        return new PageImpl<>(content,pageable,query.getTotal());
    }

}

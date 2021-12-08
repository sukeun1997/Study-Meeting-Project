package com.studyforyou.modules.study;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyforyou.modules.account.QAccount;
import com.studyforyou.modules.tag.QTag;
import com.studyforyou.modules.tag.Tag;
import com.studyforyou.modules.zone.QZone;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;

public class StudyRepositoryExtensionImpl implements StudyRepositoryExtension {

    private final JPAQueryFactory jpaQueryFactory;

    public StudyRepositoryExtensionImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Study> findByKeyword(String keyword) {
        QStudy study = QStudy.study;
        QZone qZone = QZone.zone;
        QAccount qAccount = QAccount.account;
        QTag qTag = QTag.tag;

        List<Study> query = jpaQueryFactory.selectFrom(study)
                .where(study.published.isTrue()
                        .and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(study.members ,qAccount).fetchJoin()
                .leftJoin(study.tags, qTag).fetchJoin()
                .leftJoin(study.zones, qZone).fetchJoin()
                .distinct()
                .fetch();

        return query;
    }

}

package com.studyforyou_retry.modules.study;


import com.studyforyou_retry.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Component
@Transactional
@RequiredArgsConstructor
public class StudyFactory {

    private final StudyRepository studyRepository;

    public Study createStudy(Account account,String name) {

        Study build = Study.builder().path(name).title(name)
                .managers(new HashSet<>())
                .members(new HashSet<>())
                .tags(new HashSet<>())
                .zones(new HashSet<>()).build();


        build.getManagers().add(account);

        return studyRepository.save(build);
    }

}

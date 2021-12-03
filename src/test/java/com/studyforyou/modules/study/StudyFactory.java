package com.studyforyou.modules.study;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyService studyService;
    @Autowired
    StudyRepository studyRepository;

    public Study createStudy() {
        StudyForm studyForm = new StudyForm();
        studyForm.setTitle("테스트");
        studyForm.setShortDescription("테스트");
        studyForm.setPath("테스트1");
        studyForm.setFullDescription("테스트");

        Account account = accountRepository.findByNickname("test");
        studyService.newStudy(account, studyForm);

        return studyRepository.findByPath(studyForm.getPath());
    }
}

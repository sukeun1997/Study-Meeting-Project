package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public void createStudy(Account account, StudyForm studyForm) {
        Study study = modelMapper.map(studyForm, Study.class);
        study.addManagers(account);
        studyRepository.save(study);
    }


}

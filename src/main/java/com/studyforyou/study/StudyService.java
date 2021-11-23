package com.studyforyou.study;

import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.StudyForm;
import com.studyforyou.repository.AccountRepository;
import com.studyforyou.repository.StudyRepository;
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

    public void newStudy(Account account, StudyForm studyForm) {
        Account byNickname = accountRepository.findByNickname(account.getNickname());

        Study study = modelMapper.map(studyForm, Study.class);
        study.addMangers(byNickname);
        studyRepository.save(study);
    }
}

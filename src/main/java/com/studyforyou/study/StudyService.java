package com.studyforyou.study;

import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.StudyDescriptionForm;
import com.studyforyou.dto.StudyForm;
import com.studyforyou.repository.AccountRepository;
import com.studyforyou.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
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

    public void updateDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
    }

    @Transactional(readOnly = true)
    public Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);

        if (study == null) {
            throw new IllegalArgumentException(study.getPath() + "에 해당하는 모임은 없습니다.");
        }
        return study;
    }

    @Transactional(readOnly = true)
    public Study getUpdateStudy(Account account, String path) {
        Study study = this.getStudy(path);

        if (!account.isManager(study)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        return study;
    }
}

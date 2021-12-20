package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.UserAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

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


    public Study getStudyWithManagers(Account account, String path) {

        Study study = this.getStudy(path);
        isNotManager(account, study);
        return study;
    }

    private void isNotManager(Account account, Study study) {
        if (!study.isManager(new UserAccount(account))) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    private void isExistStudy(Study study) {
        if (study == null) {
            throw new EntityNotFoundException("해당 스터디가 없습니다.");
        }
    }

    public void updateDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        study.updateDescription(studyDescriptionForm);
    }

    public Study getStudy(String path) {
        Study study = studyRepository.findStudyWithAllByPath(path);
        isExistStudy(study);
        return study;
    }

    public void updateBanner(Study study, String image) {
        study.updateBanner(image);
    }

    public void enableBanner(Study study) {
        study.enableBanner();
    }

    public void disableBanner(Study study) {
        study.disableBanner();
    }
}

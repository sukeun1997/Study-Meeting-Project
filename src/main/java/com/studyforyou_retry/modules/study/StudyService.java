package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.UserAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.zones.Zone;
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


    public Study getStudyManagers(Account account, String path) {

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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Study getStudyWithManagersAndTags(Account account, String path) {

        Study study = studyRepository.findStudyWithManagerAndTagsByPath(path);
        isNotManager(account, study);
        return study;
    }

    public void addTag(Study study, Tag tag) {
        study.addTags(tag);
    }

    public void removeTags(Study study, Tag tag) {
        study.removeTags(tag);
    }

    @Transactional(readOnly = true)
    public Study getStudyWithManagersAndZones(Account account, String path) {
        Study study = studyRepository.findStudyWithManagerAndZonesByPath(path);
        isNotManager(account, study);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getStudyWithManagers(Account account, String path) {
        Study study = studyRepository.findStudyWithManagerByPath(path);
        isNotManager(account, study);
        return study;
    }

    public void addZones(Study study, Zone zone) {
        study.addZones(zone);
    }

    public void removeZones(Study study, Zone zone) {
        study.removeZones(zone);
    }

    public void publishStudy(Study study) {
        study.publish();
    }

    public void closeStudy(Study study) {
        study.close();
    }

    public void updatePath(Study study, String newPath) {
        study.updatePath(newPath);
    }

    public void updateTitle(Study study, String newTitle) {
        study.updateTitle(newTitle);

    }

    public void deleteStudy(Study study) {
        studyRepository.delete(study);
    }
}

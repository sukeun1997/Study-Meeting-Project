package com.studyforyou.study;

import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.domain.Tag;
import com.studyforyou.domain.Zone;
import com.studyforyou.dto.ImageForm;
import com.studyforyou.dto.StudyDescriptionForm;
import com.studyforyou.dto.StudyForm;
import com.studyforyou.dto.TagForm;
import com.studyforyou.repository.AccountRepository;
import com.studyforyou.repository.StudyRepository;
import com.studyforyou.repository.TagRepository;
import com.studyforyou.tag.TagService;
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
    private final TagRepository tagRepository;
    private final TagService tagService;

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

        checkStudyNull(study);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getUpdateStudy(Account account, String path) {
        Study study = this.getStudy(path);

        checkStudyManager(account, study);

        return study;
    }

    public void updateBanner(Study study, ImageForm imageForm) {
        modelMapper.map(imageForm, study);
    }

    public void enableBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableBanner(Study study) {
        study.setUseBanner(false);
    }

    @Transactional(readOnly = true)
    public Study getStudyWithTags(Account account, String path) {
        Study study = studyRepository.findAccountWithTagsByPath(path);

        checkStudyNull(study);
        checkStudyManager(account, study);
        return study;
    }
    @Transactional(readOnly = true)
    public Study getStudyWithZones(Account account, String path) {
        Study study = studyRepository.findAccountWithZonesByPath(path);
        checkStudyNull(study);
        checkStudyManager(account, study);
        return study;
    }

    public void addTags(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    private void checkStudyNull(Study study) {
        if (study == null) {
            throw new IllegalArgumentException(study.getPath() + "에 해당하는 모임은 없습니다.");
        }
    }

    private void checkStudyManager(Account account, Study study) {
        if (!account.isManager(study)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    public void RemoveTags(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    @Transactional(readOnly = true)
    public Study getStudyWithManagers(Account account, String path) {
        Study study = studyRepository.findStudyWithMangersByPath(path);

        checkStudyNull(study);
        checkStudyManager(account, study);

        return study;
    }

    public void studyPublish(Study study) {
        study.setPublished(true);
        study.setClosed(false);
    }

    public void studyClose(Study study) {
        study.setPublished(false);
        study.setClosed(true);
    }
}

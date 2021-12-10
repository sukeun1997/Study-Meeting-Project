package com.studyforyou.modules.study;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.UserAccount;
import com.studyforyou.modules.tag.Tag;
import com.studyforyou.modules.zone.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;


    public void newStudy(Account account, StudyForm studyForm) {
        Study study = modelMapper.map(studyForm, Study.class);
        study.addMangers(account);
        studyRepository.save(study);
    }

    public void updateDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdatedEvent(study,"스터디 소개가 수정되었습니다."));
    }

    @Transactional(readOnly = true)
    public Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);

        checkStudyNull(study);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getEventStudy(String path) {
        Study study = studyRepository.findStudyWithMangersByPath(path);

        checkStudyNull(study);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getOnlyStudyByPath(String path) {
        Study study = studyRepository.findOnlyByPath(path);

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
        if (!study.isManagedBy(account)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    public void removeTags(Study study, Tag tag) {
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

    @Transactional(readOnly = true)
    public Study getStudyWithMembers(String path) {
        Study study = studyRepository.findStudyWithMembersByPath(path);

        checkStudyNull(study);
        return study;
    }

    public void studyPublish(Study study) {

        if (!study.isClosed() && !study.isPublished()) {
            study.setPublished(true);
            study.setClosed(false);
            study.setPublishedDateTime(LocalDateTime.now());
        } else {
            throw new RuntimeException("스터디가 이미 공개 되었거나 종료 상태 입니다.");
        }
        eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    public void studyClose(Study study) {

        if (!study.isClosed() && study.isPublished()) {
            study.setPublished(false);
            study.setClosed(true);
            study.setClosedDateTime(LocalDateTime.now());
            eventPublisher.publishEvent(new StudyUpdatedEvent(study,"스터디가 종료 되었습니다."));
        } else {
            throw new RuntimeException("스터디가 이미 종료 되었거나 공개 상태가 아닙니다.");
        }
    }

    public void recruitStart(Study study) {
        study.setRecruiting(true);
        study.setRecruitingUpdatedDateTime(LocalDateTime.now());
        eventPublisher.publishEvent(new StudyUpdatedEvent(study,"가입된 스터디 팀원 모집이 시작 되었습니다."));
    }

    public void recruitStop(Study study) {
        study.setRecruiting(false);
        study.setRecruitingUpdatedDateTime(LocalDateTime.now());
        eventPublisher.publishEvent(new StudyUpdatedEvent(study,"가입된 스터디 팀원 모집이 종료 되었습니다."));
    }

    public boolean checkRecruitingTime(Study study) {

        if (study.getRecruitingUpdatedDateTime() == null) {
            return true;
        }
        return study.getRecruitingUpdatedDateTime().isBefore(LocalDateTime.now().minusHours(1));
    }

    public void updatePath(Study study, String newPath) {
        study.setPath(URLEncoder.encode(newPath, StandardCharsets.UTF_8));
    }

    public void updateTitle(Study study, String newTitle) {
        study.setTitle(newTitle);
    }

    public void removeStudy(Study study) {
        if (study.isRemovable()) {
            studyRepository.delete(study);
        } else {
            throw new RuntimeException("스터디가 공개중 이므로 삭제할 수 없습니다.");
        }
    }

    public void joinStudy(Account account, Study study) {

        if (study.isJoinable(new UserAccount(account))) {
            study.addMember(account);
        } else {
            throw new RuntimeException("해당 스터디에 가입할 수 없습니다. ");
        }
    }

    public void leaveStudy(Account account, Study study) {
        if (study.isMember(new UserAccount(account))) {
            study.removeMember(account);
        } else {
            throw new RuntimeException("해당 스터디의 맴버가 아닙니다.");
        }
    }
}

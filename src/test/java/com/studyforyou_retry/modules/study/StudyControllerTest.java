package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.infra.WithMockUser;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountFactory;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class StudyControllerTest {

    public static final String NEW_STUDY = "/new-study";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyFactory studyFactory;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    StudyService studyService;

    private Account account;
    private Study study;


    @BeforeEach
    private void beforeEach() {
        account = accountRepository.findByNickname("test");
        study = studyFactory.createStudy(account, "test");
    }

    @AfterEach
    private void afterEach() {
        studyRepository.deleteAll();
    }


    @Test
    @WithAccount("test")
    @DisplayName("스터디 개설 폼")
    void studyCreateForm() throws Exception {

        mockMvc.perform(get(NEW_STUDY))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(StudyController.STUDY_FORM));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 생성 -성공")
    void studyCreate_Success() throws Exception {
        mockMvc.perform(post(NEW_STUDY)
                        .param("path", "1234")
                        .param("title", "test")
                        .param("shortDescription","test")
                        .param("fullDescription", "test")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());


        assertTrue(studyRepository.existsByTitle("test"));

        Study study = studyRepository.findStudyWithAllByPath("1234");


        assertTrue(study.getManagers().size() == 1);
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 생성 - 실패 ( 주소 중복 )")
    void studyCreate_Fail() throws Exception {

        assertTrue(studyRepository.existsByTitle("test"));


        mockMvc.perform(post(NEW_STUDY)
                        .param("path", "test")
                        .param("title", "test")
                        .param("shortDescription", "test")
                        .param("fullDescription", "test")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name(StudyController.STUDY_FORM));

        assertTrue(study.getManagers().size() == 1);

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 뷰")
    void studyView() throws Exception {

        mockMvc.perform(get("/study/" + study.getPath()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(status().isOk())
                .andExpect(view().name(StudyController.STUDY_VIEW));
    }


    @Test
    @WithAccount("test")
    @DisplayName("스터디 구성원 뷰")
    void studyMemberView() throws Exception {


        mockMvc.perform(get("/study/" + study.getPath() + "/members"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(status().isOk())
                .andExpect(view().name(StudyController.STUDY_MEMBERS));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 가입")
    void joinStudy() throws Exception {

        Account test1 = accountFactory.createNewAccount("test1");

        Study study1 = studyFactory.createStudy(test1, "test1");
        studyService.publishStudy(study1);
        studyService.recruitStart(study1);

        mockMvc.perform(get("/study/" + study1.getPath() + "/join"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(status().isOk());



        System.out.println("study : " + study1.getMembers());
        assertTrue(study1.getMembers().contains(account));


    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 탈퇴")
    void leaveStudy() throws Exception {

        studyService.publishStudy(study);
        studyService.recruitStart(study);

        study.joinStudy(account);

        mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(status().isOk());



        assertFalse(study.getMembers().contains(account));


    }
}
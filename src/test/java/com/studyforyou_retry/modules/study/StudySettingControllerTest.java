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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class StudySettingControllerTest {

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
    @DisplayName("스터디 설정 접근 - 실패 ( 관리자 X ) ")
    void accessDeniedSettingsForm() throws Exception {

        study.getManagers().clear();

        mockMvc.perform(get(getSettingsUrl() + "description"))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithAccount("test")
    @DisplayName("소개 변경 폼")
    void updateDescriptionForm() throws Exception {

        mockMvc.perform(get(getSettingsUrl() + "description"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(view().name(StudySettingController.STUDY_DESCRIPTION));
    }

    @Test
    @WithAccount("test")
    @DisplayName("소개 변경 - 성공")
    void updateDescription_Success() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "description")
                        .param("shortDescription", "ㅎㅇㅎㅇ")
                        .param("fullDescription", "ㅎㅇㅎㅇ")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getShortDescription() , "ㅎㅇㅎㅇ");
        assertEquals(study.getFullDescription() , "ㅎㅇㅎㅇ");

    }

    @Test
    @WithAccount("test")
    @DisplayName("소개 변경 - 실패")
    void updateDescription_Fail() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "description")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(StudySettingController.STUDY_DESCRIPTION));

        assertEquals(study.getShortDescription() , null);

    }

    @Test
    @WithAccount("test")
    @DisplayName("배너 이미지 변경")
    void updateBanner() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "banner")
                        .param("image", "img:")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());


        assertTrue(!study.getImage().isEmpty());
    }

    @Test
    @WithAccount("test")
    @DisplayName("배너 이미지 사용")
    void enableBanner() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "banner")
                        .param("image", "img:")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());


        assertTrue(!study.getImage().isEmpty());
    }

    private String getSettingsUrl() {
        return "/study/"+study.getPath()+"/settings/";
    }
}
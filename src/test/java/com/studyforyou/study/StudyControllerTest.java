package com.studyforyou.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou.WithAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.StudyForm;
import com.studyforyou.repository.AccountRepository;
import com.studyforyou.repository.StudyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class StudyControllerTest {

    public static final String NEW_STUDY = "/new-study";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyService studyService;

    @Autowired
    ObjectMapper objectMapper;

    Study study;
    Account account;

    @BeforeEach
    private void beforeEach() {
        StudyForm studyForm = new StudyForm();
        studyForm.setTitle("테스트");
        studyForm.setShortDescription("테스트");
        studyForm.setPath("테스트1");
        studyForm.setFullDescription("테스트");

        account = accountRepository.findByNickname("test");
        studyService.newStudy(account, studyForm);

        study = studyRepository.findByPath(studyForm.getPath());
    }

    @AfterEach
    private void afterEach() {
        studyRepository.deleteAll();
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 개설 폼 테스트")
    void newStudyFormGet() throws Exception {
        mockMvc.perform(get(NEW_STUDY))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(view().name("study/form"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 개설 성공")
    void newStudyFormPostSuccess() throws Exception {

        String path = "테스트";
        mockMvc.perform(post(NEW_STUDY)
                        .param("title","테스트")
                        .param("path",path)
                        .param("shortDescription","테스트")
                        .param("fullDescription","테스트")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)));

        assertTrue(studyRepository.existsByPath(path));

        Study study = studyRepository.findByPath(path);

        assertTrue(study.getManagers().contains(account));


    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 개설 실패")
    void newStudyFormPostFail() throws Exception {


        assertTrue(studyRepository.existsByPath("테스트1"));

        String path = "테스트1";
        mockMvc.perform(post(NEW_STUDY)
                        .param("title","테스트")
                        .param("path",path)
                        .param("shortDescription","테스트")
                        .param("fullDescription","테스트")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));


        assertEquals(studyRepository.count(),1);

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 뷰")
    void studyView() throws Exception {


        mockMvc.perform(get("/study/" + study.getPath())
                .param("path", study.getPath()))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/view"));

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 구성원 보기")
    void studyMembers() throws Exception {

        mockMvc.perform(get("/study/"+study.getPath()+"/members")
                .param("path",study.getPath()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/members"));

        assertTrue(study.getManagers().contains(account));
    }


    @Test
    @WithAccount("test")
    @DisplayName("스터디 맴버 가입")
    void studyJoin() throws Exception {

        study.getManagers().clear();
        studyService.studyPublish(study);
        studyService.recruitStart(study);

        mockMvc.perform(get("/study/" + study.getPath() + "/join")
                .param("path",study.getPath()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("study/members"));

        assertTrue(study.getMembers().contains(account));

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 맴버 탈퇴")
    void studyLeave() throws Exception {

        study.getManagers().clear();
        studyService.studyPublish(study);
        studyService.recruitStart(study);
        study.getMembers().add(account);

        mockMvc.perform(get("/study/" + study.getPath() + "/leave")
                        .param("path",study.getPath()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertTrue(!study.getMembers().contains(account));

    }
}
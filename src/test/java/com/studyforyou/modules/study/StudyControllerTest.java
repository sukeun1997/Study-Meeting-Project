package com.studyforyou.modules.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou.modules.account.WithAccount;
import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
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
public class StudyControllerTest {

    public static final String NEW_STUDY = "/new-study";
    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public StudyRepository studyRepository;

    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    public StudyService studyService;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    StudyFactory studyFactory;

   Study study;

    Account account;
    @BeforeEach
    public void beforeEach() {
        account = accountRepository.findByNickname("test");
        study = studyFactory.createStudy();
    }
    @AfterEach
    private void afterEach() {
        studyRepository.deleteAll();
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??? ?????????")
    void newStudyFormGet() throws Exception {
        mockMvc.perform(get(NEW_STUDY))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(view().name("study/form"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void newStudyFormPostSuccess() throws Exception {

        String path = "?????????";
        mockMvc.perform(post(NEW_STUDY)
                        .param("title","?????????")
                        .param("path",path)
                        .param("shortDescription","?????????")
                        .param("fullDescription","?????????")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)));

        assertTrue(studyRepository.existsByPath(path));

        Study study = studyRepository.findByPath(path);

        assertTrue(study.getManagers().contains(account));


    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void newStudyFormPostFail() throws Exception {


        assertTrue(studyRepository.existsByPath("?????????1"));

        String path = "?????????1";
        mockMvc.perform(post(NEW_STUDY)
                        .param("title","?????????")
                        .param("path",path)
                        .param("shortDescription","?????????")
                        .param("fullDescription","?????????")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));


        assertEquals(studyRepository.count(),1);

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ???")
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
    @DisplayName("????????? ????????? ??????")
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
    @DisplayName("????????? ?????? ??????")
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
    @DisplayName("????????? ?????? ??????")
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
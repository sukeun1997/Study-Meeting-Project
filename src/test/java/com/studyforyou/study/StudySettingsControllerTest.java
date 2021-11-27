package com.studyforyou.study;

import com.studyforyou.WithAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.domain.Tag;
import com.studyforyou.dto.StudyForm;
import com.studyforyou.dto.TagForm;
import com.studyforyou.repository.AccountRepository;
import com.studyforyou.repository.StudyRepository;
import com.studyforyou.repository.TagRepository;
import com.studyforyou.tag.TagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class StudySettingsControllerTest extends StudyControllerTest {

    @Autowired TagRepository tagRepository;
    @Autowired TagService tagService;


    @Test
    @DisplayName("스터디 소개 수정 폼 - 권한 없느 접근자")
    @WithAccount("test")
    void studySettingDescription_AccessFail() throws Exception {

        study.getManagers().clear();

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description")
                        .param("path", study.getPath()))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("스터디 소개 수정 폼")
    @WithAccount("test")
    void studySettingDescription() throws Exception {
        mockMvc.perform(get(SettingURL(study.getPath()) + "/description")
                .param("path", study.getPath()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(view().name("study/description"))
                .andExpect(status().isOk());
    }


    @Test
    @WithAccount("test")
    @DisplayName("스터디 소개 수정")
    void testStudySettingDescription() throws Exception {
        mockMvc.perform(post(SettingURL(study.getPath()) + "/description")
                        .param("path", study.getPath())
                        .param("shortDescription", "하이요")
                        .param("fullDescription", "하이요")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingURL(URLEncoder.encode(study.getPath(),StandardCharsets.UTF_8))+ "/description"));


        assertTrue(!study.getShortDescription().isEmpty());
        assertTrue(!study.getFullDescription().isEmpty());
    }

    @Test
    @WithAccount("test")
    @DisplayName("배너 이미지 변경하기")
    void updateBanner() throws Exception {
        mockMvc.perform(post(SettingURL(study.getPath()) + "/banner")
                        .with(csrf())
                        .param("image", "image"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/banner"));


        assertTrue(!study.getImage().isEmpty());

    }

    @Test
    @WithAccount("test")
    @DisplayName("배너 이미지 사용하기")
    void enableBanner() throws Exception {
        mockMvc.perform(post(SettingURL(study.getPath()) + "/banner/enable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));


        assertTrue(study.isUseBanner());
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 태그 추가하기")
    void studyTagsAdd() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("하자");
        mockMvc.perform(post(SettingURL(study.getPath()) + "/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertNotNull(study.getTags());
        assertNotNull(tagRepository.findByTitle(tagForm.getTagTitle()));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 태그 삭제하기")
    void studyTagsRemove() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("하자");

        Tag tag = tagService.getTag(tagForm.getTagTitle());

        studyService.addTags(study, tag);

        mockMvc.perform(post(SettingURL(study.getPath()) + "/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

       assertTrue(!study.getTags().contains(tag));

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 태그 추가후 태그 폼")
    void studyTags_Added() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("하자");

        Tag tag = tagService.getTag(tagForm.getTagTitle());

        studyService.addTags(study, tag);

        mockMvc.perform(get(SettingURL(study.getPath()) + "/tags"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(status().isOk());

        assertEquals(tagService.findByAllTags().size(),1);
    }
    @Test
    void studyZones() {
    }

    @Test
    void studyZonesAdd() {
    }

    @Test
    void studyZonesRemove() {
    }

    @Test
    void studyStatus() {
    }

    @Test
    void studyStatusPublish() {
    }

    @Test
    void studyStatusClose() {
    }

    @Test
    void studyRecruitStart() {
    }

    @Test
    void studyRecruitStop() {
    }

    @Test
    void studyPathUpdate() {
    }

    @Test
    void testStudyPathUpdate() {
    }

    @Test
    void studyRemove() {
    }



    private String SettingURL(String path) {
        return "/study/"+ path + "/settings";
    }

}
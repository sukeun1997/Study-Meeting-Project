package com.studyforyou_retry.modules.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou_retry.infra.WithMockUser;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountFactory;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.WithAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.tags.TagForm;
import com.studyforyou_retry.modules.tags.TagService;
import com.studyforyou_retry.modules.zones.Zone;
import com.studyforyou_retry.modules.zones.ZoneForm;
import com.studyforyou_retry.modules.zones.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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
    ZoneRepository zoneRepository;

    @Autowired
    ObjectMapper objectMapper;

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

    @Autowired
    TagService tagService;

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
    @DisplayName("????????? ?????? ?????? - ?????? ( ????????? X ) ")
    void accessDeniedSettingsForm() throws Exception {

        study.getManagers().clear();

        mockMvc.perform(get(getSettingsUrl() + "description"))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ???")
    void updateDescriptionForm() throws Exception {

        mockMvc.perform(get(getSettingsUrl() + "description"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(view().name(StudySettingController.STUDY_DESCRIPTION));
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ??????")
    void updateDescription_Success() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "description")
                        .param("shortDescription", "????????????")
                        .param("fullDescription", "????????????")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getShortDescription(), "????????????");
        assertEquals(study.getFullDescription(), "????????????");

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ??????")
    void updateDescription_Fail() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "description")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(StudySettingController.STUDY_DESCRIPTION));

        assertEquals(study.getShortDescription(), null);

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ????????? ??????")
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
    @DisplayName("????????? ?????? ??????")
    void enableBanner() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "banner/enable")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());


        assertTrue(study.isUseBanner());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????????")
    void disableBanner() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "banner/disable")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());


        assertTrue(!study.isUseBanner());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ???")
    void tagsView() throws Exception {

        mockMvc.perform(get(getSettingsUrl() + "tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(status().isOk())
                .andExpect(view().name(StudySettingController.STUDY_TAGS));

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void addTags() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("??????");

        mockMvc.perform(post(getSettingsUrl() + "tags/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm)))
                .andExpect(status().isOk());

        assertEquals(study.getTags().size(), 1);
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ??????")
    void removeTags_Success() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("??????");
        Tag tag = tagService.getNewTag(tagForm.getTagTitle());
        studyService.addTag(study, tag);

        assertEquals(study.getTags().size(), 1);

        mockMvc.perform(post(getSettingsUrl() + "tags/remove")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm)))
                .andExpect(status().isOk());

        assertEquals(study.getTags().size(), 0);
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ?????? ( ???????????? ?????? ?????? )")
    void removeTags_Fail() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("??????");
        Tag tag = tagService.getNewTag(tagForm.getTagTitle());
        studyService.addTag(study, tag);

        assertEquals(study.getTags().size(), 1);

        tagForm.setTagTitle("??????1");

        mockMvc.perform(post(getSettingsUrl() + "tags/remove")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm)))
                .andExpect(status().isBadRequest());

        assertEquals(study.getTags().size(), 1);
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ???")
    void zonesView() throws Exception {

        mockMvc.perform(get(getSettingsUrl() + "zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(status().isOk())
                .andExpect(view().name(StudySettingController.STUDY_ZONES));

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ??????")
    void addZones() throws Exception {

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Ansan(?????????)/Gyeonggi");

        mockMvc.perform(post(getSettingsUrl() + "zones/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm)))
                .andExpect(status().isOk());

        assertEquals(study.getZones().size(), 1);
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ??????")
    void removeZones() throws Exception {

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Ansan(?????????)/Gyeonggi");
        Zone zone = zoneRepository.findByCityAndLocalNameOfCity(zoneForm.getCity(), zoneForm.getLocalNameOfCity());

        studyService.addZones(study, zone);

        assertEquals(study.getZones().size(), 1);

        mockMvc.perform(post(getSettingsUrl() + "zones/remove")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm)))
                .andExpect(status().isOk());

        assertEquals(study.getZones().size(), 0);
    }


    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ???")
    void statusView() throws Exception {

        mockMvc.perform(get(getSettingsUrl() + "study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(status().isOk())
                .andExpect(view().name(StudySettingController.STUDY_STATUS));

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ??????")
    void studyPublish() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/publish")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertTrue(study.isPublished());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ??????")
    void studyClose() throws Exception {

        study.publish();
        mockMvc.perform(post(getSettingsUrl() + "study/close")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertTrue(!study.isPublished());
        assertTrue(study.isClosed());
    }


    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ??????")
    void updatePath() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/path")
                        .with(csrf())
                        .param("newPath", "1234"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getPath(), "1234");
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ?????? ( ?????? ?????? )")
    void updatePath_Fail() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/path")
                        .with(csrf())
                        .param("newPath", "test"))
                .andExpect(flash().attributeExists("studyPathError"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getPath(), "test");
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ?????? ( ???????????? ?????? ?????? )")
    void updatePath_Fail2() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/path")
                        .with(csrf())
                        .param("newPath", "12"))
                .andExpect(flash().attributeExists("studyPathError"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getPath(), "test");
    }


    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ??????")
    void updateTitle() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/title")
                        .param("newTitle", "test1")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getTitle(), "test1");

    }


    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ?????? ( ?????? ?????? )")
    void updateTitle_Fail() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/title")
                        .param("newTitle", "test")
                        .with(csrf()))
                .andExpect(flash().attributeExists("studyTitleError"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getTitle(), "test");
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? - ?????? ( ???????????? ?????? ?????? : ?????? )")
    void updateTitle_Fail2() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/title")
                        .param("newTitle", "")
                        .with(csrf()))
                .andExpect(flash().attributeExists("studyTitleError"))
                .andExpect(status().is3xxRedirection());

        assertEquals(study.getTitle(), "test");
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? - ?????? ")
    void removeStudy() throws Exception {

        study.setClosed(true);

        mockMvc.perform(post(getSettingsUrl() + "study/remove")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertEquals(studyRepository.count(), 0);
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? - ?????? ( isRemovable ?????? ?????? X ) ")
    void removeStudy_Fail() throws Exception {

        mockMvc.perform(post(getSettingsUrl() + "study/remove")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(getSettingsUrl() + "study"));

        assertEquals(studyRepository.count(), 1);
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ?????? - ??????")
    void recruitStart() throws Exception {

        study.publish();

        mockMvc.perform(post(getSettingsUrl() + "recruit/start")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertTrue(study.isRecruiting());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ?????? - ?????? ( ?????? ?????? ????????? 1?????? ????????? ?????? ) ")
    void recruitStart_Fail() throws Exception {

        study.publish();
        study.setRecruitDateTime(LocalDateTime.now());

        mockMvc.perform(post(getSettingsUrl() + "recruit/start")
                        .with(csrf()))
                .andExpect(flash().attribute("message","?????? ?????? ?????? ????????? 1???????????? ???????????????."))
                .andExpect(status().is3xxRedirection());

        assertTrue(!study.isRecruiting());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ?????? - ?????? ")
    void recruitStop() throws Exception {

        study.recruitStart();
        study.setRecruitDateTime(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post(getSettingsUrl() + "recruit/stop")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertTrue(!study.isRecruiting());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ?????? - ?????? ( ?????? ?????? ????????? 1?????? ????????? ?????? ) ")
    void recruitStop_Fail() throws Exception {

        study.publish();
        study.recruitStart();

        assertTrue(study.isRecruiting());

        mockMvc.perform(post(getSettingsUrl() + "recruit/stop")
                        .with(csrf()))
                .andExpect(flash().attribute("message","?????? ?????? ?????? ????????? 1???????????? ???????????????."))
                .andExpect(status().is3xxRedirection());

        assertTrue(study.isRecruiting());
    }

    private String getSettingsUrl() {
        return "/study/" + study.getPath() + "/settings/";
    }
}
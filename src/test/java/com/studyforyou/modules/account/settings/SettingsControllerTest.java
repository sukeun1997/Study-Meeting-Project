package com.studyforyou.modules.account.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou.infra.MockMvcTest;
import com.studyforyou.modules.account.WithAccount;
import com.studyforyou.modules.account.AccountService;
import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.tag.Tag;
import com.studyforyou.modules.zone.Zone;
import com.studyforyou.modules.tag.TagForm;
import com.studyforyou.modules.zone.ZoneForm;
import com.studyforyou.modules.account.AccountRepository;
import com.studyforyou.modules.tag.TagRepository;
import com.studyforyou.modules.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;


    @Autowired
    ZoneRepository zoneRepository;

    @AfterEach
    void AfterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @BeforeEach
    void beforeEach() {
        Zone build = Zone.builder().city("????????????").localNameOfCity("????????????").province("?????????").build();
        zoneRepository.save(build);
    }

    @Test
    @WithAccount("sukeun")
    @DisplayName("????????? ?????? ????????? - ?????? ")
    void profileUpdate() throws Exception {
        String bio = "?????? ????????? ??????";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Account sukeun = accountRepository.findByNickname("sukeun");
        assertEquals(sukeun.getBio() , bio);
    }

    @Test
    @WithAccount("sukeun")
    @DisplayName("????????? ?????? ????????? - ?????? ")
    void profileUpdate_error() throws Exception {
        String bio = "?????? ????????? ??????/?????? ????????? ??????/?????? ????????? ??????/?????? ????????? ??????/?????? ????????? ??????/?????? ????????? ??????";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(view().name("settings/profile"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

        Account byNickname = accountRepository.findByNickname("sukeun");
        assertEquals(byNickname.getBio() , null);
    }


    @Test
    @DisplayName("???????????? ?????? - ??????")
    @WithAccount("sukeun")
    void passwordUpdate() throws Exception {

        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "tnrms2177")
                        .param("newPasswordConfirm", "tnrms2177")
                        .with(csrf()))
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        Account byNickname = accountRepository.findByNickname("sukeun");

        assertTrue(passwordEncoder.matches("tnrms2177",byNickname.getPassword()));
    }

    @Test
    @DisplayName("???????????? ?????? - ??????")
    @WithAccount("sukeun")
    void passwordUpdate_error() throws Exception {

        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "tnrms2188")
                        .param("newPasswordConfirm", "tnrms2177")
                        .with(csrf()))
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));

        Account byNickname = accountRepository.findByNickname("sukeun");

        assertTrue(passwordEncoder.matches("12345678",byNickname.getPassword()));
    }


    @Test
    @DisplayName("?????? ?????? ???")
    @WithAccount("sukeun")
    void tagTest() throws Exception {

        mockMvc.perform(get("/settings/tags")
                ).andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));

    }


    @Test
    @DisplayName("?????? ?????? ??????")
    @WithAccount("sukeun")
    void tagAddTest() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("??????");

        mockMvc.perform(post("/settings/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag title = tagRepository.findByTitle("??????");
        assertEquals(title.getTitle(), "??????");

        Account byNickname = accountRepository.findByNickname("sukeun");

        assertTrue(byNickname.getTags().contains(title));

    }


    @Test
    @DisplayName("?????? ?????? ??????")
    @WithAccount("sukeun")
    void tagRemoveTest() throws Exception {
        Account byNickname = accountRepository.findByNickname("sukeun");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(byNickname,newTag);

        assertTrue(byNickname.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(byNickname.getTags().contains(tagForm));
    }


    @Test
    @DisplayName("?????? ?????? ???")
    @WithAccount("sukeun")
    void zoneTest() throws Exception {

        mockMvc.perform(get("/settings/zones")
                ).andExpect(view().name("settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));

    }


    @Test
    @DisplayName("?????? ?????? ??????")
    @WithAccount("sukeun")
    void zoneAddTest() throws Exception {

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("????????????(????????????)/?????????");

        mockMvc.perform(post("/settings/zones/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Account byNickname = accountRepository.findByNickname("sukeun");

        assertNotNull(byNickname.getZones());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    @WithAccount("sukeun")
    void zoneRemoveTest() throws Exception {

        Zone zone = zoneRepository.findByCityAndLocalNameOfCity("????????????", "????????????");
        Account account = accountRepository.findByNickname("sukeun");
        accountService.addZone(account, zone);

        assertTrue(account.getZones().contains(zone));

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("????????????(????????????)/?????????");


        mockMvc.perform(post("/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertTrue(account.getZones().size() == 0);
    }
}
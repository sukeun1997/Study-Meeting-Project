package com.studyforyou_retry.modules.account.setting;

import com.studyforyou_retry.infra.WithMockUser;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountFactory;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.WithAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.tags.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class SettingControllerTest {

    public static final String SETTINGS_PROFILE = "settings/profile";
    public static final String SETTINGS_PASSWORD = "settings/password";
    public static final String SETTINGS_NOTIFICATIONS = "settings/notifications";
    public static final String SETTINGS_ACCOUNT = "settings/account";
    public static final String SETTINGS_TAGS = "settings/tags";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    TagRepository tagRepository;


    Account account;

    @BeforeEach
    private void init() {
        account = accountRepository.findByNickname("test");
    }

    @Test
    @WithAccount("test")
    @DisplayName("프로필 수정 폼")
    void updateProfile() throws Exception {
        mockMvc.perform(get("/" + SETTINGS_PROFILE))
                .andExpect(model().attributeExists("profile"))
                .andExpect(view().name(SETTINGS_PROFILE));
    }

    @Test
    @WithAccount("test")
    @DisplayName("프로필 수정 요청 - 성공")
    void updateProfilePost() throws Exception {

        mockMvc.perform(post("/" + SETTINGS_PROFILE)
                        .param("bio", "gdgd")
                        .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));


        assertTrue(account.getBio().equals("gdgd"));

    }

    @Test
    @WithAccount("test")
    @DisplayName("프로필 수정 요청 - 실패 ( 유효성 검사 )")
    void updateProfilePost_Fail() throws Exception {

        mockMvc.perform(post("/" + SETTINGS_PROFILE)
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(view().name(SETTINGS_PROFILE));
    }


    @Test
    @WithAccount("test")
    @DisplayName("패스워드 변경 폼")
    void updatePassword() throws Exception {
        mockMvc.perform(get("/" + SETTINGS_PASSWORD))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(status().isOk());
    }


    @Test
    @WithAccount("test")
    @DisplayName("패스워드 변경 요청 - 성공")
    void updatePasswordPost() throws Exception {


        mockMvc.perform(post("/" + SETTINGS_PASSWORD)
                        .with(csrf())
                        .param("newPassword", "12341234")
                        .param("newPasswordConfirm", "12341234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));

        assertTrue(passwordEncoder.matches("12341234", account.getPassword()));

    }

    @Test
    @WithAccount("test")
    @DisplayName("패스워드 변경 요청 - 실패 ( 새로운 비밀번호와 확인 값이 다름 )")
    void updatePasswordPost_Fail() throws Exception {


        mockMvc.perform(post("/" + SETTINGS_PASSWORD)
                        .with(csrf())
                        .param("newPassword", "12341234")
                        .param("newPasswordConfirm", "123412345"))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_PASSWORD));

        assertTrue(passwordEncoder.matches("testtest", account.getPassword()));
    }

    @Test
    @WithAccount("test")
    @DisplayName("패스워드 변경 요청 - 실패 ( 비밀번호 길이가 8자 미만 )")
    void updatePasswordPost_Fail2() throws Exception {


        mockMvc.perform(post("/" + SETTINGS_PASSWORD)
                        .with(csrf())
                        .param("newPassword", "1234")
                        .param("newPasswordConfirm", "1234"))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_PASSWORD));

        assertTrue(passwordEncoder.matches("testtest", account.getPassword()));
    }

    @Test
    @WithAccount("test")
    @DisplayName("알림 설정 폼")
    void updateNotifications() throws Exception {

        mockMvc.perform(get("/" + SETTINGS_NOTIFICATIONS))
                .andExpect(model().attributeExists("notifications"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("알림 설정 변경 - 성공")
    void updateNotificationsPost() throws Exception {

        mockMvc.perform(post("/" + SETTINGS_NOTIFICATIONS)
                        .with(csrf())
                        .param("studyCreatedByEmail", "true"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        assertEquals(account.isStudyCreatedByEmail(), true);
    }


    @Test
    @WithAccount("test")
    @DisplayName("계정 설정 폼")
    void updateAccount() throws Exception {
        mockMvc.perform(get("/" + SETTINGS_ACCOUNT))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("계정 설정 변경 - 성공")
    void updateAccountPost() throws Exception {
        mockMvc.perform(post("/" + SETTINGS_ACCOUNT)
                        .param("nickname", "gdgd12")
                        .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection());

        assertEquals(account.getNickname(), "gdgd12");
    }

    @Test
    @WithAccount("test")
    @DisplayName("계정 설정 변경 - 실패 ( 닉네임 중복 )")
    void updateAccountPost_Fail() throws Exception {

        accountFactory.createNewAccount("test1");

        mockMvc.perform(post("/" + SETTINGS_ACCOUNT)
                        .param("nickname", "test1")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_ACCOUNT));

        assertEquals(account.getNickname(), "test");
    }

    @Test
    @WithAccount("test")
    @DisplayName("관심 주제 폼")
    void updateTags() throws Exception {

        tagRepository.save(Tag.builder().title("자바").build());

        mockMvc.perform(get("/" + SETTINGS_TAGS))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(view().name(SETTINGS_TAGS));

        assertEquals(tagRepository.count(), 1);
    }
}
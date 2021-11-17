package com.studyforyou.settings;

import com.studyforyou.WithAccount;
import com.studyforyou.account.AccountService;
import com.studyforyou.domain.Account;
import com.studyforyou.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void AfterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("sukeun")
    @DisplayName("프로필 수정 테스트 - 정상 ")
    void profileUpdate() throws Exception {
        String bio = "짧은 소개를 수정";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Account sukeun = accountRepository.findByNickname("sukeun");
        assertEquals(sukeun.getBio() , bio);
    }

    @Test
    @WithAccount("sukeun")
    @DisplayName("프로필 수정 테스트 - 오류 ")
    void profileUpdate_error() throws Exception {
        String bio = "짧은 소개를 수정/짧은 소개를 수정/짧은 소개를 수정/짧은 소개를 수정/짧은 소개를 수정/짧은 소개를 수정";
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
    @DisplayName("패스워드 수정 - 정상")
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
    @DisplayName("패스워드 수정 - 오류")
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
}
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
}
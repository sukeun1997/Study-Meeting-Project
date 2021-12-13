package com.studyforyou_retry.modules.account;

import lombok.With;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:/application-test.properties")
class AccountControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired
    AccountFactory accountFactory;

    @AfterEach
    private void after() {
        accountRepository.deleteAll();
    }
    @Test
    @DisplayName("회원가입 창 테스트")
    public void signUpPageTest() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void postSignUpPage_Success() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "asdad")
                .param("password", "test123123")
                .param("email", "test@naver.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated());

        assertTrue(accountRepository.existsByEmail("test@naver.com"));


    }

    @Test
    @DisplayName("회원가입 - 실패 (중복회원)")
    void postSignUpPage_Fail() throws Exception {

        accountFactory.createNewAccount("test");

        assertTrue(accountRepository.existsByNickname("test"));

        mockMvc.perform(post("/sign-up")
                        .param("nickname", "test")
                        .param("password", "test123123")
                        .param("email", "test@naver.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());

        assertEquals(accountRepository.count(), 1);
    }

    @Test
    @DisplayName("회원가입 - 실패 (잘못된 입력)")
    void postSignUpPage_Fail2() throws Exception {

        mockMvc.perform(post("/sign-up")
                        .param("nickname", "test1")
                        .param("password", "test")
                        .param("email", "test@naver.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(unauthenticated());

        assertEquals(accountRepository.count(), 0);
    }

    @Test
    @WithAccount("test")
    @DisplayName("이메일 인증 확인 - 성공")
    void checkEmailToken_Success() throws Exception {

        Account account = accountRepository.findByNickname("test");

        mockMvc.perform(get("/check-email-token")
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail())
                        .with(csrf()))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(status().isOk());

        assertTrue(account.isEmailVerified());
    }

    @Test
    @WithAccount("test")
    @DisplayName("이메일 인증 확인 - 실패")
    void checkEmailToken_Fail() throws Exception {

        Account account = accountRepository.findByNickname("test");
        mockMvc.perform(get("/check-email-token")
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail() +"1")
                        .with(csrf()))
                .andExpect(model().attributeDoesNotExist("nickname"))
                .andExpect(model().attributeDoesNotExist("numberOfUser"))
                .andExpect(model().attributeExists("error"))
                .andExpect(status().isOk());

        assertFalse(account.isEmailVerified());
    }

    @Test
    @WithAccount("test")
    @DisplayName("인증 이메일 재전송 - 성공")
    void resendConfirmEmail_Success() throws Exception {
        Account account = accountRepository.findByNickname("test");
        account.setEmailCheckTokenGeneratedAt(account.getEmailCheckTokenGeneratedAt().minusHours(1));

        mockMvc.perform(get("/resend-confirm-email"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("error"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("인증 이메일 재전송 - 실패")
    void resendConfirmEmail_Fail() throws Exception {

        Account account = accountRepository.findByNickname("test");

        mockMvc.perform(get("/resend-confirm-email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name("account/check-email"));
    }
}
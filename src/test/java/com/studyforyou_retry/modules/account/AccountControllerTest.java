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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
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

    @Test
    @WithAccount("test")
    @DisplayName("프로필 폼")
    void profileView() throws Exception {
        mockMvc.perform(get("/profile/test"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("isOwner", true))
                .andExpect(status().isOk())
                .andExpect(view().name("account/profile"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("이메일 로그인 링크 보내기 - 성공")
    void emailLogin() throws Exception {
        mockMvc.perform(post("/email-login")
                        .with(csrf())
                        .param("email", "test@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.ACCOUNT_CHECK_LOGIN_EMAIL))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist("error"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("이메일 로그인 링크 보내기 - 실패 (잘못된 이메일)")
    void emailLogin_Fail() throws Exception {
        mockMvc.perform(post("/email-login")
                        .with(csrf())
                        .param("email", "tests@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.ACCOUNT_CHECK_LOGIN_EMAIL))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("이메일 로그인 링크 접속 - 성공")
    void emailLoginCheck() throws Exception {


        Account test = accountFactory.createNewAccount("test");
        test.GenerateCheckToken();

        mockMvc.perform(get("/logged-in-by-email")
                .param("email", test.getEmail())
                .param("token", test.getEmailCheckToken()))
                .andExpect(view().name(AccountController.ACCOUNT_LOGGED_IN_BY_EMAIL))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("이메일 로그인 링크 접속 - 실패 (잘못된 이메일)")
    void emailLoginCheck_Fail() throws Exception {

        Account test = accountFactory.createNewAccount("test");
        test.GenerateCheckToken();
        mockMvc.perform(get("/logged-in-by-email")
                        .param("email", test.getEmail() +"a")
                        .param("token", test.getEmailCheckToken()))
                .andExpect(view().name(AccountController.ACCOUNT_LOGGED_IN_BY_EMAIL))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("이메일 로그인 링크 접속 - 실패 (잘못된 토큰)")
    void emailLoginCheck_Fail2() throws Exception {

        Account test = accountFactory.createNewAccount("test");
        test.GenerateCheckToken();

        mockMvc.perform(get("/logged-in-by-email")
                        .param("email", test.getEmail())
                        .param("token", test.getEmailCheckToken()+"a"))
                .andExpect(view().name(AccountController.ACCOUNT_LOGGED_IN_BY_EMAIL))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated());
    }
}
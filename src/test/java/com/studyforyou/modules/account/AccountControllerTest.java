package com.studyforyou.modules.account;

import com.studyforyou.infra.mail.EmailMessage;
import com.studyforyou.infra.mail.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private AccountService accountService;

    @MockBean
    EmailService emailService;


    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }
    @Test
    @DisplayName("회원 가입 화면 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    void signUpSubmit_false() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "sukeun")
                        .param("email", "email..")
                        .param("password", "12345")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());

    }


    @Test
    @DisplayName("회원 가입 처리 - 입력값 정상")
    void signUpSubmit_true() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "sukeun")
                        .param("email", "email@eamil.com")
                        .param("password", "123456789")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("email@eamil.com");

        assertNotNull(account.getEmailCheckToken());
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "123456789");

        assertTrue(accountRepository.existsByEmail("email@eamil.com"));
        then(emailService).should().sendEmail(any(EmailMessage.class));

    }

    @Test
    @DisplayName("이메일 인증 - 잘못된 입력")
    void input_wrong_email() throws Exception {

        mockMvc.perform(get("/check-email-token")
                        .queryParam("token", "asdasd")
                        .queryParam("email", "asdasd"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
                .andExpect(view().name("account/checked-email"));



    }

    @Test
    @DisplayName("이메일 인증 - 옳바른 입력")
    void input_true_email() throws Exception {

        createAccount();
        Account account = accountRepository.findByEmail("email@email.com");

        mockMvc.perform(get("/check-email-token")
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail()))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated());
    }


    public void createAccount() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("email@email.com");
        signUpForm.setPassword("12345678");
        signUpForm.setNickname("sukeun");
        accountService.processNewAccount(signUpForm);
    }

    @Test
    @DisplayName("이메일 로그인 성공 테스트")
    void login_success_test_email() throws Exception {
       createAccount();


        mockMvc.perform(post("/login")
                        .param("username", "email@email.com")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withAuthenticationName("sukeun"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("닉네임 로그인 성공 테스트")
    void login_success_test_nickname() throws Exception {
        createAccount();


        mockMvc.perform(post("/login")
                        .param("username", "sukeun")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withAuthenticationName("sukeun"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void login_fail_test_nickname() throws Exception {
        createAccount();

        mockMvc.perform(post("/login")
                        .param("username", "11111")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser(username = "sukeun")
    void logout_test() throws Exception {

        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/"));
    }
}
package com.studyforyou.account;

import com.studyforyou.domain.Account;
import com.studyforyou.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private AccountRepository accountRepository;


    @MockBean
    JavaMailSender javaMailSender;

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
        then(javaMailSender).should().send(any(SimpleMailMessage.class));

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

        Account account = Account.builder()
                .email("email@eamil.coms")
                .nickname("asds")
                .emailCheckToken(UUID.randomUUID().toString())
                .build();

        accountRepository.save(account);

        mockMvc.perform(get("/check-email-token")
                        .queryParam("token", account.getEmailCheckToken())
                        .queryParam("email", account.getEmail()))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated());



    }
}
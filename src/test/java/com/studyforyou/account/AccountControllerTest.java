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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


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
                .andExpect(view().name("account/sign-up"));

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
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("email@eamil.com");

        assertNotNull(account.getEmailCheckToken());
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "123456789");

        assertTrue(accountRepository.existsByEmail("email@eamil.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));

    }
}
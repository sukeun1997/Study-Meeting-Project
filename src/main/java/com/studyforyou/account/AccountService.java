package com.studyforyou.account;

import com.studyforyou.domain.Account;
import com.studyforyou.domain.Tag;
import com.studyforyou.dto.PasswordForm;
import com.studyforyou.dto.SignUpForm;
import com.studyforyou.repository.AccountRepository;
import com.studyforyou.settings.NicknameForm;
import com.studyforyou.settings.Notifications;
import com.studyforyou.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    private final PasswordEncoder passwordEncoder;


    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = savedNewAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account savedNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디포유, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken()+"&email="+ newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);

    }

    public void completeCheckEmail(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void completeProfileUpdate(Account account, Profile profile) {
        account.profileUpdate(profile);
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickName) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(emailOrNickName);

        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickName);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickName);
        }

        return new UserAccount(account);
    }

    public void updatePassword(PasswordForm passwordForm, Account account) {

        String password = passwordEncoder.encode(passwordForm.getNewPasswordConfirm());
        account.setPassword(password);
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        account.notificationsUpdate(notifications);
        accountRepository.save(account);
    }

    public void updateNickName(Account account, NicknameForm nickNameForm) {
        account.setNickname(nickNameForm.getNickname());
        accountRepository.save(account);
        login(account);
    }

    public void sendConfirmEmail(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디포유, 이메일 로그인 인증");
        mailMessage.setText("/login-by-email?token="+ account.getEmailCheckToken()+"&email="+ account.getEmail());

        javaMailSender.send(mailMessage);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());

        byId.ifPresent(a -> a.getTags().add(tag));
    }

    @Transactional(readOnly = true)
    public Set<Tag> getTags(Account account) {
        Account byId = accountRepository.findById(account.getId()).orElseThrow(EntityNotFoundException::new);

        return byId.getTags();
    }
}

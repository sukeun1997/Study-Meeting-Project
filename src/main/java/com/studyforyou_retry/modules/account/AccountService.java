package com.studyforyou_retry.modules.account;

import com.studyforyou_retry.modules.account.setting.Notifications;
import com.studyforyou_retry.modules.account.setting.PasswordForm;
import com.studyforyou_retry.modules.account.setting.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public void createNewAccount(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        sendConfirmEmail(account);
        accountRepository.save(account);
        login(account);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailAndNickName) throws UsernameNotFoundException {

        Account account = accountRepository.findByNickname(emailAndNickName);

        if (account == null) {
            account = accountRepository.findByEmail(emailAndNickName);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailAndNickName);
        }

        return new UserAccount(account);
    }

    public void verifiedEmailToken(Account account) {
        account.verifiedEmailToken();
        login(account);
    }

    private void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);
    }


    public void sendConfirmEmail(Account account) {
        account.GenerateCheckToken();
        log.info("/check-email-token?token={}&email={}", account.getEmailCheckToken(), account.getEmail());
        //TODO 인증 이메일 보내기
    }

    public void updateProfile(Account account, Profile profile) {
        account.updateProfile(profile);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, PasswordForm passwordForm) {
        String password = passwordEncoder.encode(passwordForm.getNewPasswordConfirm());
        account.updatePassword(password);
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        account.updateNotifications(notifications);
        accountRepository.save(account);
    }

    public void updateAccount(Account account, String nickname) {
        account.updateNickName(nickname);
        accountRepository.save(account);
    }
}


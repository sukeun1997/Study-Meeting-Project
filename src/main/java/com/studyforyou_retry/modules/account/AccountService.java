package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
        account.getGenerateCheckToken();
        log.info("/check-email-token?token={}&email={}", account.getEmailCheckToken(), account.getEmail());
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailAndNickName) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(emailAndNickName);

        if (account == null) {
            account = accountRepository.findByNickname(emailAndNickName);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailAndNickName);
        }

        return new UserAccount(account);
    }

    public void verifiedEmailToken(Account account) {
        account.verifiedEmailToken();
    }
}


package com.studyforyou.modules.account;

import com.studyforyou.infra.config.AppProperties;
import com.studyforyou.modules.account.settings.PasswordForm;
import com.studyforyou.modules.tag.Tag;
import com.studyforyou.modules.zone.Zone;
import com.studyforyou.infra.mail.EmailMessage;
import com.studyforyou.infra.mail.EmailService;
import com.studyforyou.modules.account.settings.NicknameForm;
import com.studyforyou.modules.account.settings.Notifications;
import com.studyforyou.modules.account.settings.Profile;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;


    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = savedNewAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account savedNewAccount(SignUpForm signUpForm) {

        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {

        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디 포유 서비스를 사용하려면 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());

        String process = templateEngine.process("mail/simple-link", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디포유, 회원 가입 인증")
                .message(process)
                .build();

        emailService.sendEmail(emailMessage);
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

        Context context = new Context();
        context.setVariable("link", "/login-by-email?token="+ account.getEmailCheckToken()+"&email="+ account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "스터디포유, 이메일 로그인 인증");
        context.setVariable("message", "로그인을 하기위해 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());

        String process = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디포유, 이메일 로그인 인증")
                .message(process)
                .build();

        emailService.sendEmail(emailMessage);
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

    public void removeTag(Account account, Tag tag) {
        Account accountById = accountRepository.findById(account.getId()).orElseThrow(EntityNotFoundException::new);
        accountById.getTags().remove(tag);
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(user -> user.getZones().add(zone));
    }

    @Transactional(readOnly = true)
    public Set<Zone> getZones(Account account) {
        Account byId = accountRepository.findById(account.getId()).orElseThrow(EntityNotFoundException::new);
        return byId.getZones();
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());

        byId.ifPresent(user -> user.getZones().remove(zone));
    }

    @Transactional(readOnly = true)
    public Account getAccount(String nickname) {
        Account account = accountRepository.findByNickname(nickname);
        if (nickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }

        return account;
    }
}

package com.studyforyou_retry.modules.account;

import com.studyforyou_retry.infra.config.AppProperties;
import com.studyforyou_retry.infra.mail.EmailMessage;
import com.studyforyou_retry.infra.mail.HtmlEmailService;
import com.studyforyou_retry.modules.account.setting.Notifications;
import com.studyforyou_retry.modules.account.setting.PasswordForm;
import com.studyforyou_retry.modules.account.setting.Profile;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.zones.Zone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
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
import org.yaml.snakeyaml.tokens.TagToken;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final HtmlEmailService htmlEmailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account createNewAccount(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        Account save = accountRepository.save(account);
        login(account);
        return save;
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

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);
    }

    @Async
    public void sendConfirmEmail(Account account) {
        account.GenerateCheckToken();
        log.info("/check-email-token?token={}&email={}", account.getEmailCheckToken(), account.getEmail());
        sendSignUpEmail(account);
    }

    private void sendSignUpEmail(Account account) {
        Context context = new Context();

        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", "스터디 포유 이메일 인증을 위해 아래 링크를 통해 접속해 주세요");
        context.setVariable("link", "/check-email-token?token="+account.getEmailCheckToken()+"&email="+account.getEmail());
        context.setVariable("host",appProperties.getHost());
        context.setVariable("linkName","스터디 포유 이메일 인증");

        String process = templateEngine.process("mail/simple-link", context);


        EmailMessage message = EmailMessage.builder().to(account.getEmail())
                .message(process)
                .subject("스터디 포유 이메일 인증")
                .build();

        htmlEmailService.sendEmail(message);
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

    @Async
    public void sendLoginEmail(Account byEmail) {
        Context context = new Context();

        context.setVariable("nickname", byEmail.getNickname());
        context.setVariable("message", "스터디 포유 이메일 로그인을 위해 아래 링크를 통해 접속해 주세요");
        context.setVariable("link", "/logged-in-by-email?token="+byEmail.getEmailCheckToken()+"&email="+byEmail.getEmail());
        context.setVariable("host",appProperties.getHost());
        context.setVariable("linkName","스터디 포유 이메일 로그인");

        String process = templateEngine.process("mail/simple-link", context);


        EmailMessage message = EmailMessage.builder().to(byEmail.getEmail())
                .message(process)
                .subject("스터디 포유 이메일 로그인")
                .build();

        htmlEmailService.sendEmail(message);
    }

    @Transactional(readOnly = true)
    public Set<String> findTags(Account account) {
        Account byId = accountRepository.findById(account.getId()).orElseThrow(EntityNotFoundException::new);
        return byId.getTags().stream().map(Tag::getTitle).collect(Collectors.toSet());
    }

    public void addTags(Account account, Tag tag) {
        accountRepository.findById(account.getId()).ifPresent(user -> user.getTags().add(tag));
    }

    public void removeTags(Account account, Tag tag) {
        accountRepository.findById(account.getId()).ifPresent(user -> user.getTags().remove(tag));
    }

    @Transactional(readOnly = true)
    public List<String> findZones(Account account) {
        Account byId = accountRepository.findById(account.getId()).orElseThrow(EntityNotFoundException::new);
        return byId.getZones().stream().map(Zone::toString).collect(Collectors.toList());
    }

    public void addZones(Account account, Zone zone) {
        accountRepository.findById(account.getId()).ifPresent(user -> user.getZones().add(zone));
    }

    public void removeZones(Account account, Zone zone) {
        accountRepository.findById(account.getId()).ifPresent(user -> user.getZones().remove(zone));
    }
}


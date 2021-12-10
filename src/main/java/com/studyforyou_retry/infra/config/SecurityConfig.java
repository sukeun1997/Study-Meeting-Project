package com.studyforyou_retry.infra.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .mvcMatchers("/").permitAll()
                .anyRequest().authenticated();

//        http.formLogin().defaultSuccessUrl("/").permitAll();
//
//        http.logout().logoutSuccessUrl("/");




    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().
                antMatchers("/favicon.ico", "/resources/**","/images/**")
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }

}

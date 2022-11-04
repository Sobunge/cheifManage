package com.pensasha.cheifManage.securingWeb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.pensasha.cheifManage.user.UserDetailsServiceImp;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

        AuthenticationManager authenticationManager;

        @Autowired
        private UserDetailsServiceImp userDetailsService;

        @Autowired
        private CustomSuccessHandler customSuccessHandler;

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService)
                                .passwordEncoder(new BCryptPasswordEncoder());
        }

        @Bean
        protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                                .antMatchers("/", "/error", "/changePassword")
                                .permitAll()
                                .anyRequest().authenticated().and().formLogin().loginPage("/")
                                .usernameParameter("username")
                                .passwordParameter("password").permitAll().successHandler(customSuccessHandler).and()
                                .logout()
                                .logoutUrl("/logout").logoutSuccessUrl("/?logout").permitAll().and()
                                .exceptionHandling()
                                .accessDeniedPage("/403").and().csrf().disable();

                return http.build();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().antMatchers("/img/**", "/fontawesome-free/**", "/js/**",  "/css/**", "/webjars/**");
        }

        @Bean(name = "passordEncoder")
        public BCryptPasswordEncoder passwordencoder() {
                return new BCryptPasswordEncoder();
        }

}

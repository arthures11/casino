package com.bryja.casino;

import com.bryja.casino.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customerUserDetailsService ;

    @Bean
    public PasswordEncoder passwordEncoder()
    { return new BCryptPasswordEncoder(); }
    @Value("/user/add")
    private String successUrl;
    @Value("/workdays")
    private String failureUrl;


    @Bean
    protected SecurityFilterChain chains(HttpSecurity http) throws Exception {

        http
                //.cors()
                //.and()
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/swagger-ui/**", "/swagger-ui/#/", "/swagger-ui**").hasRole("USER")
                        //.requestMatchers("/h2-console/**", "/h2-console/#/", "/h2-console**").hasRole("USER")
                        .requestMatchers("/index.html", "/error", "/webjars/**", "/githubprivacyerror.html","/css/**","/assets/**", "/images/**", "/fonts/**", "/scripts/**", "/error", "/login", "/", "/user2", "/user/add", "/ruleta", "/adminpanelbonuses", "/adminpanelusers", "/bonuses", "/profile", "/register", "/table","/favicon", "/favion/**", "/sock/**", "/chathistory").permitAll()
                        .anyRequest().authenticated()
                        .and()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                )
                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //and()
                .oauth2Login()
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .and()
                .formLogin((form) -> form
                        .loginPage("/index")
                        .loginProcessingUrl("/index")
                        .defaultSuccessUrl("/index")
                        .permitAll()
                ).
                httpBasic().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .securityContext()
                .securityContextRepository(new HttpSessionSecurityContextRepository())
                .and();
        // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();


    }



    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler(successUrl);
    }

    @Bean
    SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler(failureUrl);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    { return authenticationConfiguration.getAuthenticationManager();}

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}


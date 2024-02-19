package com.example.Cat.Shop.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.Cat.Shop.service.CustomUserDetailService;
import static org.springframework.security.config.Customizer.withDefaults;

import java.beans.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	GoogleOauth2SuccessHandler googleOauth2SuccessHandler;
	
	@Autowired
	CustomUserDetailService customUserDetailService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.headers(headers->headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()))
			.authorizeHttpRequests(requests -> requests
					.requestMatchers("/shop","/","/home","/about","/contact","/register","/shop/**","/forgot-password").permitAll()
					.requestMatchers("/admin/**","/admin").hasRole("ADMIN")
					.anyRequest().authenticated()
			)		
			.oauth2Login((o) -> o
					.loginPage("/login").permitAll()
					.successHandler(googleOauth2SuccessHandler)
			)
			.formLogin((form) -> form
					.loginPage("/login").permitAll()
				)
				  .logout((logout) -> logout .permitAll() .deleteCookies("JSESSIONID")
				  .invalidateHttpSession(true) .logoutRequestMatcher(new
				  AntPathRequestMatcher("/logout")) .logoutSuccessUrl("/"))

			;
		
		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth)
	  throws Exception { auth.userDetailsService(customUserDetailService); }


	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web
	      .ignoring()
	      .requestMatchers("/resources/**", "/css/**", "/js/**", "/images/**", "/productImages/**","/resources/static/css/**");
	}
	

	@Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }
	
	
	private ClientRegistration googleClientRegistration() {
		return ClientRegistration.withRegistrationId("google")
			.clientId("814934428768-9dp8fg377ln4dh3rjqe92fraccle0109.apps.googleusercontent.com")
			.clientSecret("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")
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri("http://localhost:8080/login/oauth2/code/google")
			.scope("openid", "profile", "email", "address", "phone")
			.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
			.tokenUri("https://www.googleapis.com/oauth2/v4/token")
			.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
			.userNameAttributeName(IdTokenClaimNames.SUB)
			.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
			.clientName("CatShop")
			.build();
	}
    
}

package com.lms.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.lms.jwt.config.JwtAuthenticationEntryPoint;
import com.lms.jwt.config.JwtRequestFilter;
import com.lms.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	AccessDeniedHandler accessDeniedHandler;

	@Autowired
	BCryptPasswordEncoder encodePassword;

	@Autowired
	private CustomUserDetailsService userDetailsService;

//	@Autowired
//	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//	@Autowired
//	private JwtRequestFilter jwtRequestFilter;

	// @Autowired
	// DataSource dataSource;

	 @Override
	 public void configure(WebSecurity web) throws Exception {
		 web.ignoring().antMatchers("/v2/signup", "/v2/authenticate");
	 }
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(encodePassword);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		http.authorizeRequests().antMatchers("/", "/register", "/saveuser").permitAll().and().authorizeRequests()
				.antMatchers("/secured/**").fullyAuthenticated().anyRequest().hasAnyRole("ADMIN").and().formLogin()
				.permitAll().and().formLogin().successHandler(savedRequestAwareAuthenticationSuccessHandler())
				.loginPage("/login").failureUrl("/login?error").loginProcessingUrl("/login")
				.usernameParameter("username").passwordParameter("password").and().csrf().and().exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler).and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login?logout").and()
				.csrf();

		// .and().rememberMe().tokenRepository(persistentTokenRepository()).tokenValiditySeconds(1209600)

	}

	// Check here to complete the read-me login implementation :
	// https://www.mkyong.com/spring-security/spring-security-remember-me-example/

	@Bean
	public AccessDeniedHandler getAccessDeniedHandler() {
		return new AccessDeniedHandler() {

			@Override
			public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
					throws IOException, ServletException {

				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				// System.out.println(auth.getName());
				response.sendError(403);
			}
		};
	}

	@Bean
	public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler auth = new SavedRequestAwareAuthenticationSuccessHandler();
		auth.setTargetUrlParameter("targetUrl");
		return auth;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}

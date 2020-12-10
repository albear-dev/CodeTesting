package app.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import app.common.security.JwtAuthenticationManager;
import app.common.security.JwtAuthenticationTokenFilter;
import app.common.security.JwtProvider;
import app.common.security.JwtTokenAuthenticationVoter;
import app.common.security.RestAuthenticationEntryPoint;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	JwtProvider jwt;
	@Autowired 
	private JwtAuthenticationManager jwtAuthenticationManager;

	@Bean
	public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(AuthenticationManager authenticationManager, JwtProvider jwt) {
		return new JwtAuthenticationTokenFilter(authenticationManager, jwt);
	}

	@Bean
	public AccessDecisionManager accessDecisionManager() {
	    List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
	        new WebExpressionVoter(),
	        new RoleVoter(),
	        new AuthenticatedVoter(),
	        new JwtTokenAuthenticationVoter());
	    return new UnanimousBased(decisionVoters);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http	.csrf().disable()
				.httpBasic().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)		// jwt토큰 방식으로 검증할것이므로 세션 사용 안함
			.and()
				.authorizeRequests() 										
				.antMatchers("/*/member/join", "/*/member/login").permitAll()					// 가입,로그인은 접근 가능하도록 함.
//				.anyRequest().hasRole("USER") 								
				.anyRequest().authenticated()													// 이 외에는 인가된 사용자만 접근 가능
			    .accessDecisionManager(accessDecisionManager())
			.and()
				.exceptionHandling()
				.authenticationEntryPoint(new RestAuthenticationEntryPoint())					// 인증 오류는 RestAuthenticationEntryPoint 에서 처리함
			.and()
				// UsernamePasswordAuthenticationFilter 전에 jwtAuthenticationManager를 통하여 인증 처리함
				.addFilterBefore(jwtAuthenticationTokenFilter(jwtAuthenticationManager, jwt), UsernamePasswordAuthenticationFilter.class);
			;

	}
    
	@Override 
	public void configure(WebSecurity web) {
		// ignore check swagger resource
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**");

	}
}
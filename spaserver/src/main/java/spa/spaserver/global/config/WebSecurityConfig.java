package spa.spaserver.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spa.spaserver.member.jwt.JwtAuthenticationFilter;
import spa.spaserver.member.jwt.JwtTokenProvider;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate redisTemplate;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.httpBasic().disable()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers("/api/v1/users/sign-up", "/api/v1/users/login", "/api/v1/users/authority",
				"/api/v1/users/reissue", "/api/v1/users/logout", "/api/v1/map/location", "/api/v1/map/charge", "/api/v1/map/route").permitAll()
			.antMatchers("/api/v1/users/userTest").hasRole("USER")
			.antMatchers("/api/v1/users/adminTest").hasRole("ADMIN")
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate),
				UsernamePasswordAuthenticationFilter.class);
		// JwtAuthenticationFilter를 UsernamePasswordAuthentictaionFilter 전에 적용시킨다.
	}

	// 암호화에 필요한 PasswordEncoder Bean 등록
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
package enterprises.inwaiders.plames.assembler.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	public SpringSecurityConfig() {
		super();
		
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        
		http	
			.csrf().disable();

		http
			.authorizeRequests()
			.antMatchers("/resources/**", "/web/controller/**", "/api/**", "/self/**").permitAll();
	
		http
			.authorizeRequests()
			.antMatchers("/**").authenticated();
		
		http
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
		
		http
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login").deleteCookies("JSESSIONID")
			.invalidateHttpSession(true); 
	}
}

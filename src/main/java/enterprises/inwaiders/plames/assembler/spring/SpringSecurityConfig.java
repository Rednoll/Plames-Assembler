package enterprises.inwaiders.plames.assembler.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
			.antMatchers("/**").permitAll();
	}
}

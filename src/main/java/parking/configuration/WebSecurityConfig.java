package parking.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import parking.security.RESTAuthenticationEntryPoint;
import parking.security.RESTAuthenticationFailureHandler;
import parking.security.RESTAuthenticationSuccessHandler;

/**
 * 
 * @author gustavojotz
 *
 */
@Configuration
@EnableTransactionManagement
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) 
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private RESTAuthenticationEntryPoint authenticationEntryPoint;
	
	@Autowired
	private RESTAuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	private RESTAuthenticationSuccessHandler authenticationSuccessHandler;

	@Value("${ldap.login_form}")
	private Boolean loginform;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic();
		http.csrf().disable();//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());;
		http.authorizeRequests().anyRequest().fullyAuthenticated();
		http.formLogin().permitAll();
		if (!loginform) {
			http.formLogin().successHandler(authenticationSuccessHandler);
			http.formLogin().failureHandler(authenticationFailureHandler);
			http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		}
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/health", "/uptime");
	}

	@Configuration
	protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

		@Value("${ldap.url}")
		private String url;
		
		@Value("${ldap.domain}")
		private String domain;

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
		    ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(domain, url);
		    provider.setConvertSubErrorCodesToExceptions(true);
		    provider.setUseAuthenticationRequestCredentials(true);
			auth.authenticationProvider(provider);
		}
		
	}
	
}

package com.productdesc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.productdesc.services.UserDetailsServiceImpl;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	 @Bean 
	 UserDetailsService getUserDetailService() {
		 return new UserDetailsServiceImpl(); 
	 }
	  
	 @Bean 
	 BCryptPasswordEncoder passwordEncoder() { 
		 return new	 BCryptPasswordEncoder(); 
	 }


    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }
	  
	 // override configure(AuthenticationManagerBuilder auth) implemented method
	 @Override 
	 protected void configure(AuthenticationManagerBuilder auth) throws	 Exception { 
		 auth.authenticationProvider(authenticationProvider()); 
	 }
	 
	 @Override 
	 protected void configure(HttpSecurity http) throws Exception {
	 http.authorizeRequests().antMatchers("/user/**").hasAnyRole("USER", "ADMIN", "MANAGER")
	 						 .antMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER")
	 						 .antMatchers("/manager/**").hasRole("MANAGER")
							 .antMatchers("/**").permitAll()
							 .antMatchers(HttpMethod.POST).hasAnyRole("ADMIN", "MANAGER")
							 .antMatchers(HttpMethod.PUT).hasAnyRole("ADMIN", "MANAGER")
							 .antMatchers(HttpMethod.DELETE).hasAnyRole("ADMIN", "MANAGER")
							 .antMatchers(HttpMethod.GET).hasAnyRole("USER", "ADMIN", "MANAGER")
							 .and().formLogin() .loginPage("/signin")
							 .loginProcessingUrl("/dologin") 
							 .defaultSuccessUrl("/user/index")
							 .and().csrf().disable(); }
							 
}

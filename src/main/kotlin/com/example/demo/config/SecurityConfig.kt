package com.example.demo.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class SecurityConfig  : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) : Unit {
        http.csrf().disable()

        http.authorizeRequests()
                .antMatchers("/sections/**").hasRole("API")
                .antMatchers("/import/**").hasRole("API")
                .antMatchers("/export/**").hasRole("API")
                .and().httpBasic()

        http.authorizeRequests()
                //.antMatchers("/api/**").permitAll()
                .antMatchers("/").hasRole("USER").anyRequest().authenticated()
                .and()
                //.formLogin().loginPage("/login").permitAll()
                //.and()
                .logout().permitAll()


    }
    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder): Unit {
        auth.inMemoryAuthentication().withUser("user").password("{noop}1").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}1").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("api").password("{noop}1").roles("API");
    }
}
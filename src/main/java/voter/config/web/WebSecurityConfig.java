package voter.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
class WebSecurityConfig extends GlobalAuthenticationConfigurerAdapter {

    private final String ADMIN_USERNAME = "admin";

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> {
            if (username.isEmpty()) {
                throw new UsernameNotFoundException("could not find the user '"
                        + username + "'");
            }
            String role;
            if (ADMIN_USERNAME.equals(username)) {
                role = "ROLE_ADMIN";
            } else {
                role = "ROLE_USER";
            }
            return new User(username, "", true, true, true, true,
                    AuthorityUtils.createAuthorityList(role));

        };
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfigurator extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().fullyAuthenticated().and().
                httpBasic().and().
                csrf().disable();
    }
}
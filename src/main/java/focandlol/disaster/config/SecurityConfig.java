package focandlol.disaster.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import focandlol.disaster.security.CustomLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestParam;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ObjectMapper objectMapper;
  private final AuthenticationConfiguration authenticationConfiguration;


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(auth -> auth.disable());
    http.httpBasic(auth -> auth.disable());
    http.formLogin(auth -> auth.disable());

    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/login/**", "/message/**", "/signup").permitAll()
        .anyRequest().authenticated());

    http.addFilterAt(new CustomLoginFilter("/login", authenticationConfiguration.getAuthenticationManager(),
        objectMapper
    ), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

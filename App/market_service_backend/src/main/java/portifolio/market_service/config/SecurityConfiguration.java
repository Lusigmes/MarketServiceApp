package portifolio.market_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider jwtAuthProvider;


    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        AuthenticationManager authManager
        ) throws Exception{ //JwtAuthFilter jwtFilter
        http
            .authenticationManager(authManager)
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            // .requestMatchers(HttpMethod.POST , "/usuarios").permitAll()
            .requestMatchers("/auth/registro", "/auth/login").permitAll()
            .requestMatchers("/usuarios/**").permitAll()
            .requestMatchers("/clientes/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/prestadores/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/avaliacoes/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/demandas/**").hasAnyRole("USER", "ADMIN")
            
            
            .anyRequest().authenticated()
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(jwtAuthProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            // .httpBasic(httpBasic->{
            //     httpBasic.realmName("RealmTest");
            // });
            

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET","POST"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }

    /*
    String[] permitAllEndpoints = {
    "/usuarios",
    "/login",
    "/public/**"
        };

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(permitAllEndpoints).permitAll()
            .anyRequest().authenticated()
        ); 
*/
}

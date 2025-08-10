package portifolio.market_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;
import portifolio.market_service.repository.UsuarioRepository;

@Configuration
@AllArgsConstructor
public class SecurityConfiguration {
    private final UsuarioRepository usuarioRepository;

    @Bean
    UserDetailsService usuarioService(){
        return email -> usuarioRepository.findByEmail(email)
        .orElseThrow(()-> new UsernameNotFoundException("USUARIO NÃO ENCONTRADO"));
    }
    
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        AuthenticationManager authManager
        ) throws Exception{ //JwtAuthFilter jwtFilter
        http
            .authenticationManager(authManager)
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers(HttpMethod.POST , "/usuarios").permitAll()
                .requestMatchers("/usuarios/**").permitAll()
                .requestMatchers("/clientes/**").permitAll()
                .requestMatchers("/prestadores/**").permitAll()

            
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic->{
                httpBasic.realmName("RealmTest");
            });
            // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            

        return http.build();
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

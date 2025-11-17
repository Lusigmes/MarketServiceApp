package portifolio.market_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- aqui
            .authorizeHttpRequests(auth -> auth
            // .requestMatchers(HttpMethod.POST , "/usuarios").permitAll()
            .requestMatchers("/auth/registro", "/auth/login").permitAll()
            .requestMatchers("/usuarios/**").hasAnyRole("USER","ADMIN")
            .requestMatchers("/clientes/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/prestadores/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/avaliacoes/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/demandas/**").hasAnyRole("USER", "ADMIN")
            // escolha de roles que podem trabalhar em detemrinados metodos
            
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

        configuration.setAllowedOrigins(List.of("http://localhost:5173")); //      http://localhost:8080 //   "http://localhost:5173", // Vue Vite         "http://localhost:3000"  // alternativa
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); 
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));
    // configuration.setAllowCredentials(true); // se quiser cookies no futuro
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

}

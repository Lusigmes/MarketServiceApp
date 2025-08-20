package portifolio.market_service.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.AllArgsConstructor;
import portifolio.market_service.repository.UsuarioRepository;

@Configuration
@AllArgsConstructor
public class ApplicationConfiguration {
    
    private final UsuarioRepository usuarioRepository;


    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("usuarios");
    }
    
    @Bean
    @Lazy
    UserDetailsService userDetailsService(){
        return email -> {
            System.out.println("[Spring Security] Carregando usuário pelo email: " + email);
            return usuarioRepository.findByEmailWithRelations(email)
                .orElseThrow(() -> new UsernameNotFoundException("USUARIO NÃO ENCONTRADO"));
        };
        // return email -> usuarioRepository.findByEmail(email)
        // .orElseThrow(()-> new UsernameNotFoundException("USUARIO NÃO ENCONTRADO"));
    }
    
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
    
    @Bean
    AuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}

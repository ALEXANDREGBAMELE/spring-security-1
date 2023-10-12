package com.example.securytyapp.config;

import com.example.securytyapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration de l'application Spring Security:
 * <ul>
 *     <li>Configuration du service d'authentification</li>
 *     <li>Configuration du gestionnaire d'authentification</li>
 *     <li>Configuration du fournisseur d'authentification</li>
 *     <li>Configuration de l'encodeur de mot de passe</li>
 *     <li>Configuration du service de détails utilisateur</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;


    /**
     * Configuration du service de détails utilisateur
     * @return le service de détails utilisateur
     */

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

    }

    /**
     * Configuration du fournisseur d'authentification
     * @return le fournisseur d'authentification
     */

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    /**
     * Configuration du gestionnaire d'authentification
     * @param config: la configuration d'authentification
     * @return le gestionnaire d'authentification
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuration de l'encodeur de mot de passe avec l'algorithme BCrypt
     * @return l'encodeur de mot de passe
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

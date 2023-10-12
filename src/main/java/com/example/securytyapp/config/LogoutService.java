package com.example.securytyapp.config;

import com.example.securytyapp.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;


/**
 * Service de déconnexion
 * <p>
 *     Ce service permet de déconnecter un utilisateur en invalidant son token.
 *     Pour cela, il implémente l'interface {@link LogoutHandler} de Spring Security.
 *     Cette interface permet de définir une action à effectuer lors de la déconnexion d'un utilisateur.
 *     Dans notre cas, on invalide le token de l'utilisateur.
 *     Pour cela, on récupère le token dans le header de la requête, on le recherche dans la base de données,
 *    on le met à jour en le marquant comme expiré et révoqué, puis on le sauvegarde.
 *    Enfin, on vide le contexte de sécurité.
 *    Ce service est utilisé dans la classe {@link SecurityConfig} pour configurer Spring Security.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;


    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElseThrow(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}

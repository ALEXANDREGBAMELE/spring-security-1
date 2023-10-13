package com.example.securytyapp.auths;

import com.example.securytyapp.config.JwtService;
import com.example.securytyapp.domaines.Token;
import com.example.securytyapp.domaines.User;
import com.example.securytyapp.enums.Role;
import com.example.securytyapp.enums.TokenType;
import com.example.securytyapp.repository.TokenRepository;
import com.example.securytyapp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(@Validated RegisterRequest request)
            throws BadCredentialsException {
        if (request.getRole() == null) {
            request.setRole(Role.USER);
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        if (user == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        boolean userExists = repository.findByEmail(user.getEmail()).isPresent();
        if (userExists) {
            throw new BadCredentialsException("User already exists");
        }



        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }




    public AuthenticationResponse authenticate(
            @Validated
            AuthenticationRequest request
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials");
        }
        var user = repository.findByEmail(request.getEmail()).orElseThrow(null);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        savedUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }

    public void savedUserToken(
            @NonNull User user,
            @NonNull String jwtToken
    ) {

        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        if (token == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                var jwtToken = jwtService.generateToken(user);
                var newRefreshToken = jwtService.generateRefreshToken(user);
                revokeAllUserTokens(user);
                savedUserToken(user, jwtToken);

                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(newRefreshToken)
                        .build();

                if (authResponse == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }

                new ObjectMapper()
                        .writeValue(
                                response.getOutputStream(),
                                authResponse
                        );
            }


        }
    }

}

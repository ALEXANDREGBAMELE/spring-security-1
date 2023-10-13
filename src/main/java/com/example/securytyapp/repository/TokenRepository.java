package com.example.securytyapp.repository;

import com.example.securytyapp.domaines.Token;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Hidden
public interface TokenRepository extends JpaRepository<Token, Long> {


    /**
     * Trouver tous les tokens valides d'un utilisateur donné
     * @param id l'id de l'utilisateur
     * @return la liste des tokens valides
     */
    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByToken(String token);
}


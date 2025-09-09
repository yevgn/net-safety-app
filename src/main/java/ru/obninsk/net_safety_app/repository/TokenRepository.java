package ru.obninsk.net_safety_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.obninsk.net_safety_app.entity.Token;
import ru.obninsk.net_safety_app.entity.TokenMode;
import ru.obninsk.net_safety_app.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Transactional
//    @Query(value = "UPDATE Token SET revoked = true, expired = true" +
//            " WHERE user.email = :email")
//    int revokeAllByUserEmail(String email);

    Optional<Token> findByToken(String token);

    @Query("""
           SELECT t.user FROM Token t WHERE t.token = :token
           """)
    Optional<User> findUserByToken(String token);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE Token SET revoked = true, expired = true" +
            " WHERE user.email = :email AND tokenMode IN :modes")
    int revokeAllUsersTokensByTokenModeIn(@Param("email") String email, @Param("modes") List<TokenMode> modes);

    @Query("""
            SELECT t FROM Token t WHERE t.user.email = :user_email
            AND t.revoked = false AND t.expired = false
            """)
    List<Token> findByUserEmailNotRevokedAndNotExpired(@Param("user_email") String userEmail);

}
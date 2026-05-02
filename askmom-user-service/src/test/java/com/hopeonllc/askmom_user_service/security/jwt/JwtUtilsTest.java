package com.hopeonllc.askmom_user_service.security.jwt;

import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.security.user.AskmomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private static final String JWT_SECRET =
            "bXlEZXZlbG9wbWVudEp3dFNlY3JldEtleU15RGV2ZWxvcG1lbnRKd3RTZWNyZXRLZXk=";

    @Test
    void generateAndParseToken_shouldReturnExpectedUsername() {
        JwtUtils jwtUtils = buildJwtUtils(60_000);
        Authentication authentication = buildAuthentication("jwt-user@test.com");

        String token = jwtUtils.generateJwtTokenForUser(authentication);

        assertTrue(jwtUtils.validateToken(token));
        assertEquals("jwt-user@test.com", jwtUtils.getUserNameFromToken(token));
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenExpired() {
        JwtUtils jwtUtils = buildJwtUtils(-1);
        Authentication authentication = buildAuthentication("expired@test.com");

        String token = jwtUtils.generateJwtTokenForUser(authentication);

        assertFalse(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenMalformed() {
        JwtUtils jwtUtils = buildJwtUtils(60_000);

        assertFalse(jwtUtils.validateToken("not-a-jwt"));
    }

    private JwtUtils buildJwtUtils(int expirationMillis) {
        JwtUtils jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationMillis);
        return jwtUtils;
    }

    private Authentication buildAuthentication(String email) {
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPasswordHash("$2a$10$abcdefghijklmnopqrstuv");
        AskmomUserDetails userDetails = AskmomUserDetails.buildUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

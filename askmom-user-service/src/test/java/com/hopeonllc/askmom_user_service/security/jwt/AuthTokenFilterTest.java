package com.hopeonllc.askmom_user_service.security.jwt;

import com.hopeonllc.askmom_user_service.security.user.AskmomUserDetails;
import com.hopeonllc.askmom_user_service.security.user.AskmomUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthTokenFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldAuthenticateUser_whenTokenIsValid() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        AskmomUserDetailsService userDetailsService = mock(AskmomUserDetailsService.class);
        AuthTokenFilter filter = new AuthTokenFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(filter, "userDetailsService", userDetailsService);

        UserDetails userDetails = User.withUsername("filter@test.com")
                .password("secret")
                .roles("USER")
                .build();
        when(jwtUtils.validateToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUserNameFromToken("valid-token")).thenReturn("filter@test.com");
        when(userDetailsService.loadUserByUsername("filter@test.com")).thenReturn(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("filter@test.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(userDetailsService).loadUserByUsername("filter@test.com");
    }

    @Test
    void doFilterInternal_shouldSkipAuthentication_whenHeaderMissing() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        AskmomUserDetailsService userDetailsService = mock(AskmomUserDetailsService.class);
        AuthTokenFilter filter = new AuthTokenFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(filter, "userDetailsService", userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

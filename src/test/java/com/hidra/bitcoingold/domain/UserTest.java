package com.hidra.bitcoingold.domain;

import com.hidra.bitcoingold.util.UserCreator;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void getAuthorities_shouldReturnAdminAndUserRoles_WhenUserIsAdmin() {
        User user = UserCreator.createAdminUser();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));;
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals(2, authorities.size());
    }

    @Test
    void getAuthorities_shouldReturnUserRole_WhenUserIsNotAdmin() {
        User user = UserCreator.createValidUser();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals(1, authorities.size());
    }

}
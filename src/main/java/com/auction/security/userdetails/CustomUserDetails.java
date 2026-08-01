package com.auction.security.userdetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.auction.entity.Permission;
import com.auction.entity.Role;
import com.auction.entity.User;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return user.getRoles()
                .stream()
                .flatMap(role -> buildAuthorities(role).stream())
                .collect(Collectors.toSet());

    }

    private Set<GrantedAuthority> buildAuthorities(Role role) {

        Set<GrantedAuthority> authorities =
                role.getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !Boolean.TRUE.equals(user.getAccountExpired());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(user.getAccountLocked());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !Boolean.TRUE.equals(user.getCredentialsExpired());
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getEnabled());
    }
}
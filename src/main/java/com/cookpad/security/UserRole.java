package com.cookpad.security;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.cookpad.security.UserPermission.*;


public enum UserRole {
    USER(Sets.newHashSet(
            USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE,
            POST_READ
    )),
    ADMIN(Sets.newHashSet(
            USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE,
            POST_READ, POST_CREATE, POST_UPDATE, POST_DELETE
    ));

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getAllPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        return getAllPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
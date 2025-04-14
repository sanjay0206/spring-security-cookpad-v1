package com.cookpad.security;


import lombok.Getter;

@Getter
public enum UserPermission {
    POST_READ("SCOPE_recipe:read"),
    POST_UPDATE("SCOPE_recipe:update"),
    POST_CREATE("SCOPE_recipe:create"),
    POST_DELETE("SCOPE_recipe:delete"),

    USER_READ("SCOPE_user:read"),
    USER_UPDATE("SCOPE_user:update"),
    USER_CREATE("SCOPE_user:create"),
    USER_DELETE("SCOPE_user:delete");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }
}

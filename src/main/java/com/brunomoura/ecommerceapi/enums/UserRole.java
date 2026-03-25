package com.brunomoura.ecommerceapi.enums;

public enum UserRole {

    ADMIN,
    USER;

    private String authorities;

    public String getAuthorities() {
        return authorities;
    }
}

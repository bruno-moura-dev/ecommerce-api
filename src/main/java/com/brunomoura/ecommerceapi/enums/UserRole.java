package com.brunomoura.ecommerceapi.enums;

public enum UserRole {

    ADMIN,
    USER;

    public String getAuthorities() {
        return "ROLE_" + this.name();
    }
}

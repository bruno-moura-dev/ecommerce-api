package com.brunomoura.ecommerceapi.utils.formatter;

public final class EmailUtils {
    public static final String MASK = "********";

    public static String maskEmail(String email) {
        if (email == null) {
            return null;
        }

        int atIndex = email.indexOf("@");

        if (atIndex <= 0 || atIndex == (email.length() -1)) {
            return email;
        }

        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (name.length() <= 2) {
            return email;
        }

        return name.substring(0, 2) + MASK + domain;
    }
}

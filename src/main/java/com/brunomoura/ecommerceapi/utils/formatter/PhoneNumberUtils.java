package com.brunomoura.ecommerceapi.utils.formatter;

public final class PhoneNumberUtils {

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        String clean = phoneNumber.replaceAll("\\D", "");

        if (clean.startsWith("55") && clean.length() > 11) {
            clean = clean.substring(2);
        }

        int length = clean.length();
        if (length < 10) {
            return phoneNumber;
        }

        String ddd = clean.substring(0, 2);
        String finalPart = clean.substring(length - 4);

        if (length == 11) {
            return "(" + ddd + ")" + " 9****-" + finalPart;
        } else {
            return "(" + ddd + ")" + " ****-" + finalPart;
        }
    }
}

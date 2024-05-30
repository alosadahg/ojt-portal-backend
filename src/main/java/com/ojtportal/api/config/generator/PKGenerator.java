package com.ojtportal.api.config.generator;

import java.security.SecureRandom;

public class PKGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final String OTP = "0123456789";

    private static final SecureRandom random = new SecureRandom();

    public static String generate(String type) {
        int length = (type.equalsIgnoreCase("verification")) ? 8 : 5;
        StringBuilder stringBuilder = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            int randomIndex = random.nextInt((type.equalsIgnoreCase("otp")) ? OTP.length() : CHARACTERS.length());
            if (type.equalsIgnoreCase("otp")) stringBuilder.append(OTP.charAt(randomIndex));
            else stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
}

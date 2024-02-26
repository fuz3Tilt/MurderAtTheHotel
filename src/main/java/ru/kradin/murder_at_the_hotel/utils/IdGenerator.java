package ru.kradin.murder_at_the_hotel.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class IdGenerator {
    public static String generate() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] idBytes = new byte[20];
        secureRandom.nextBytes(idBytes);
        return Base64.getEncoder().withoutPadding().encodeToString(idBytes);
    }

    public static String generateForButton() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] idBytes = new byte[8];
        secureRandom.nextBytes(idBytes);
        return Base64.getEncoder().withoutPadding().encodeToString(idBytes);
    }
}

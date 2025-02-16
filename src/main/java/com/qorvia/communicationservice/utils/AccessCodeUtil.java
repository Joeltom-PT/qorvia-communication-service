package com.qorvia.communicationservice.utils;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Component
public class AccessCodeUtil {

    private static final String SECRET = "your-secret-key";
    private static final String ALGORITHM = "AES";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateAccessCodePublic(String eventId, String startTime, Long organizerId) {
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, FORMATTER);
        return generateAccessCode(eventId, parsedStartTime, organizerId);
    }

    private String generateAccessCode(String eventId, LocalDateTime startTime, Long organizerId) {
        try {
            String data = eventId + "|" + startTime.format(FORMATTER) + "|" + organizerId;
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toCustomEncoding(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating access code", e);
        }
    }

    private SecretKey getSecretKey() {
        try {
            byte[] key = SECRET.getBytes(StandardCharsets.UTF_8);
            if (key.length != 16 && key.length != 24 && key.length != 32) {
                key = Arrays.copyOf(key, 16);
            }
            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    private String toCustomEncoding(byte[] bytes) {
        StringBuilder encoded = new StringBuilder();
        for (byte b : bytes) {
            int index = (b & 0xFF) % CHARSET.length();
            encoded.append(CHARSET.charAt(index));
        }
        return encoded.toString();
    }
}

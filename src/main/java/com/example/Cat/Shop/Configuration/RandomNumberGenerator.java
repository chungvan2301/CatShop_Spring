package com.example.Cat.Shop.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class RandomNumberGenerator {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String generateRandomPassword() {
        String randomPassword = generateRandomString(10);
        String hashedPassword = passwordEncoder.encode(randomPassword);
        return hashedPassword;
    }

    private static String generateRandomString(int length) {
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomNumber = (int) (Math.random() * 10);
            randomString.append(randomNumber);
        }
        return randomString.toString();
    }
}

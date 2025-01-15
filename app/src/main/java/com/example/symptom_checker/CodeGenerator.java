package com.example.symptom_checker;

import java.util.Random;

public class CodeGenerator {
    public static String generateRandomCode() {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            code.append(charset.charAt(random.nextInt(charset.length())));
        }
        return code.toString();
    }
}

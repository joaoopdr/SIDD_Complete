package com.example.symptom_checker;

import java.util.HashMap;
import java.util.Map;

public class SymptomStorage {
    private static Map<String, Boolean> storedSymptoms = new HashMap<>();

    public static void storeSymptom(String symptom, boolean hasSymptom) {
        storedSymptoms.put(symptom.toLowerCase(), hasSymptom);
    }

    public static boolean hasSymptom(String symptom) {
        return storedSymptoms.getOrDefault(symptom.toLowerCase(), false);
    }

    public static Map<String, Boolean> getStoredSymptoms() {
        return storedSymptoms;
    }
}
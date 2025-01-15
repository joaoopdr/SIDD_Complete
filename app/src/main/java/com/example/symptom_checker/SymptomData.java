package com.example.symptom_checker;

import java.util.List;

public class SymptomData {
    private String main_symptom;
    private int num_days;
    private int intensity;
    private List<String> additional_symptoms;

    // Construtor
    public SymptomData(String main_symptom, int num_days, int intensity, List<String> additional_symptoms) {
        this.main_symptom = main_symptom;
        this.num_days = num_days;
        this.intensity = intensity;
        this.additional_symptoms = additional_symptoms;
    }

    // Getters e Setters
    public String getMainSymptom() {
        return main_symptom;
    }

    public void setMainSymptom(String main_symptom) {
        this.main_symptom = main_symptom;
    }

    public int getNumDays() {
        return num_days;
    }

    public void setNumDays(int num_days) {
        this.num_days = num_days;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public List<String> getAdditionalSymptoms() {
        return additional_symptoms;
    }

    public void setAdditionalSymptoms(List<String> additional_symptoms) {
        this.additional_symptoms = additional_symptoms;
    }
}
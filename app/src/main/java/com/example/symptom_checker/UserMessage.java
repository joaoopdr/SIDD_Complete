package com.example.symptom_checker;

public class UserMessage {
    private String main_symptom;
    private String selected_symptom;
    private int num_days;
    private int intensity;
    private String additional_symptom;
    private String answer;

    // Getters and setters

    public String getMain_symptom() {
        return main_symptom;
    }

    public void setMain_symptom(String main_symptom) {
        this.main_symptom = main_symptom;
    }

    public String getSelected_symptom() {
        return selected_symptom;
    }

    public void setSelected_symptom(String selected_symptom) {
        this.selected_symptom = selected_symptom;
    }

    public int getNum_days() {
        return num_days;
    }

    public void setNum_days(int num_days) {
        this.num_days = num_days;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String getAdditional_symptom() {
        return additional_symptom;
    }

    public void setAdditional_symptom(String additional_symptom) {
        this.additional_symptom = additional_symptom;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
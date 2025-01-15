package com.example.symptom_checker;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse {
    @SerializedName("message")
    public String message;

    @SerializedName("suggestions")
    public List<String> suggestions;

    @SerializedName("question")
    public String question;

    @SerializedName("diagnosis")
    public String diagnosis;

    @SerializedName("symptom_duration")
    public String symptomDuration;

    @SerializedName("symptom_intensity")
    public String symptomIntensity;

    @SerializedName("symptoms_absent")
    public String symptomsAbsent;

    @SerializedName("symptoms_present")
    public String symptomsPresent;

    @Override
    public String toString() {
        return "Question: " + question + ", Message: " + message + ", Suggestions: " + suggestions;
    }
}
package com.example.symptom_checker;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class RequestBody {

    @SerializedName("main_symptom")
    public String mainSymptom;

    @SerializedName("selected_symptom")
    public String selectedSymptom;

    @SerializedName("num_days")
    public Integer numDays;

    @SerializedName("intensity")
    public Integer intensity;

    @SerializedName("additional_symptom")
    public String additionalSymptom;

    @SerializedName("answer")
    public String answer;

    @NonNull
    @Override
    public String toString() {
        return "RequestBody{" +
                "mainSymptom='" + mainSymptom + '\'' +
                ", selectedSymptom='" + selectedSymptom + '\'' +
                ", numDays=" + numDays +
                ", intensity=" + intensity +
                ", additionalSymptom='" + additionalSymptom + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
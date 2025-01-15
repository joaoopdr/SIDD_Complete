package com.example.symptom_checker;

import java.io.Serializable;
import java.util.Random;

public class Prescription implements Serializable {
    private String medicineName;
    private String dosage;
    private String frequency;
    private String description;
    private String doctorName;
    private String doctorCRM;
    private String date;
    private String validity;
    private String status;
    private String dateAdded;
    private String qrCodeId;

    // Construtor vazio necessário para Firebase
    public Prescription() {}

    public Prescription(String medicineName, String dosage, String frequency, String description,
                        String doctorName, String doctorCRM, String date, String validity, String status) {
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.description = description;
        this.doctorName = doctorName;
        this.doctorCRM = doctorCRM;
        this.date = date;
        this.validity = validity;
        this.status = status;
        this.dateAdded = java.time.LocalDate.now().toString();
        this.qrCodeId = generateQRCodeId(); // Gera um ID único para o QR Code
    }

    // Getters e Setters existentes
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorCRM() { return doctorCRM; }
    public void setDoctorCRM(String doctorCRM) { this.doctorCRM = doctorCRM; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getValidity() { return validity; }
    public void setValidity(String validity) { this.validity = validity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Novos getters e setters para dateAdded e qrCodeId
    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public String getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(String qrCodeId) { this.qrCodeId = qrCodeId; }

    // Método para gerar um ID único para o QR Code
    private String generateQRCodeId() {
        Random random = new Random();
        return "QR" + String.format("%06d", random.nextInt(1000000));
    }
}
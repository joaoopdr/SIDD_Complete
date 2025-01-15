package com.example.symptom_checker;

import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class Hospital {
    private String name;
    private double latitude;
    private double longitude;
    private int queueTime;
    private View decorView;

    public Hospital(String name, double latitude, double longitude, int queueTime) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.queueTime = queueTime;


    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(int queueTime) {
        this.queueTime = queueTime;
    }

    public static void initializeHospitals(ArrayList<Hospital> hospitals) {
        Random random = new Random();

        hospitals.add(new Hospital("UPA de Novo Hamburgo", -29.6888, -51.1328, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Bom Princípio", -29.4858, -51.3542, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Vacaria", -28.5128, -50.9339, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Santa Maria", -29.6842, -53.8069, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Canoas", -29.9176, -51.1839, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Canoas", -29.9176, -51.1839, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Porto Alegre", -30.0346, -51.2177, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Lajeado", -29.4661, -51.9616, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Bagé", -31.3314, -54.1064, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Venâncio Aires", -29.6149, -52.1934, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Santa Rosa", -27.8702, -54.4818, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Cruz Alta", -28.6382, -53.6068, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Alegrete", -29.7835, -55.7918, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Bento Gonçalves", -29.1662, -51.5165, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Viamão", -30.0807, -51.0194, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Sapiranga", -29.6380, -51.0067, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Caxias do Sul", -29.1678, -51.1794, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Carazinho", -28.2833, -52.7875, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Uruguaiana", -29.7614, -57.0853, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Erechim", -27.6346, -52.2751, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Santo Ângelo", -28.3006, -54.2631, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de São Borja", -28.6600, -56.0033, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Santa Cruz do Sul", -29.7181, -52.4259, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Ijuí", -28.3878, -53.9146, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Frederico Westphalen", -27.3596, -53.3953, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Três Passos", -27.4554, -53.9297, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de São Leopoldo", -29.7542, -51.1500, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Alvorada", -29.9914, -51.0809, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Cachoeira do Sul", -30.0390, -52.8944, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Tramandaí", -29.9842, -50.1320, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Camaquã", -30.8511, -51.8126, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Gravataí", -29.9441, -50.9918, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Gravataí", -29.9441, -50.9918, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Pelotas", -31.7649, -52.3371, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Pelotas", -31.7649, -52.3371, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Esteio", -29.8527, -51.1841, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Farroupilha", -29.2221, -51.3419, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Parobé", -29.6249, -50.8318, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de São Jerônimo", -29.9590, -51.7254, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Sapucaia do Sul", -29.8315, -51.1450, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Capão da Canoa", -29.7484, -50.0064, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Cachoeirinha", -29.9489, -51.0932, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Panambi", -28.2837, -53.5016, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Torres", -29.3352, -49.7265, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Novo Hamburgo", -29.6888, -51.1328, random.nextInt(50)));
        hospitals.add(new Hospital("UPA de Guaíba", -30.1089, -51.3231, random.nextInt(50)));
    }

    public static void updateQueueTimes(ArrayList<Hospital> hospitals) {
        Random random = new Random();
        for (Hospital hospital : hospitals) {
            hospital.setQueueTime(random.nextInt(50));
        }
    }
}

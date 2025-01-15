package com.example.symptom_checker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultsActivity extends AppCompatActivity {

    private TextView resultsTitle;
    private View decorView;
    private LinearLayout resultsContainer;
    private Button backButton;
    private Button startMeetButton;

    private Map<String, Boolean> answers;
    private Map<String, List<String>> diseaseSymptoms;
    private Map<String, String> diseaseTreatments;
    private List<Map.Entry<String, Double>> sortedResults;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultsTitle = findViewById(R.id.resultsTitle);
        resultsContainer = findViewById(R.id.resultsContainer);
        backButton = findViewById(R.id.backButton);
        startMeetButton = findViewById(R.id.startMeetButton);

        answers = (HashMap<String, Boolean>) getIntent().getSerializableExtra("answers");

        decorView = getWindow().getDecorView();

        // Inicializa Firebase Auth e Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadDiseaseData();
        loadTreatments();

        Map<String, Double> confidenceLevels = calculateConfidenceLevels(answers);
        displayResults(confidenceLevels);

        // Ação para o botão de voltar
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, ServiceSelectionActivity.class);
            startActivity(intent);
            finish();
        });

        // Ação para o botão de iniciar reunião
        startMeetButton.setOnClickListener(v -> {
            // Certifique-se de que o usuário está autenticado antes de continuar
            String userId = getCurrentUserId();
            if (userId == null) return; // Se não estiver autenticado, não prosseguir

            // Capture o diagnóstico principal (primeiro da lista ordenada)
            String mainDiagnosis = sortedResults.isEmpty() ? "Nenhum diagnóstico" : sortedResults.get(0).getKey();

            // Capture os sintomas apresentados
            List<String> presentSymptoms = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : answers.entrySet()) {
                if (entry.getValue()) {
                    presentSymptoms.add(entry.getKey());
                }
            }

            // Prepare os dados para upload no Firestore
            Map<String, Object> diagnosisData = new HashMap<>();
            diagnosisData.put("diagnosis", mainDiagnosis);
            diagnosisData.put("symptoms", presentSymptoms);
            diagnosisData.put("timestamp", Timestamp.now());

            // Salve os dados no Firebase Firestore no caminho correto
            firestore.collection("users").document(userId)
                    .collection("diagnosis")
                    .add(diagnosisData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ResultsActivity.this, "Diagnóstico salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        // Sucesso: Navegue para a atividade de reunião
                        Intent meetIntent = new Intent(ResultsActivity.this, MeetingActivity.class);
                        startActivity(meetIntent);
                    })
                    .addOnFailureListener(e -> {
                        // Falha: Exiba uma mensagem de erro ou faça outra ação apropriada
                        Toast.makeText(ResultsActivity.this, "Erro ao salvar no Firestore", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // Método para obter o UID do usuário autenticado, com verificação de autenticação
    private String getCurrentUserId() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Carrega os dados das doenças e seus sintomas
    private void loadDiseaseData() {
        diseaseSymptoms = new HashMap<>();
        diseaseTreatments = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            JSONArray diseases = jsonObject.getJSONArray("doenças");
            for (int i = 0; i < diseases.length(); i++) {
                JSONObject disease = diseases.getJSONObject(i);
                String diseaseName = disease.getString("nome");
                JSONArray symptomsArray = disease.getJSONArray("sintomas");
                List<String> symptoms = new ArrayList<>();
                for (int j = 0; j < symptomsArray.length(); j++) {
                    symptoms.add(symptomsArray.getString(j).trim());
                }
                diseaseSymptoms.put(diseaseName, symptoms);
                if (disease.has("treatment")) {
                    diseaseTreatments.put(diseaseName, disease.getString("treatment"));
                } else {
                    diseaseTreatments.put(diseaseName, "Tratamento não especificado");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Ler JSON dos ativos
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("diseases.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // Carregar os tratamentos de doenças
    private void loadTreatments() {
        DecisionTree decisionTree = new DecisionTree(this);
        diseaseTreatments = decisionTree.getTreatments();
    }

    // Calcula os níveis de confiança com base nas respostas fornecidas
    private Map<String, Double> calculateConfidenceLevels(Map<String, Boolean> answers) {
        Map<String, Double> confidenceLevels = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : diseaseSymptoms.entrySet()) {
            String disease = entry.getKey();
            List<String> symptoms = entry.getValue();
            int matchCount = 0;
            for (String symptom : symptoms) {
                if (answers.getOrDefault(symptom, false)) {
                    matchCount++;
                }
            }
            double confidence = symptoms.size() > 0 ? (double) matchCount / symptoms.size() : 0;
            confidenceLevels.put(disease, confidence);
        }
        return confidenceLevels;
    }

    // Exibe os resultados calculados na interface
    private void displayResults(Map<String, Double> confidenceLevels) {
        sortedResults = new ArrayList<>(confidenceLevels.entrySet());
        sortedResults.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        if (!sortedResults.isEmpty()) {
            sortedResults.removeIf(entry -> entry.getValue() == 0.0);

            if (!sortedResults.isEmpty()) {
                Map.Entry<String, Double> mainDiagnosis = sortedResults.remove(0);
                String mainDiagnosisText = String.format("%s: %.1f%% de confiança",
                        mainDiagnosis.getKey(),
                        mainDiagnosis.getValue() * 100);
                addResult(mainDiagnosisText, mainDiagnosis.getKey());
            }

            for (Map.Entry<String, Double> entry : sortedResults) {
                String resultText = String.format("%s: %.1f%% de confiança",
                        entry.getKey(),
                        entry.getValue() * 100);
                addResult(resultText, entry.getKey());
            }
        }
    }

    // Adiciona os resultados à interface
    private void addResult(String text, String disease) {
        View resultView = getLayoutInflater().inflate(R.layout.result_item, null);
        TextView resultTextView = resultView.findViewById(R.id.resultTextView);
        LinearLayout detailsContainer = resultView.findViewById(R.id.detailsContainer);

        resultTextView.setText(text);

        List<String> symptoms = diseaseSymptoms.get(disease);
        List<String> presentSymptoms = new ArrayList<>();
        List<String> absentSymptoms = new ArrayList<>();
        for (String symptom : symptoms) {
            if (answers.getOrDefault(symptom, false)) {
                presentSymptoms.add(symptom);
            } else {
                absentSymptoms.add(symptom);
            }
        }

        TextView presentSymptomsTextView = resultView.findViewById(R.id.presentSymptoms);
        presentSymptomsTextView.setText("Sintomas presentes: " + String.join(", ", presentSymptoms));

        TextView absentSymptomsTextView = resultView.findViewById(R.id.absentSymptoms);
        absentSymptomsTextView.setText("Sintomas ausentes: " + String.join(", ", absentSymptoms));

        String treatment = diseaseTreatments.get(disease);
        TextView treatmentTextView = resultView.findViewById(R.id.treatmentTextView);
        treatmentTextView.setText("Tratamento: " + (treatment != null ? treatment : "Não especificado"));

        resultView.setOnClickListener(v -> {
            if (detailsContainer.getVisibility() == View.VISIBLE) {
                detailsContainer.setVisibility(View.GONE);
            } else {
                detailsContainer.setVisibility(View.VISIBLE);
            }
        });

        detailsContainer.setVisibility(View.GONE);

        resultsContainer.addView(resultView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}
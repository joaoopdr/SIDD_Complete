package com.example.symptom_checker;

import android.content.Context;
import android.content.res.AssetManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecisionTree {

    private DecisionTreeNode root;
    private Map<String, List<String>> diseaseSymptoms;
    private Map<String, String> treatments;
    private Map<String, Double> diseaseProbabilities;

    public DecisionTree(Context context) {
        loadFromJson(context);
        loadTreatments(context);
        diseaseSymptoms = new HashMap<>();
        diseaseProbabilities = new HashMap<>();
        populateDiseaseSymptoms(root, new ArrayList<>());
        calculateDiseaseProbabilities();
    }

    private void loadFromJson(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("decision_tree_model.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject decisionTree = new JSONObject(jsonString);
            root = parseJsonToNode(decisionTree);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private DecisionTreeNode parseJsonToNode(JSONObject jsonObject) throws JSONException {
        DecisionTreeNode node = new DecisionTreeNode();

        if (jsonObject.has("question")) {
            node.setQuestion(jsonObject.getString("question"));
            node.setYesNode(parseJsonToNode(jsonObject.getJSONObject("yes")));
            node.setNoNode(parseJsonToNode(jsonObject.getJSONObject("no")));
        } else if (jsonObject.has("diagnosis")) {
            node.setDiagnosis(jsonObject.getString("diagnosis"));
        }

        return node;
    }

    public DecisionTreeNode getRoot() {
        return root;
    }

    public DecisionTreeNode nextNode(DecisionTreeNode currentNode, Map<String, Boolean> answers) {
        if (currentNode.isLeaf()) {
            return currentNode;
        }

        if (answers.containsKey(currentNode.getQuestion())) {
            if (answers.get(currentNode.getQuestion())) {
                return currentNode.getYesNode();
            } else {
                return currentNode.getNoNode();
            }
        }
        return currentNode;
    }

    public String getDiagnosis(Map<String, Boolean> answers) {
        return traverseTree(root, answers);
    }

    private String traverseTree(DecisionTreeNode node, Map<String, Boolean> answers) {
        if (node.isLeaf()) {
            return node.getDiagnosis();
        }

        String question = node.getQuestion();
        if (answers.getOrDefault(question, false)) {
            return traverseTree(node.getYesNode(), answers);
        } else {
            return traverseTree(node.getNoNode(), answers);
        }
    }

    public Map<String, Integer> compareSymptoms(Map<String, Boolean> answers) {
        Map<String, Integer> matchCounts = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : diseaseSymptoms.entrySet()) {
            String disease = entry.getKey();
            List<String> symptoms = entry.getValue();
            int matchCount = 0;
            for (String symptom : symptoms) {
                if (answers.getOrDefault(symptom, false)) {
                    matchCount++;
                }
            }
            matchCounts.put(disease, matchCount);
        }
        return matchCounts;
    }

    public List<String> getDiseaseSymptoms(String disease) {
        return diseaseSymptoms.getOrDefault(disease, new ArrayList<>());
    }

    private void populateDiseaseSymptoms(DecisionTreeNode node, List<String> currentSymptoms) {
        if (node.isLeaf()) {
            diseaseSymptoms.put(node.getDiagnosis(), new ArrayList<>(currentSymptoms));
        } else {
            List<String> yesSymptoms = new ArrayList<>(currentSymptoms);
            yesSymptoms.add(node.getQuestion());
            populateDiseaseSymptoms(node.getYesNode(), yesSymptoms);

            populateDiseaseSymptoms(node.getNoNode(), currentSymptoms);
        }
    }

    public int getTotalSymptomsForDiagnosis(String diagnosis) {
        List<String> symptoms = diseaseSymptoms.get(diagnosis);
        return symptoms != null ? symptoms.size() : 0;
    }

    public String getTreatment(String diagnosis) {
        return treatments.getOrDefault(diagnosis, "Nenhum tratamento disponível.");
    }

    private void loadTreatments(Context context) {
        treatments = new HashMap<>();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("tratamentos.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String disease = parts[0].trim();
                    String treatment = parts[1].trim();
                    treatments.put(disease, treatment);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calculateDiseaseProbabilities() {
        int totalSymptoms = 0;
        for (List<String> symptoms : diseaseSymptoms.values()) {
            totalSymptoms += symptoms.size();
        }

        for (Map.Entry<String, List<String>> entry : diseaseSymptoms.entrySet()) {
            String disease = entry.getKey();
            int symptomCount = entry.getValue().size();
            double probability = (double) symptomCount / totalSymptoms;
            diseaseProbabilities.put(disease, probability);
        }
    }

    public Map<String, Double> getDiseaseProbabilities() {
        return diseaseProbabilities;
    }

    public double getDiseaseProbability(String disease) {
        return diseaseProbabilities.getOrDefault(disease, 0.0);
    }

    public Map<String, String> getTreatments() {
        return treatments;
    }
}
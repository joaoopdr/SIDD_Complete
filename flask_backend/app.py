from flask import Flask, request, jsonify
import pandas as pd
from sklearn.tree import DecisionTreeClassifier

app = Flask(__name__)

# Example dataset
data = {
    'Disease': ['Influenza', 'Dengue Fever', 'Zika Virus', 'Chikungunya', 'Pneumonia', 'Tuberculosis', 'COVID-19', 'Leptospirosis', 'Yellow Fever', 'Malaria'],
    'Severity': ['Moderate', 'Severe', 'Moderate', 'Severe', 'Severe', 'Severe', 'Severe', 'Severe', 'Severe', 'Severe'],
    'Incidence': ['High', 'Moderate', 'Moderate', 'Moderate', 'High', 'High', 'High', 'Moderate', 'Low', 'High'],
    'Treatments': [
        'Rest, Fluids, Antiviral medication',
        'Supportive care, Fluids, Pain relievers',
        'Rest, Fluids, Pain relievers',
        'Supportive care, Fluids, Pain relievers',
        'Antibiotics, Rest, Fluids',
        'Antibiotics, Long-term treatment',
        'Rest, Fluids, Antiviral medication',
        'Antibiotics, Supportive care',
        'Vaccination, Supportive care',
        'Antimalarial drugs, Supportive care'
    ],
    'Fever': [1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    'Cough': [1, 0, 0, 0, 1, 1, 1, 0, 0, 0],
    'Headache': [1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    'Muscle Pain': [1, 1, 1, 1, 1, 0, 1, 1, 1, 1],
    'Rash': [0, 1, 1, 1, 0, 0, 0, 0, 1, 0],
    'Breathlessness': [0, 0, 0, 0, 1, 1, 1, 1, 0, 0],
    'Fatigue': [1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    'Nausea': [0, 1, 1, 1, 0, 0, 1, 1, 1, 1]
}

# Load the dataset into a DataFrame
df = pd.DataFrame(data)

# Features and target
X = df[['Fever', 'Cough', 'Headache', 'Muscle Pain', 'Rash', 'Breathlessness', 'Fatigue', 'Nausea']]
y = df['Disease']

# Train a decision tree classifier
clf = DecisionTreeClassifier()
clf.fit(X, y)

@app.route('/symptom_checker', methods=['POST'])
def symptom_checker():
    input_data = request.json
    symptoms = input_data['symptoms']

    # Generate the input vector
    input_vector = [0] * len(X.columns)
    symptom_indices = {symptom: idx for idx, symptom in enumerate(X.columns)}
    for symptom in symptoms:
        if symptom in symptom_indices:
            input_vector[symptom_indices[symptom]] = 1

    # Predict diseases
    probs = clf.predict_proba([input_vector])[0]
    disease_prob = {disease: prob for disease, prob in zip(clf.classes_, probs)}

    # Filter results by non-zero probability
    disease_prob = {disease: prob for disease, prob in disease_prob.items() if prob > 0}

    # Sort by probability
    sorted_disease_prob = sorted(disease_prob.items(), key=lambda item: item[1], reverse=True)

    results = []
    for rank, (disease, probability) in enumerate(sorted_disease_prob, start=1):
        row = df[df['Disease'] == disease].iloc[0]
        matched_symptoms = [symptom for symptom in symptoms if symptom in row.index and row[symptom] == 1]
        unmatched_symptoms = [symptom for symptom in row.index if row[symptom] == 1 and symptom not in symptoms]
        results.append({
            'rank': rank,
            'disease': disease,
            'severity': row['Severity'],
            'incidence': row['Incidence'],
            'treatments': row['Treatments'],
            'matchConfidence': round(probability * 100, 2),
            'matchedSymptoms': matched_symptoms,
            'unmatchedSymptoms': unmatched_symptoms
        })

    return jsonify(results=results)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

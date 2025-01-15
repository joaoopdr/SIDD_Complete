package com.example.symptom_checker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class SymptomActivity extends AppCompatActivity {

    private EditText symptomEditText;
    private Button startButton;
    private Button backButton;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);

        symptomEditText = findViewById(R.id.symptomsInput);
        startButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);

        decorView = getWindow().getDecorView();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] symptoms = symptomEditText.getText().toString().split(",");
                for (String symptom : symptoms) {
                    SymptomStorage.storeSymptom(symptom.trim(), true);
                }
                Intent intent = new Intent(SymptomActivity.this, QuestionsActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomActivity.this, ServiceSelectionActivity.class);
                startActivity(intent);
                finish(); // This will close the current activity
            }
        });
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
package com.example.symptom_checker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class LegalDisclaimerActivity extends AppCompatActivity {

    private CheckBox agreementCheckBox;
    private Button continueButton;
    private ScrollView legalScrollView;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the legal disclaimer has already been accepted
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean accepted = preferences.getBoolean("accepted", false);

        if (accepted) {
            // If accepted, go directly to the LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the LegalDisclaimerActivity
            return;
        }

        setContentView(R.layout.activity_legal_disclaimer);

        decorView = getWindow().getDecorView();

        agreementCheckBox = findViewById(R.id.agreementCheckBox);
        continueButton = findViewById(R.id.continueButton);
        legalScrollView = findViewById(R.id.legalScrollView);

        legalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!legalScrollView.canScrollVertically(1)) {
                    agreementCheckBox.setEnabled(true);
                }
            }
        });

        agreementCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            continueButton.setEnabled(isChecked);
        });

        continueButton.setOnClickListener(v -> {
            // Save the acceptance state
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("accepted", true);
            editor.apply();

            Intent intent = new Intent(LegalDisclaimerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the LegalDisclaimerActivity
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |  View.SYSTEM_UI_FLAG_FULLSCREEN
                    |  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}

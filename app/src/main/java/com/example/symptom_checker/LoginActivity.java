package com.example.symptom_checker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView errorTextView;
    private TextView signUpText;
    private View decorView;
    private FirebaseAuth mAuth;

    // Shared Preferences to save login state
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String PREF_LOGIN_STATE = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and SharedPreferences
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        // Check if user is already logged in
        if (sharedPreferences.getBoolean(PREF_LOGIN_STATE, false) && mAuth.getCurrentUser() != null) {
            skipLogin();
        } else {
            setContentView(R.layout.activity_login);
            decorView = getWindow().getDecorView();

            emailInput = findViewById(R.id.email_input);
            passwordInput = findViewById(R.id.password_input);
            loginButton = findViewById(R.id.login_button);
            errorTextView = findViewById(R.id.error_text_view);
            signUpText = findViewById(R.id.signup_text);

            // Handle Enter key press for login
            passwordInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    loginButton.performClick();
                    return true;
                }
                return false;
            });

            loginButton.setOnClickListener(v -> loginUser());

            signUpText.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            errorTextView.setText("Por favor, preencha todos os campos");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save login state
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(PREF_LOGIN_STATE, true);
                            editor.apply();

                            skipLogin();
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            errorTextView.setText("Nenhuma conta encontrada com este e-mail.");
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            errorTextView.setText("Credenciais de login inválidas");
                        } else {
                            errorTextView.setText("Erro ao fazer login");
                        }
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void skipLogin() {
        Intent intent = new Intent(LoginActivity.this, ServiceSelectionActivity.class);
        startActivity(intent);
        finish();
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

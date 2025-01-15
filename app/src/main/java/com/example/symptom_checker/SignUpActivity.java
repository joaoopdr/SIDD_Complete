package com.example.symptom_checker;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;
    private TextView errorTextView;
    private View decorView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        decorView = getWindow().getDecorView();

        emailEditText = findViewById(R.id.email_input);
        nameEditText = findViewById(R.id.name_input);
        passwordEditText = findViewById(R.id.password_input);
        confirmPasswordEditText = findViewById(R.id.confirm_password_input);
        signUpButton = findViewById(R.id.signup_button);
        errorTextView = findViewById(R.id.error_text_view);

        // Handle Enter key press for sign up
        confirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN) {
                signUpButton.performClick();
                return true;
            }
            return false;
        });

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorTextView.setText("Por favor, preencha todos os campos");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                errorTextView.setText("As senhas não coincidem");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            // Create user document in Firestore
                            db.collection("users").document(userId)
                                    .set(new User(email, name))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "Conta criada com sucesso", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        errorTextView.setText("Erro ao criar a conta");
                                        errorTextView.setVisibility(View.VISIBLE);
                                    });
                        } else {
                            handleSignUpError(task.getException());
                        }
                    });
        });

        TextView loginText = findViewById(R.id.login_text);
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleSignUpError(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            errorTextView.setText("A senha é muito fraca. Escolha uma senha mais forte.");
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorTextView.setText("O endereço de e-mail está mal formatado.");
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            errorTextView.setText("Já existe uma conta com este endereço de e-mail.");
        } else {
            errorTextView.setText("Erro ao criar a conta");
        }
        errorTextView.setVisibility(View.VISIBLE);
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

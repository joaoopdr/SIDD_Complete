package com.example.symptom_checker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView nomeTextView;
    private FirebaseFirestore firestore;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializar componentes da UI
        nomeTextView = findViewById(R.id.profile_name);
        decorView = getWindow().getDecorView();

        // Inicializar Firebase Auth e Firestore
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Definir o nome do usuário no nomeTextView
        if (user != null) {
            firestore.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String nome = document.getString("name");
                                if (nome != null) {
                                    nomeTextView.setText(nome);
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Nome não encontrado", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Falha ao carregar dados do usuário", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Fechar a atividade se nenhum usuário estiver autenticado
        }

        configurarItensConfiguracao();
    }

    private void configurarItensConfiguracao() {
        View perfilItem = findViewById(R.id.profile_item);
        View notificacoesItem = findViewById(R.id.notifications_item);
        View privacidadeItem = findViewById(R.id.privacy_item);
        View geralItem = findViewById(R.id.general_item);
        View sobreNosItem = findViewById(R.id.about_us_item);
        View sairItem = findViewById(R.id.log_out_item);

        perfilItem.setOnClickListener(v -> abrirConfiguracoesPeril());
        notificacoesItem.setOnClickListener(v -> abrirConfiguracoesNotificacoes());
        privacidadeItem.setOnClickListener(v -> abrirConfiguracoesPrivacidade());
        geralItem.setOnClickListener(v -> abrirConfiguracoesGerais());
        sobreNosItem.setOnClickListener(v -> abrirSobreNos());
        sairItem.setOnClickListener(v -> sair());
    }

    private void abrirConfiguracoesPeril() {
        // TODO: Implementar configurações de perfil
        Toast.makeText(this, "Abrindo Configurações de Perfil", Toast.LENGTH_SHORT).show();
    }

    private void abrirConfiguracoesNotificacoes() {
        // TODO: Implementar configurações de notificações
        Toast.makeText(this, "Abrindo Configurações de Notificações", Toast.LENGTH_SHORT).show();
    }

    private void abrirConfiguracoesPrivacidade() {
        // TODO: Implementar configurações de privacidade
        Toast.makeText(this, "Abrindo Configurações de Privacidade", Toast.LENGTH_SHORT).show();
    }

    private void abrirConfiguracoesGerais() {
        // TODO: Implementar configurações gerais
        Toast.makeText(this, "Abrindo Configurações Gerais", Toast.LENGTH_SHORT).show();
    }

    private void abrirSobreNos() {
        // TODO: Implementar sobre nós
        Toast.makeText(this, "Abrindo Sobre Nós", Toast.LENGTH_SHORT).show();
    }

    private void sair() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(ProfileActivity.this, "Sessão encerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
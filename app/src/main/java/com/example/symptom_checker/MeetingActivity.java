package com.example.symptom_checker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class MeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar as opções da conferência
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si")) // URL do servidor Jitsi público
                    .setRoom("Consulta" + System.currentTimeMillis()) // Nome da sala
                    .setAudioMuted(false)  // Áudio não mutado por padrão
                    .setVideoMuted(false)  // Vídeo não mutado por padrão
                    .build();

            // Iniciar a atividade do Jitsi Meet
            JitsiMeetActivity.launch(this, options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Finalizar a atividade MeetingActivity após iniciar a reunião
        finish();
    }
}
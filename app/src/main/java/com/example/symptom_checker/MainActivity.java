package com.example.symptom_checker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private SymptomCheckerApi apiService;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText userInput;
    private FrameLayout sendButton;
    private boolean sessionStarted = false;
    private int currentStep = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juan);

        apiService = ApiClient.getClient().create(SymptomCheckerApi.class);
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        userInput = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.layoutSend);

        sendButton.setEnabled(false); // Disable initially
        startConversation();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = userInput.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    messageList.add(new Message(messageText, true));
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    userInput.setText("");
                    continueConversation(messageText);
                }
            }
        });
    }

    private void startConversation() {
        apiService.startConversation().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionStarted = true;
                    sendButton.setEnabled(true);

                    ApiResponse apiResponse = response.body();
                    if (apiResponse.message != null) {
                        messageList.add(new Message(apiResponse.message, false));
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                } else {
                    Log.e("API_CALL", "Failed to start session. Code: " + response.code());
                    logErrorBody(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_CALL", "Error starting session: " + t.getMessage());
            }
        });
    }

    private void logErrorBody(Response<ApiResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
            Log.e("API_CALL", "Error body: " + errorBody);
        } catch (IOException e) {
            Log.e("API_CALL", "Error reading error body", e);
        }
    }

    // Assumindo que você já configurou Retrofit e RequestBody

    private void continueConversation(String userInputText) {
        if (!sessionStarted) {
            Log.e("API_CALL", "Session not started, cannot continue conversation.");
            return;
        }

        RequestBody requestBody = new RequestBody();
        switch (currentStep) {
            case 1:
                requestBody.mainSymptom = userInputText;
                break;
            case 15:
                requestBody.selectedSymptom = userInputText;
                break;
            case 2:
                try {
                    requestBody.numDays = Integer.parseInt(userInputText);
                } catch (NumberFormatException e) {
                    messageList.add(new Message("Por favor, insira um número válido para os dias.", false));
                    chatAdapter.notifyDataSetChanged();
                    return;
                }
                break;
            case 3:
                try {
                    requestBody.intensity = Integer.parseInt(userInputText);
                } catch (NumberFormatException e) {
                    messageList.add(new Message("Por favor, insira um número válido para a gravidade.", false));
                    chatAdapter.notifyDataSetChanged();
                    return;
                }
                break;
            case 4:
                requestBody.additionalSymptom = userInputText;
                break;
            case 5:
                requestBody.answer = userInputText;
                break;
            default:
                messageList.add(new Message("Etapa desconhecida.", false));
                chatAdapter.notifyDataSetChanged();
                return;
        }

        apiService.continueConversation(requestBody).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.message != null) {
                        messageList.add(new Message(apiResponse.message, false));
                    }
                    if (apiResponse.question != null) {
                        messageList.add(new Message(apiResponse.question, false));
                    }
                    if (apiResponse.diagnosis != null) {
                        String diagnosisMessage = "Diagnóstico: " + apiResponse.diagnosis + "\n" +
                                apiResponse.symptomDuration + "\n" +
                                apiResponse.symptomIntensity + "\n" +
                                apiResponse.symptomsAbsent + "\n" +
                                apiResponse.symptomsPresent;
                        messageList.add(new Message(diagnosisMessage, false));
                    }
                    
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);

                    if (currentStep == 4 && userInputText.equalsIgnoreCase("não")) {
                        currentStep = 5; // Muda para step 5 se a resposta for "não"
                    } else if (currentStep == 4) {
                        currentStep = 4; // Mantém no step 4 se a resposta não for "não"
                    } else if (apiResponse.diagnosis != null) {
                        currentStep = 6; // Diagnóstico
                    } else if (apiResponse.question != null) {
                        currentStep = 5;
                    } else {
                        currentStep++;
                    }
                } else {
                    logErrorBody(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_CALL", "Error: " + t.getMessage());
            }
        });
    }
}
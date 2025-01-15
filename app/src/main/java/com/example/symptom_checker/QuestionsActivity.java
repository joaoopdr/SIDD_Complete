package com.example.symptom_checker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    private DecisionTree decisionTree;
    private DecisionTreeNode currentNode;
    private Map<String, Boolean> answers;
    private View decorView;

    private TextView questionTextView;
    private Button yesButton;
    private Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        decisionTree = new DecisionTree(this);
        answers = new HashMap<>(SymptomStorage.getStoredSymptoms());

        questionTextView = findViewById(R.id.questionTextView);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        decorView = getWindow().getDecorView();

        currentNode = decisionTree.getRoot();
        updateQuestion();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAnswer(true);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAnswer(false);
            }
        });
    }

    private void processAnswer(boolean answer) {
        if (currentNode != null) {
            answers.put(currentNode.getQuestion(), answer);
            currentNode = answer ? currentNode.getYesNode() : currentNode.getNoNode();
            updateQuestion();
        } else {
            navigateToResults();
        }
    }

    private void updateQuestion() {
        while (currentNode != null && !currentNode.isLeaf() && answers.containsKey(currentNode.getQuestion())) {
            boolean storedAnswer = answers.get(currentNode.getQuestion());
            currentNode = storedAnswer ? currentNode.getYesNode() : currentNode.getNoNode();
        }

        if (currentNode == null || currentNode.isLeaf()) {
            navigateToResults();
        } else {
            String question = "Você tem " + currentNode.getQuestion() + "?";
            questionTextView.setText(question);
        }
    }

    private void navigateToResults() {
        Intent intent = new Intent(QuestionsActivity.this, ResultsActivity.class);
        intent.putExtra("answers", new HashMap<>(answers));
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
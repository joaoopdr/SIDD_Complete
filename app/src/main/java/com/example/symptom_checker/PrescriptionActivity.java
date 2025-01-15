package com.example.symptom_checker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity {

    private static final String TAG = "PrescriptionActivity";
    private RecyclerView listViewPrescriptions;
    private View decorView;
    private PrescriptionAdapter adapter;
    private List<Prescription> prescriptionList;
    private Button back_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        listViewPrescriptions = findViewById(R.id.listViewPrescriptions);
        listViewPrescriptions.setLayoutManager(new LinearLayoutManager(this));

        prescriptionList = new ArrayList<>();
        adapter = new PrescriptionAdapter(prescriptionList, this);
        listViewPrescriptions.setAdapter(adapter);
        back_Button = findViewById(R.id.back_button);

        decorView = getWindow().getDecorView();

        fetchPrescriptions();

        back_Button.setOnClickListener(v -> finish());
    }

    private void fetchPrescriptions() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("prescriptions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        prescriptionList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Prescription prescription = document.toObject(Prescription.class);
                            prescriptionList.add(prescription);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching prescriptions", task.getException());
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
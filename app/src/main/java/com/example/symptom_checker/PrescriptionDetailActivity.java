package com.example.symptom_checker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PrescriptionDetailActivity extends AppCompatActivity {

    private static final String TAG = "PrescriptionDetailActivity";
    private Prescription prescription;
    private TextView titleTextView, dosageTextView, frequencyTextView, descriptionTextView,
            doctorTextView, crmTextView, dateTextView, validityTextView, statusTextView;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_details);

        initializeViews();

        String prescriptionId = getIntent().getStringExtra("prescriptionId");
        if (prescriptionId != null) {
            fetchPrescription(prescriptionId);
        } else {
            String qrCodeId = getIntent().getStringExtra("qrCodeId");
            if (qrCodeId != null) {
                fetchPrescriptionByQRCode(qrCodeId);
            } else {
                Toast.makeText(this, "Erro: Nenhuma informação de prescrição fornecida", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.textViewPrescriptionTitle);
        dosageTextView = findViewById(R.id.textViewPrescriptionDosage);
        frequencyTextView = findViewById(R.id.textViewPrescriptionFrequency);
        descriptionTextView = findViewById(R.id.textViewPrescriptionDescription);
        doctorTextView = findViewById(R.id.textViewPrescriptionDoctor);
        crmTextView = findViewById(R.id.textViewPrescriptionDoctorCRM);
        dateTextView = findViewById(R.id.textViewPrescriptionDate);
        validityTextView = findViewById(R.id.textViewPrescriptionValidity);
        statusTextView = findViewById(R.id.textViewPrescriptionStatus);
        qrCodeImageView = findViewById(R.id.imageViewQRCode);
    }

    private void fetchPrescription(String prescriptionId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("prescriptions").document(prescriptionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    prescription = documentSnapshot.toObject(Prescription.class);
                    if (prescription != null) {
                        updateUI(prescription);
                    } else {
                        Toast.makeText(this, "Erro ao carregar prescrição", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching prescription", e);
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchPrescriptionByQRCode(String qrCodeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("prescriptions")
                .whereEqualTo("qrCodeId", qrCodeId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        prescription = queryDocumentSnapshots.getDocuments().get(0).toObject(Prescription.class);
                        updateUI(prescription);
                    } else {
                        Toast.makeText(this, "Prescrição não encontrada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching prescription by QR code", e);
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI(Prescription prescription) {
        titleTextView.setText("Medicamento: " + prescription.getMedicineName());
        dosageTextView.setText("Dosagem: " + prescription.getDosage());
        frequencyTextView.setText("Frequência: " + prescription.getFrequency());
        descriptionTextView.setText("Descrição: " + prescription.getDescription());
        doctorTextView.setText("Médico: Dr. " + prescription.getDoctorName());
        crmTextView.setText("CRM: " + prescription.getDoctorCRM());
        dateTextView.setText("Data: " + prescription.getDate());
        validityTextView.setText("Validade: " + prescription.getValidity());
        statusTextView.setText("Status: " + prescription.getStatus());

        generateAndSetQRCode(prescription.getQrCodeId());
    }

    private void generateAndSetQRCode(String qrCodeId) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrCodeId, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_prescription_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            sharePrescription();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sharePrescription() {
        if (prescription == null) {
            Toast.makeText(this, "Nenhuma prescrição para compartilhar", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Prescrição Médica");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Detalhes da prescrição:\n" +
                "Medicamento: " + prescription.getMedicineName() + "\n" +
                "Dosagem: " + prescription.getDosage() + "\n" +
                "Frequência: " + prescription.getFrequency() + "\n" +
                "Médico: Dr. " + prescription.getDoctorName() + "\n" +
                "CRM: " + prescription.getDoctorCRM() + "\n" +
                "Data: " + prescription.getDate() + "\n" +
                "Validade: " + prescription.getValidity() + "\n" +
                "Status: " + prescription.getStatus() + "\n" +
                "QR Code ID: " + prescription.getQrCodeId());
        startActivity(Intent.createChooser(shareIntent, "Compartilhar Prescrição"));
    }
}
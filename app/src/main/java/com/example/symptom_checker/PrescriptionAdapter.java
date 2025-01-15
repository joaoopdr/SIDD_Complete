package com.example.symptom_checker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    private List<Prescription> prescriptions;
    private Context context;

    public PrescriptionAdapter(List<Prescription> prescriptions, Context context) {
        this.prescriptions = prescriptions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prescription_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prescription prescription = prescriptions.get(position);

        holder.medicineNameTextView.setText("Medicamento: " + prescription.getMedicineName());
        holder.statusTextView.setText("Status: " + prescription.getStatus());
        holder.dateAddedTextView.setText("Data de Inclusão: " + prescription.getDateAdded());

        // Você pode adicionar lógica aqui para definir o avatar do médico, se necessário
        // holder.doctorAvatar.setImageResource(R.drawable.default_doctor_avatar);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PrescriptionDetailActivity.class);
            intent.putExtra("prescriptionId", prescription.getQrCodeId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorAvatar;
        TextView medicineNameTextView;
        TextView statusTextView;
        TextView dateAddedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorAvatar = itemView.findViewById(R.id.doctorAvatar);
            medicineNameTextView = itemView.findViewById(R.id.medicineNameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            dateAddedTextView = itemView.findViewById(R.id.dateAddedTextView);
        }
    }
}
package com.example.symptom_checker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Random;

public class QueueActivity extends AppCompatActivity {

    private static final long UPDATE_INTERVAL = 300000; // 5 minutos
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final double DEFAULT_ZOOM = 13.0; // Aproximadamente 10 km de raio

    private ArrayList<Hospital> hospitals;
    private MapView map;
    private View decorView;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_map);

        map = findViewById(R.id.map);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        decorView = getWindow().getDecorView();

        hospitals = new ArrayList<>();
        Hospital.initializeHospitals(hospitals);

        // Recebe os dados de localização do Intent
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        if (latitude != 0 && longitude != 0) {
            userLocation = new Location("");
            userLocation.setLatitude(latitude);
            userLocation.setLongitude(longitude);
            updateMapWithUserLocation();
        } else {
            // Solicita a localização se não foi passada
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    userLocation = location;
                    updateMapWithUserLocation();
                    stopLocationUpdates(); // Para após obter a localização
                    break;
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void updateMapWithUserLocation() {
        if (userLocation == null) return;

        IMapController mapController = map.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        GeoPoint userPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
        mapController.setCenter(userPoint);

        // Adiciona o marcador do usuário
        Marker userMarker = new Marker(map);
        userMarker.setPosition(userPoint);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setIcon(getResources().getDrawable(R.drawable.ic_user));
        userMarker.setTitle("Sua Localização");
        map.getOverlays().add(userMarker);

        // Adiciona os marcadores dos hospitais
        for (Hospital hospital : hospitals) {
            addHospitalMarker(hospital);
        }

        map.invalidate();
    }

    private void addHospitalMarker(final Hospital hospital) {
        GeoPoint location = new GeoPoint(hospital.getLatitude(), hospital.getLongitude());
        Marker marker = new Marker(map);
        marker.setPosition(location);
        marker.setTitle(hospital.getName());
        marker.setSnippet("Tempo de espera estimado: " + hospital.getQueueTime() + " minutos");

        marker.setInfoWindow(new HospitalInfoWindow(map, hospital));

        // Permite que o InfoWindow seja fechado ao clicar fora
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            if (marker.isInfoWindowShown()) {
                marker.closeInfoWindow(); // Fecha o InfoWindow se já estiver aberto
            } else {
                marker.showInfoWindow(); // Mostra o InfoWindow se estiver fechado
            }
            return true;
        });

        map.getOverlays().add(marker);
    }

    private class HospitalInfoWindow extends InfoWindow {

        private Hospital hospital;

        HospitalInfoWindow(MapView mapView, Hospital hospital) {
            super(R.layout.hospital_info_window, mapView);
            this.hospital = hospital;
        }

        @Override
        public void onOpen(Object item) {
            View view = getView();

            // Exibe o nome do hospital
            ((TextView) view.findViewById(R.id.hospital_name)).setText(hospital.getName());

            // Exibe o tempo estimado de fila
            ((TextView) view.findViewById(R.id.queue_time)).setText("Tempo de fila: " + hospital.getQueueTime() + " minutos");

            // Botão para abrir no Waze
            Button wazeButton = view.findViewById(R.id.wazeButton);
            wazeButton.setOnClickListener(v -> openWaze(hospital.getLatitude(), hospital.getLongitude()));
        }

        @Override
        public void onClose() {
            // O comportamento padrão já fecha o InfoWindow ao clicar fora
        }
    }

    private void openWaze(double latitude, double longitude) {
        String url = "https://waze.com/ul?ll=" + latitude + "," + longitude + "&navigate=yes";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permissão Negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
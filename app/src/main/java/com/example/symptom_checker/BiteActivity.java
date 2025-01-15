package com.example.symptom_checker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.tensorflow.lite.Interpreter;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiteActivity extends AppCompatActivity {

    // Ensure these dimensions match the input size expected by your model
    private static final int MODEL_WIDTH = 256;
    private static final int MODEL_HEIGHT = 256;
    private static final int FLOAT_TYPE_SIZE = 4;  // Each float takes up 4 bytes
    private static final int PIXEL_SIZE = 3;  // RGB channels
    private static final int MODEL_INPUT_SIZE = FLOAT_TYPE_SIZE * MODEL_WIDTH * MODEL_HEIGHT * PIXEL_SIZE;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private ImageView imageView;
    private View decorView;
    private TextView resultTextView;
    private Button uploadButton;
    private Button backButton;
    private Button startMeetButton;
    private ActivityResultLauncher<String> imagePickerLauncher;

    private Interpreter tflite;
    private List<String> labels;
    private String recognizedResult = "";  // Variable to store classification result

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseStorage storage;  // Reference to Firebase Storage
    private Uri imageUri;  // Stores the URI of the selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bite);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        uploadButton = findViewById(R.id.uploadButton);
        backButton = findViewById(R.id.backButton);
        startMeetButton = findViewById(R.id.startMeetButton);

        decorView = getWindow().getDecorView();

        firestore = FirebaseFirestore.getInstance();  // Initialize Firestore
        auth = FirebaseAuth.getInstance();  // Initialize Firebase Authentication
        storage = FirebaseStorage.getInstance();  // Initialize Firebase Storage

        initializeModel();
        loadLabels();
        initializeLaunchers();
        checkAndRequestPermissions();

        // Action for the gallery upload button
        uploadButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Action for the back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(BiteActivity.this, ServiceSelectionActivity.class);
            startActivity(intent);
            finish();
        });

        // Action for the start meeting button
        startMeetButton.setOnClickListener(v -> {
            if (imageUri != null && !recognizedResult.isEmpty()) {
                uploadImageToStorage();  // Upload the image to Firebase Storage
            } else {
                Toast.makeText(BiteActivity.this, "No image or classification result available!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check and request permissions
    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Initialize the TensorFlow Lite model
    private void initializeModel() {
        try {
            MappedByteBuffer tfliteModel = loadModelFile();
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(tfliteModel, options);
        } catch (IOException e) {
            Log.e("BiteActivity", "Error initializing TensorFlow Lite model", e);
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        String modelPath = "super_model_epoch_01.tflite";  // Use 'bite.tflite' as per your model file
        AssetFileDescriptor fileDescriptor = getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Load model labels
    private void loadLabels() {
        labels = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line);
            }
            reader.close();
        } catch (IOException e) {
            Log.e("BiteActivity", "Error reading labels", e);
        }
    }

    // Initialize image pickers
    private void initializeLaunchers() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;  // Store the selected image URI
                        try {
                            Bitmap bitmap = getBitmapFromUri(uri);
                            imageView.setImageBitmap(bitmap);
                            classifyImage(bitmap);  // Classify the image after it's selected
                        } catch (IOException e) {
                            Log.e("BiteActivity", "Error loading image", e);
                        }
                    }
                }
        );
    }

    // Get the bitmap from a URI
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
    }

    // Image classification method
    private void classifyImage(Bitmap image) {
        try {
            if (tflite == null) {
                Log.e("BiteActivity", "TensorFlow Lite interpreter is null");
                return;
            }

            // Resize the image to match the model input size (299x299)
            Bitmap resizedImage = Bitmap.createScaledBitmap(image, MODEL_WIDTH, MODEL_HEIGHT, true);

            // Allocate the input buffer
            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(MODEL_INPUT_SIZE);
            inputBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[MODEL_WIDTH * MODEL_HEIGHT];
            resizedImage.getPixels(intValues, 0, resizedImage.getWidth(), 0, 0, resizedImage.getWidth(), resizedImage.getHeight());

            inputBuffer.rewind();
            for (int pixelValue : intValues) {
                inputBuffer.putFloat((((pixelValue >> 16) & 0xFF) - 128) / 128.0f);  // Red
                inputBuffer.putFloat((((pixelValue >> 8) & 0xFF) - 128) / 128.0f);   // Green
                inputBuffer.putFloat(((pixelValue & 0xFF) - 128) / 128.0f);          // Blue
            }

            // Prepare output buffer (assuming a single output with 22 classes; adjust if necessary)
            float[][] outputArray = new float[1][22];

            tflite.run(inputBuffer, outputArray);

            float[] confidences = outputArray[0];

            // Find the class with the highest confidence
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            if (maxPos < labels.size()) {
                recognizedResult = labels.get(maxPos);
                final int confidencePercentage = (int) (maxConfidence * 100);

                runOnUiThread(() -> {
                    resultTextView.setText(String.format("Recognized: %s\nConfidence: %d%%", recognizedResult, confidencePercentage));
                });
            } else {
                Log.e("BiteActivity", "Index out of bounds for labels");
                runOnUiThread(() -> {
                    resultTextView.setText("Error: Unrecognized class");
                });
            }

        } catch (Exception e) {
            Log.e("BiteActivity", "Error classifying image", e);
            runOnUiThread(() -> {
                resultTextView.setText("Error classifying image: " + e.getMessage());
            });
        }
    }

    // Upload the image to Firebase Storage
    private void uploadImageToStorage() {
        if (imageUri == null) {
            Toast.makeText(BiteActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique reference for the image in Firebase Storage
        String userId = auth.getCurrentUser().getUid();
        String fileName = System.currentTimeMillis() + ".jpg";  // Name based on timestamp
        StorageReference imageRef = storage.getReference().child("images/" + userId + "/" + fileName);

        // Upload the image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // After the upload, get the image's download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Toast.makeText(BiteActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                        // Now that we have the image URL, we can save the diagnosis and the URL in Firestore
                        saveDiagnosisAndStartMeeting(imageUrl);
                    }).addOnFailureListener(e -> {
                        Log.e("BiteActivity", "Error getting image URL", e);
                        Toast.makeText(BiteActivity.this, "Error getting image URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("BiteActivity", "Error uploading image", e);
                    Toast.makeText(BiteActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                });
    }

    // Save the diagnosis, image URL, and start the meeting
    private void saveDiagnosisAndStartMeeting(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        String roomName = "Consultation" + System.currentTimeMillis();
        String jitsiLink = "https://meet.jit.si/" + roomName;

        Map<String, Object> diagnosisData = new HashMap<>();
        diagnosisData.put("diagnosis", recognizedResult);  // Add classification result
        diagnosisData.put("meetingLink", jitsiLink);
        diagnosisData.put("imageUrl", imageUrl);  // Add image URL
        diagnosisData.put("timestamp", Timestamp.now());

        String diagnosisPath = "/users/" + userId + "/diagnosis/" + System.currentTimeMillis();

        firestore.document(diagnosisPath)
                .set(diagnosisData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("BiteActivity", "Diagnosis and meeting link saved successfully!");
                    startJitsiMeeting(roomName);
                })
                .addOnFailureListener(e -> {
                    Log.e("BiteActivity", "Error saving to Firestore", e);
                    Toast.makeText(BiteActivity.this, "Error saving diagnosis!", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to start the Jitsi meeting
    private void startJitsiMeeting(String roomName) {
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(roomName)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .build();

            JitsiMeetActivity.launch(BiteActivity.this, options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(BiteActivity.this, "Error starting meeting!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
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
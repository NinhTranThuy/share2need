package com.example.share2need.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.share2need.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText editFullName, editEmail, editPhone, editAddress;
    private LinearLayout buttonSave, imagePostArea = null;
    TextView tvAddImage = null, tvButtonPost = null;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private LatLng selectedLocation;
    private Marker marker;
    String imageUrl;

    private final String userId = "u1"; // Giả lập user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        buttonSave = findViewById(R.id.buttonSave);
        imagePostArea = findViewById(R.id.imagePostArea);
        tvButtonPost = findViewById(R.id.tvButtonPost);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Load dữ liệu người dùng hiện tại
        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                editFullName.setText(doc.getString("fullname"));
                editEmail.setText(doc.getString("email"));
                editPhone.setText(doc.getString("phone"));
                editAddress.setText(doc.getString("address"));

                if (doc.contains("geopointUser")) {
                    GeoPoint geoPoint = doc.getGeoPoint("geopointUser");
                    if (geoPoint != null) {
                        selectedLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        if (mMap != null) {
                            updateMapMarker();
                        }
                    }
                }
            }
        });

        // Sự kiện lưu
        buttonSave.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String address = editAddress.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ họ tên và email", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("fullname", fullName);
            updates.put("email", email);
            updates.put("phone", phone);
            updates.put("address", address);
            updates.put("profileImage", imageUrl);

            if (selectedLocation != null) {
                updates.put("geopointUser", new GeoPoint(selectedLocation.latitude, selectedLocation.longitude));
            }

            db.collection("users").document(userId).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (selectedLocation != null) {
            updateMapMarker();
        } else {
            requestLocationIfPermitted();
        }

        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            updateMapMarker();
        });
    }

    private void requestLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            updateMapMarker();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationIfPermitted();
        } else {
            Toast.makeText(this, "Ứng dụng cần quyền truy cập vị trí để hiển thị bản đồ.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateMapMarker() {
        if (mMap == null || selectedLocation == null) return;

        if (marker != null) marker.remove();

        marker = mMap.addMarker(new MarkerOptions().position(selectedLocation).title("Vị trí đã chọn"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f));

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    selectedLocation.latitude,
                    selectedLocation.longitude,
                    1
            );
            if (!addresses.isEmpty()) {
                String addr = addresses.get(0).getAddressLine(0);
                editAddress.setText(addr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addImage_onClick(View view) {
        tvAddImage = view.findViewById(R.id.tvAddImage);
        tvAddImage.setVisibility(View.GONE);
        openImagePicker();
    }
    private void openImagePicker() {
        Intent pickerImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickerImageIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(pickerImageIntent, "Chọn ảnh"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                addImageToContainer(imageUri);
                convertUriToBase64(imageUri);
            }
        }
    }

    //Lay ra tung anh trong listImage<Uri> de cho vao Layout
    private void addImageToContainer(Uri imageUri){
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) (90 * getResources().getDisplayMetrics().density),
                (int) (90 * getResources().getDisplayMetrics().density)
        );
        params.setMargins(5, 5, 5, 5);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(imageUri);
        imagePostArea.addView(imageView);
    }
    private void convertUriToBase64(Uri imageUris) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUris);
                if (inputStream == null) {
                    Log.e("PostActivityy", "inputStream null cho Uri: " + imageUris.toString());
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close(); // ⚠️ Đóng stream sau khi dùng

                if (bitmap != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    byte[] imageBytes = outputStream.toByteArray();
                    outputStream.close();

                    imageUrl = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // dùng NO_WRAP nếu muốn chuỗi gọn
                }

            } catch (Exception e) {
                Log.e("EditProfileActivity", "Lỗi khi chuyển ảnh sang Base64: " + e.getMessage());
            }
    }


}

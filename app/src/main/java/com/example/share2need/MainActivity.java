package com.example.share2need;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.adapters.ProductAdapter;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.models.Product;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewProducts = null;
    List<Product> productList = new ArrayList<>();
    ProductRepository productRepository = null;
    TextView tvAdress = null;
    //Location
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        Log.d("Network", "Online: " + isConnected);
        FirebaseFirestore.setLoggingEnabled(true); // Thêm vào onCreate()
        initDataView();

        initData();
    }

    private void initData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initDataView() {


        productRepository = new ProductRepository();
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);


        // 1. Khởi tạo observer
        productRepository.getProductsLiveData().observe(this, products -> {
            if (products == null) {
                Log.e("MainActivity", "Received null products");
                return;
            }
            Log.d("MainActivityy", "Received " + products.size() + " products");
            ProductAdapter productAdapter = new ProductAdapter(this, products);
            recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerViewProducts.setAdapter(productAdapter);
        });

        // 2. Bắt đầu lắng nghe
        productRepository.startListening();
//            productList = (List<Product>) productRepository.getAllProductsLive();



    }

    public void findLocation_onClick(View view) {
        // Kiểm tra quyền trước
        if (checkLocationPermission()) {
            // Đã có quyền, tiến hành lấy vị trí
            getCurrentLocation();
        } else {
            // Chưa có quyền, yêu cầu quyền
            requestLocationPermission();
        }
    }
    // Kiểm tra quyền truy cập vị trí
    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    // Yêu cầu quyền truy cập vị trí
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    // Lấy vị trí hiện tại
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        getAddressFromLocation(latitude, longitude);
                    } else {
                        // Nếu không có vị trí cuối cùng, yêu cầu cập nhật vị trí mới
                        requestNewLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lấy vị trí: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Yêu cầu cập nhật vị trí mới nếu không có vị trí cuối cùng
    private void requestNewLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        fusedLocationClient.removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            Location location = locationResult.getLastLocation();
                            getAddressFromLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(MainActivity.this, "Khong the tim thay vi tri", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                Looper.getMainLooper()
        );
    }

    // Lấy địa chỉ từ tọa độ
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                String fullAddress = address.getAddressLine(0);
                TextView tvAddress = findViewById(R.id.tvAdress);
                tvAddress.setText(fullAddress);

                // Có thể hiển thị thêm thông tin khác nếu cần
                String locationInfo = String.format(
                        "Vị trí: %.6f, %.6f\nĐịa chỉ: %s",
                        latitude,
                        longitude,
                        fullAddress
                );
                Toast.makeText(this, locationInfo, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi lấy địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Đã cấp quyền, lấy vị trí
                getCurrentLocation();
            } else {
                Toast.makeText(this,
                        "Cần cấp quyền vị trí để sử dụng tính năng này",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
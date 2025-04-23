package com.example.share2need.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.share2need.R;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.firebase.UserRepository;
import com.example.share2need.models.Product;
import com.example.share2need.models.User;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {
    ProductRepository productRepository = new ProductRepository();
    LinearLayout imagePostArea = null;
    Spinner spinner = null;
    List<Uri> listImage = new ArrayList<>();
    TextView tvAddImage = null, tvButtonPost = null;
    EditText edtAddress = null;
    EditText edtProductName = null, edtQuantity = null, edtDescription = null;
    GeoPoint geoPoint = null;
    List<String> uploadedUrls = new ArrayList<>();
    LinearLayout btnPost = null;
    ImageView btnNoti = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        btnPost.setOnClickListener(view -> {
            if (uploadedUrls.isEmpty()) {
                Toast.makeText(this, "Vui lòng chờ ảnh được tải lên!", Toast.LENGTH_SHORT).show();
                return;
            }
            addProduct_onClick(view);
        });
    }
    private void initView() {
        btnNoti = findViewById(R.id.btnNoti);
        tvButtonPost = findViewById(R.id.tvButtonPost);
        btnPost = findViewById(R.id.btnPost);
        edtProductName = findViewById(R.id.edtProductName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtDescription = findViewById(R.id.edtDescription);
        spinner = findViewById(R.id.spinnerCategoriez);
        // Spinner
        spinner.setEnabled(true);
        spinner.setClickable(true);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //ImageArea
        imagePostArea = findViewById(R.id.imagePostArea);

        edtAddress = findViewById(R.id.edtAddress);

        //**************************************//
        //Bao gio co dang nhap Firebase Auth thi bo
        UserRepository usersRepository = new UserRepository();
        Intent intent = getIntent();
        usersRepository.getUserInfo(intent.getStringExtra("userID"), new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                edtAddress.setText(user.getAddress());
                geoPoint = user.getGeopointUser();
                edtAddress.setEnabled(false);
            }

            @Override
            public void onUserNotFound() {
                edtAddress.setEnabled(true);
            }

            @Override
            public void onError(Exception e) {
                edtAddress.setText("Fail!");
            }
        });
    }
    //Chon Anh san pham
    public void addImage_onClick(View view) {
        tvAddImage = view.findViewById(R.id.tvAddImage);
        tvAddImage.setVisibility(View.GONE);
        if(listImage.size() < 3){
            openImagePicker();
        } else {
            Toast.makeText(this, "Đã quá 3 ảnh", Toast.LENGTH_SHORT).show();
        }
    }
    private void openImagePicker() {

        Intent pickerImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickerImageIntent.setType("image/*");
        pickerImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(pickerImageIntent,"Chọn ảnh"), 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Nhiều ảnh được chọn
                int count = Math.min(data.getClipData().getItemCount(), 3 - listImage.size());
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    addImageToContainer(imageUri);
                }
            } else if (data.getData() != null && listImage.size() < 3) {
                // Chỉ 1 ảnh được chọn
                addImageToContainer(data.getData());
            }
        }
//        uploadImagesToFirebase(listImage);
        convertUriToBase64(listImage);

    }
    //Lay ra tung anh trong listImage<Uri> de cho vao Layout
    private void addImageToContainer(Uri imageUri){
        listImage.add(imageUri);

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
    private void convertUriToBase64(List<Uri> imageUris) {
        uploadedUrls.clear(); // Danh sách string ảnh đã mã hóa
        int totalImages = imageUris.size();
        int successCount = 0;

        for (Uri uri : imageUris) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream == null) {
                    Log.e("PostActivity", "inputStream null cho Uri: " + uri.toString());
                    continue;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close(); // ⚠️ Đóng stream sau khi dùng

                if (bitmap != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    byte[] imageBytes = outputStream.toByteArray();
                    outputStream.close();

                    String base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // dùng NO_WRAP nếu muốn chuỗi gọn
                    uploadedUrls.add(base64);
                    successCount++;
                }

            } catch (Exception e) {
                Log.e("PostActivity", "Lỗi khi chuyển ảnh sang Base64: " + e.getMessage());
            }
        }

        // ✅ Gọi enable sau khi xử lý xong tất cả ảnh
        if (successCount == totalImages) {
            enablePostButton();
        } else {
            Toast.makeText(this, "Một số ảnh không thể xử lý.", Toast.LENGTH_SHORT).show();
        }
    }

    //PHONG TOA: De tam o day
    private void uploadImagesToFirebase(List<Uri> imageUris) {
        uploadedUrls.clear(); // Clear nếu có ảnh cũ

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        final int[] uploadedCount = {0};
        int totalImages = imageUris.size();

        for (Uri uri : imageUris) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference fileRef = storageRef.child(fileName);

            fileRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                uploadedUrls.add(downloadUri.toString());
                                uploadedCount[0]++;

                                if (uploadedCount[0] == totalImages) {
                                    // ✅ Tất cả ảnh đã được upload thành công
                                    enablePostButton();
                                }
                            }))
                    .addOnFailureListener(e -> {
                        uploadedCount[0]++;
                        Log.e("FirebaseUpload", "Lỗi upload ảnh: " + e.getMessage());

                        if (!uploadedUrls.isEmpty()) {
                            enablePostButton(); // Cho phép đăng bài dù 1 số ảnh bị lỗi
                        }
                    });
        }
    }
    //
    private void enablePostButton() {
        btnPost.setEnabled(true);
        btnPost.setClickable(true);
        tvButtonPost.setTextColor(getResources().getColor(R.color.white));
        btnPost.setBackground(getResources().getDrawable(R.drawable.button_layout));
        btnNoti.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        btnNoti.setBackgroundColor(getResources().getColor(R.color.green));
    }


    public void addProduct_onClick(View view) {
        String productName = edtProductName.getText().toString();
        int quantity = Integer.parseInt(edtQuantity.getText().toString());
        String description = edtDescription.getText().toString();
        String address = edtAddress.getText().toString();
        String category = spinner.getSelectedItem().toString();

        productRepository.insertProrduct(productName, quantity, description, category,uploadedUrls,address, geoPoint);
        //finish();
    }
    //Nut quay lai
    public void backActivity_onClick(View view) {
        finish();
    }
}
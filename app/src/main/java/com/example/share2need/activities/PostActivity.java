package com.example.share2need.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    ProductRepository productRepository = new ProductRepository();
    LinearLayout imagePostArea = null;
    Spinner spinner = null;
    List<Uri> listImage = new ArrayList<>();
    TextView tvAddImage = null;
    EditText edtAddress = null;
    EditText edtProductName = null, edtQuantity = null, edtDescription = null;


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
    }

    private void initView() {
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
    }

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

    public void addProduct_onClick(View view) {
        String productName = edtProductName.getText().toString();
        String quantity = edtQuantity.getText().toString();
        String description = edtDescription.getText().toString();
        String address = edtAddress.getText().toString();
        String category = spinner.getSelectedItem().toString();


//        productRepository.insertProrduct(productName, quantity, description, description, category,listImage);
    }
    //Nut quay lai
    public void backActivity_onClick(View view) {
        finish();
    }
}
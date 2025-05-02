package com.example.share2need.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.R;
import com.example.share2need.adapters.ProductAdapter;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.models.Product;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText edtSearch;
    ImageView noProductFoundIcon;
    LinearLayout btnPost;
    ChipGroup chipDistanceGroup, chipCategoriesGroup;
    RecyclerView productRecyclerView;
    List<Product> productList = null;
    ProductAdapter productAdapter;
    int distance = 10;
    String category = "Tất cả";
    Location currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        currentLocation = intent.getParcelableExtra("currentLocation");

        initView();
        setupEvent();
        setupRecycleView();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProduct();
            }
        });
    }

    private void initView() {
        edtSearch = findViewById(R.id.edtSearch);
        noProductFoundIcon = findViewById(R.id.noProductFoundIcon);
        chipDistanceGroup = findViewById(R.id.chipDistanceGroup);
        chipCategoriesGroup = findViewById(R.id.chipCategoriesGroup);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        btnPost = findViewById(R.id.btnPost);
    }

    private void setupEvent() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchProduct();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        chipDistanceGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = findViewById(checkedId);
            if(selectedChip != null){
                distance = Integer.parseInt(selectedChip.getText().toString().replace("km","").trim());
                //searchProduct()
            }
        });

        chipCategoriesGroup.setOnCheckedChangeListener((group, checkedId) -> {
          Chip selectedChip = findViewById(checkedId);
          if(selectedChip != null){
              category = selectedChip.getText().toString().trim();
            //searchProduct();
          }
        });
    }

    private void setupRecycleView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        productRecyclerView.setAdapter(productAdapter);
    }

    public void searchProduct(){
        String keyword = edtSearch.getText().toString().trim();
        ProductRepository productRepository = new ProductRepository();
        Log.d("testSearch", "testOnClickSearch");
        productRepository.searchProduct(keyword, distance, category,currentLocation,  new ProductRepository.SearchCallback() {
            @Override
            public void onSearchSuccess(List<Product> products) {
                if (products != null) {
                    Log.d("testSearch", "testSearchSuccess" + products.size());
                    noProductFoundIcon.setVisibility(View.GONE);
                    productRecyclerView.setVisibility(View.VISIBLE);
                    productList.clear();
                    productList.addAll(products);
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.d("testSearch", "testSearchNone" + products.size());
                    noProductFoundIcon.setVisibility(View.VISIBLE);
                    productRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchError(Exception e) {
                Log.d("testSearch", "FAIL");
            }
        });
    }
    public void backSearchActivity_onClick(View view) {
        finish();
    }

}
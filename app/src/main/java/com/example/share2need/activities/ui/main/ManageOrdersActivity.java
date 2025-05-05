package com.example.share2need.activities.ui.main;

import android.os.Bundle;

import com.example.share2need.R;
import com.example.share2need.activities.ui.main.ui.main.OrderListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.share2need.activities.ui.main.ui.main.SectionsPagerAdapter;
import com.example.share2need.databinding.ActivityManageOrdersBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class ManageOrdersActivity extends AppCompatActivity {
    TabLayout tabLayout;
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager2 viewPager;
    private ActivityManageOrdersBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityManageOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this);
        viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Đang giao"); break;
                case 1: tab.setText("Đã giao"); break;
                case 2: tab.setText("Đã hủy"); break;
            }
        }).attach();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                // Lấy Fragment hiện tại
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);

                // Kiểm tra xem Fragment có phải là OrderListFragment không
                if (fragment instanceof OrderListFragment) {
                    // Tải lại dữ liệu trong Fragment
                    ((OrderListFragment) fragment).reloadData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);
                if (fragment instanceof OrderListFragment) {
                    ((OrderListFragment) fragment).reloadData();
                }
            }
        });

    }


    public void backActivity_onClick(View view) {
        finish();
    }
}
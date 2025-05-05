package com.example.share2need.activities.ui.main.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    public SectionsPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return OrderListFragment.newInstance("đang giao");
            case 1: return OrderListFragment.newInstance("đã nhận");
            case 2: return OrderListFragment.newInstance("hủy");
            default: return OrderListFragment.newInstance("đang giao");
        }

    }

    @Override
    public int getItemCount() {
        return 3; // số lượng tab
    }
}

package com.example.tluofficehours;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyAppointmentActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager); // Sửa ID này thành id.viewPager
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // --- Bắt đầu phần setup ViewPager2 và TabLayout ---
        // Adapter cho ViewPager2 để hiển thị các Fragment tĩnh
        AppointmentsStaticPagerAdapter pagerAdapter = new AppointmentsStaticPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Sắp tới");
                            break;
                        case 1:
                            tab.setText("Lịch sử");
                            break;
                    }
                }).attach();

        // Handle Bottom Navigation (giữ nguyên từ các màn hình trước)
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Navigate to home
                Intent intent = new Intent(this, com.example.tluofficehours.view.StudentMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_calendar) {
                // Đã ở trang lịch, không làm gì cả
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Navigate to profile
                Intent intent = new Intent(this, com.example.tluofficehours.view.StudentProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
        // Chọn item tương ứng trong Bottom Navigation (giả sử icon lịch được chọn)
        bottomNavigationView.setSelectedItemId(R.id.navigation_calendar);
    }

    // Adapter tĩnh cho ViewPager2 để hiển thị các Fragment mẫu
    private static class AppointmentsStaticPagerAdapter extends FragmentStateAdapter {

        public AppointmentsStaticPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Trả về Fragment với layout tĩnh tương ứng
            switch (position) {
                case 0:
                    return new UpcomingAppointmentsFragment();
                case 1:
                    return new HistoryAppointmentsFragment();
                default:
                    return new UpcomingAppointmentsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Có 2 tab: Sắp tới và Lịch sử
        }
    }
}
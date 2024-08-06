package com.vdsl.myapplication.Home.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.vdsl.myapplication.AllProducts.FragmentAllProds;
import com.vdsl.myapplication.Cart.Fragment.FragmentCart;
import com.vdsl.myapplication.Home.Fragment.FragmentProduct;
import com.vdsl.myapplication.Order.FragmentOrder;
import com.vdsl.myapplication.Profile.FragmentProfile;
import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivityHome extends AppCompatActivity {

    ActivityHomeBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,new FragmentProduct()).commit();


        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.item_home) {
                selectedFragment = new FragmentProduct();
                binding.layouheader.setVisibility(View.VISIBLE);
                binding.imgAvatar.setVisibility(View.VISIBLE);
            } else if (item.getItemId() == R.id.item_cart) {
                selectedFragment = new FragmentCart();
                binding.layouheader.setVisibility(View.VISIBLE);
                binding.imgAvatar.setVisibility(View.GONE);
            } else if (item.getItemId() == R.id.item_all_prod) {
                selectedFragment = new FragmentAllProds();
                binding.layouheader.setVisibility(View.VISIBLE);
                binding.imgAvatar.setVisibility(View.GONE);
            }  else if (item.getItemId() == R.id.item_profile) {
                selectedFragment = new FragmentProfile();
                binding.layouheader.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setSelectedItemId(R.id.item_home);
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            binding.txtHeaderTitle.setText(item.getTitle());
            getSupportFragmentManager().beginTransaction().replace(R.id.fr_container, selectedFragment).commit();
            return true;
        });
    }

    public void hideLayoutHeaderAndBottomNav() {
        binding.layouheader.setVisibility(View.GONE);
        binding.bottomNavigation.setVisibility(View.GONE);
    }

    public void showLayoutHeaderAndBottomNav() {
        binding.layouheader.setVisibility(View.VISIBLE);
        binding.bottomNavigation.setVisibility(View.VISIBLE);
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            showLayoutHeaderAndBottomNav();
            super.onBackPressed();
        }
    }

}
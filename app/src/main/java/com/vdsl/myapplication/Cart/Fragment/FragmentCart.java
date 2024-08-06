package com.vdsl.myapplication.Cart.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vdsl.myapplication.APIService;
import com.vdsl.myapplication.Cart.Adapter.CartAdapter;
import com.vdsl.myapplication.Cart.Model.CartModel;
import com.vdsl.myapplication.Home.Model.ProductModel;
import com.vdsl.myapplication.Pay.FragmentPay;
import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.FragmentCartBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FragmentCart extends Fragment implements CartAdapter.CartUpdateListener{

    FragmentCartBinding binding;
    List<CartModel> cartList;
    CartAdapter cartAdapter;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      binding = FragmentCartBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartList, getContext(), (CartAdapter.CartUpdateListener) this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcCart.setLayoutManager(linearLayoutManager);
        binding.rcCart.setAdapter(cartAdapter);
        sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);


        fetchAllCarts();

        binding.btnPay.setOnClickListener(v -> {
            FragmentPay fragmentPay= new FragmentPay();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fr_container,fragmentPay)
                    .addToBackStack(null)
                    .commit();
        });

    }

    private void fetchAllCarts() {
        String uid = sharedPreferences.getString("uid", "");
        Log.d("TAG", "id cart: "+uid);
        Log.d("TAG", "fetchAllCarts: uid"+uid);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        Call<List<CartModel>> call = apiService.getCartItems(uid);
        call.enqueue(new Callback<List<CartModel>>() {
            @Override
            public void onResponse(Call<List<CartModel>> call, Response<List<CartModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartList.clear();
                    cartList.addAll(response.body());
                    cartAdapter.notifyDataSetChanged();
//                    double totalPrice = 0;
//                    for (CartModel item : cartList) {
//                        totalPrice += item.getProduct().getPrice() * item.getQuantity();
//                    }
//                    binding.txtToTalPrice.setText(String.format("%.2f", totalPrice));
                    updateTotalPrice();
                } else {
                    Log.e("FragmentCart", "Failed to fetch cart items");
                }
            }

            @Override
            public void onFailure(Call<List<CartModel>> call, Throwable t) {
                Log.e("FragmentCart", "Error fetching cart items: " + t.getMessage());
            }
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartModel item : cartList) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        binding.txtToTalPrice.setText(String.format("%.2f", totalPrice));
    }


    @Override
    public void onCartUpdated() {
        updateTotalPrice();
    }

}
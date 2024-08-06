package com.vdsl.myapplication.Pay;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vdsl.myapplication.APIService;
import com.vdsl.myapplication.AllProducts.FragmentAllProds;
import com.vdsl.myapplication.Cart.Adapter.CartAdapter;
import com.vdsl.myapplication.Cart.Model.CartModel;
import com.vdsl.myapplication.Home.Activity.ActivityHome;
import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.FragmentCartBinding;
import com.vdsl.myapplication.databinding.FragmentPayBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentPay extends Fragment {

    FragmentPayBinding binding;
    ArrayList<CartModel> listPay;
    PayAdapter payAdapter;
    private static final String[] paymethod = {"Cash", "credit card"};
    CartModel cartModel;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof ActivityHome) {
            ((ActivityHome) getActivity()).hideLayoutHeaderAndBottomNav();
        }


        listPay = new ArrayList<>();
        payAdapter = new PayAdapter(listPay, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcItemPay.setLayoutManager(linearLayoutManager);
        binding.rcItemPay.setAdapter(payAdapter);
        cartModel = new CartModel();

         sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, paymethod);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spPaymethod.setAdapter(adapter);

        binding.spPaymethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMethod = (String) parent.getItemAtPosition(position);
                Log.d("FragmentPay", "Selected payment method: " + selectedMethod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fetchAllItem();


        binding.btnCancel.setOnClickListener(v -> {

        });
        binding.btnPayNow.setOnClickListener(v -> {
            boolean err = false;

            String fullname= binding.edtFullName.getText().toString().trim();
            String address= binding.edtAddress.getText().toString().trim();
            String phoneNumber= binding.edtPhoneNumber.getText().toString().trim();
//             String PHONE_REGEX = "^0\\d{9}$";
             String PHONE_REGEX = "^0\\d{9}$";

            if(fullname.isEmpty()){
                err=true;
                binding.edtFullName.setError("please enter you name");
            }
            if(address.isEmpty()){
                err=true;
                binding.edtAddress.setError("please enter you address");
            }
            if(phoneNumber.isEmpty()){
                err=true;
                binding.edtPhoneNumber.setError("please enter you name");
            }else if(!phoneNumber.matches(PHONE_REGEX)){
                err=true;
                binding.edtPhoneNumber.setError("please enter number and start with 0");
            }

            if(!err){
                BillModel newBill = new BillModel();
                newBill.setUserId("user_id_example");  // Thay thế bằng ID người dùng thực tế
                newBill.setFullname(fullname);
                newBill.setAddress(address);
                newBill.setPhoneNumber(phoneNumber);
                newBill.setNote(binding.edtNote.getText().toString().trim());
                newBill.setPaymethod(Arrays.toString(paymethod));
                newBill.setProduct(listPay);  // listPay là danh sách sản phẩm trong giỏ hàng

                // Gửi yêu cầu thêm hóa đơn mới
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(APIService.DOMAIN)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                APIService apiService = retrofit.create(APIService.class);
                Call<BillModel> call = apiService.addBill(newBill);
                call.enqueue(new Callback<BillModel>() {
                    @Override
                    public void onResponse(Call<BillModel> call, Response<BillModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Xử lý thành công
                            Toast.makeText(getContext(), "Bill added successfully", Toast.LENGTH_SHORT).show();
                            FragmentAllProds fragmentAllProds= new FragmentAllProds();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fr_container,fragmentAllProds)
                                    .addToBackStack(null)
                                    .commit();
                            Log.d("FragmentPay", "Bill added successfully: " + response.body());
                        } else {
                            Toast.makeText(getContext(), "Pay failed", Toast.LENGTH_SHORT).show();
                            Log.e("FragmentPay", "Failed to add bill");
                        }
                    }

                    @Override
                    public void onFailure(Call<BillModel> call, Throwable t) {
                        Log.e("FragmentPay", "Error adding bill: " + t.getMessage());
                    }
                });
            }

        });


    }

    private void fetchAllItem() {
        String uid = sharedPreferences.getString("uid", "");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.DOMAIN).addConverterFactory(GsonConverterFactory.create()).build();
        APIService apiService = retrofit.create(APIService.class);
        Call<List<CartModel>> call = apiService.getCartItems(uid);
        call.enqueue(new Callback<List<CartModel>>() {
            @Override
            public void onResponse(Call<List<CartModel>> call, Response<List<CartModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listPay.clear();
                    listPay.addAll(response.body());
                    payAdapter.notifyDataSetChanged();
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
        for (CartModel item : listPay) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        binding.txtTotalPrice.setText(String.format("%.2f", totalPrice));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show header and bottom navigation when this fragment is destroyed
        if (getActivity() instanceof ActivityHome) {
            ((ActivityHome) getActivity()).showLayoutHeaderAndBottomNav();
        }
    }
}
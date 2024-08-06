package com.vdsl.myapplication.AllProducts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.vdsl.myapplication.APIService;
import com.vdsl.myapplication.Category.CategoryModel;
import com.vdsl.myapplication.Home.Model.ProductModel;
import com.vdsl.myapplication.databinding.DialogProductBinding;
import com.vdsl.myapplication.databinding.FragmentAllProdsBinding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentAllProds extends Fragment {

    private static final int MY_REQUEST_CODE = 10;
    FragmentAllProdsBinding binding;
    List<ProductModel> productList;
    AllProdsAdapter allProdsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllProdsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productList = new ArrayList<>();
        allProdsAdapter = new AllProdsAdapter(productList, getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rcProduct.setLayoutManager(gridLayoutManager);
        binding.rcProduct.setAdapter(allProdsAdapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");

        // Hide or show btnAdd based on role
        if (!"admin".equals(role)) {
            binding.btnAddProd.setVisibility(View.GONE);
        } else {
            binding.btnAddProd.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        Call<List<ProductModel>> call = apiService.getProducts();

        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();
                    allProdsAdapter = new AllProdsAdapter(productList, getActivity());
                    binding.rcProduct.setAdapter(allProdsAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });

        //search
        binding.edtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<ProductModel> newList = new ArrayList<>();
                if (newText.isEmpty() || newText.length() == 0) {
                    newList.addAll(productList);
                } else {
                    for (ProductModel prod : productList) {
                        if (prod.getName().toLowerCase().contains(newText.toLowerCase())) {
                            newList.add(prod);
                        }
                    }
                    allProdsAdapter = new AllProdsAdapter(newList, getActivity());
                    binding.rcProduct.setAdapter(allProdsAdapter);
                }
                allProdsAdapter.filterList(newList);
                return true;
            }
        });


        //if scroll down then hide btn add
        binding.rcProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.btnAddProd.hide();
                } else {
                    binding.btnAddProd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        binding.btnAddProd.setOnClickListener(v -> {
            DialogProductBinding dialogBinding = DialogProductBinding.inflate(getLayoutInflater());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(dialogBinding.getRoot());
            Dialog dialog = builder.create();

            fetchCategories(dialogBinding);

            dialogBinding.btnCancel.setOnClickListener(v1 -> {
                dialog.cancel();
            });

            dialogBinding.btnSave.setOnClickListener(v2 -> {
                String pName = dialogBinding.edtPName.getText().toString().trim();
                String pPrice = dialogBinding.edtPPrice.getText().toString().trim();
                String pAddress = dialogBinding.edtPAddress.getText().toString().trim();
                String pLinkImage = dialogBinding.edtPLinkImage.getText().toString().trim();
                String pSize = dialogBinding.edtPSize.getText().toString().trim();
                String pCategory = dialogBinding.spCategories.getSelectedItem().toString().trim();



                boolean err = false;

                if (pName.isEmpty()) {
                    dialogBinding.edtPName.setError("Please enter product name!");
                    err = true;
                }
                if (pPrice.isEmpty()) {
                    dialogBinding.edtPPrice.setError("Please enter product price!");
                    err = true;
                } else {
                    try {
                        double price = Double.parseDouble(pPrice);
                        if (price < 0) {
                            dialogBinding.edtPPrice.setError("Please enter number > 0");
                            err = true;
                        }
                    } catch (NumberFormatException e) {
                        dialogBinding.edtPPrice.setError("Please enter number format!");
                        err = true;
                    }
                }
                if (pAddress.isEmpty()) {
                    dialogBinding.edtPAddress.setError("Please enter product address!");
                    err = true;
                }
                if (pLinkImage.isEmpty()) {
                    dialogBinding.edtPLinkImage.setError("Please enter product link image!");
                    err = true;
                }
                if (pSize.isEmpty()) {
                    dialogBinding.edtPSize.setError("Please enter product size!");
                    err = true;
                }

                if (!err) {
                    ProductModel newProduct = new ProductModel(pName, pAddress, pLinkImage, pSize, pCategory, Double.parseDouble(pPrice));
                    Log.d("Main", "Sending new product: " + newProduct);
                    Call<ProductModel> addCall = apiService.addProduct(newProduct);
                    addCall.enqueue(new Callback<ProductModel>() {
                        @Override
                        public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                            if (response.isSuccessful()) {
                                productList.add(response.body());
                                allProdsAdapter.notifyItemInserted(productList.size() - 1);
                                allProdsAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            } else {
                                // Handle error response
                                Log.e("Main", "Failed to add product. Response code: " + response.code() + ", Message: " + response.message());
                                // Log response body if possible
                                try {
                                    if (response.errorBody() != null) {
                                        Log.e("Main", "Error body: " + response.errorBody().string());
                                    }
                                } catch (IOException e) {
                                    Log.e("Main", "Error reading error body", e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductModel> call, Throwable t) {
                            Log.e("Main", t.getMessage());
                        }
                    });
                }

            });

            dialog.show();
        });

    }

    private void fetchCategories(DialogProductBinding dialogBinding) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        Call<List<CategoryModel>> call = apiService.getCategories();  // Assuming you have a getCategories() method in APIService

        call.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                if (response.isSuccessful()) {
                    List<CategoryModel> categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    for (CategoryModel category : categories) {
                        categoryNames.add(category.getTitle());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dialogBinding.spCategories.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });

    }
}
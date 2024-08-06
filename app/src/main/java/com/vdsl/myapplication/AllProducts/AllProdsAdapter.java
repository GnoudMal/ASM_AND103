package com.vdsl.myapplication.AllProducts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdsl.myapplication.APIService;
import com.vdsl.myapplication.Cart.Model.CartModel;
import com.vdsl.myapplication.Category.CategoryModel;
import com.vdsl.myapplication.Home.Adapter.ProductAdapter;
import com.vdsl.myapplication.Home.Model.ProductModel;
import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.DialogConfirmBinding;
import com.vdsl.myapplication.databinding.DialogProductBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllProdsAdapter extends RecyclerView.Adapter<AllProdsAdapter.AllProdsViewHolder> {

    private List<ProductModel> productList;
    Context context;

    APIService apiService;

    public AllProdsAdapter(List<ProductModel> productList, Context context) {
        this.productList = productList;
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);
    }

    public void filterList(ArrayList<ProductModel> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllProdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new AllProdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllProdsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductModel productModel = productList.get(position);

        holder.txtName.setText(productModel.getName());
        holder.txtCategory.setText(productModel.getCategory());
        holder.txtPrice.setText(String.valueOf(productModel.getPrice()));
        Picasso.get().load(productModel.getImage()).into(holder.imgProduct);

        holder.itemView.setOnLongClickListener(v -> {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            DialogProductBinding dialogBinding = DialogProductBinding.inflate(inflater);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogBinding.getRoot());
            Dialog dialog = builder.create();

            dialogBinding.btnCancel.setOnClickListener(v1 -> {
                dialog.cancel();
            });

            fetchCategories(dialogBinding,productModel.getCategory());

            dialogBinding.edtPName.setText(productList.get(position).getName());
            dialogBinding.edtPPrice.setText(String.valueOf(productList.get(position).getPrice()));
            dialogBinding.edtPAddress.setText(productList.get(position).getAddress());
            dialogBinding.edtPLinkImage.setText(productList.get(position).getImage());
            dialogBinding.edtPSize.setText(productList.get(position).getSize());

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
                    ProductModel updatedProduct = new ProductModel(
                            productModel.get_id(),  // Lấy ID sản phẩm cần sửa
                            pName, pAddress, pLinkImage, pSize, pCategory, Double.parseDouble(pPrice)
                    );

//                    APIService apiService = RetrofitClient.getClient().create(APIService.class);
                    Call<ProductModel> updateCall = apiService.updateProduct(productModel.get_id(), updatedProduct);
                    updateCall.enqueue(new Callback<ProductModel>() {
                        @Override
                        public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                            if (response.isSuccessful()) {
                                productList.set(position, updatedProduct);
                                notifyItemChanged(position);
                                dialog.dismiss();
                            } else {
                                // Xử lý lỗi khi không thành công
                                Log.e("AllProdsAdapter", "Failed to update product. Response code: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductModel> call, Throwable t) {
                            Log.e("AllProdsAdapter", "Error updating product: " + t.getMessage());
                        }
                    });
                }

            });
            dialog.show();
            return true;
        });

        holder.imgDelete.setOnClickListener(v -> {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            DialogConfirmBinding dialogBinding = DialogConfirmBinding.inflate(inflater);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogBinding.getRoot());
            Dialog dialog = builder.create();

            dialogBinding.btnNo.setOnClickListener(v1 -> {
                dialog.cancel();
            });
            dialogBinding.btnYes.setOnClickListener(v2 -> {
                Call<Void> deleteCall = apiService.deleteProduct(productModel.get_id());
                deleteCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            productList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, productList.size());
                            dialog.dismiss();
                        } else {
                            Log.e("AllProdsAdapter", "Failed to delete product. Response code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("AllProdsAdapter", "Error deleting product: " + t.getMessage());
                    }
                });
            });
            dialog.show();

        });

        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");


        holder.btnAddToCard.setOnClickListener(v -> {
            String userId = uid; // Thay thế bằng userId thực tế
            Log.d("TAG", "onBindViewHolder: "+uid);
            CartModel cartItem = new CartModel(userId, productModel, 1);
            Call<Void> addToCartCall = apiService.addToCart(cartItem);
            addToCartCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("AllProdsAdapter", "Failed to add to cart. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("AllProdsAdapter", "Error adding to cart: " + t.getMessage());
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class AllProdsViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCategory, txtPrice;
        ImageView imgProduct, imgDelete;
        Button btnAddToCard;

        public AllProdsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            btnAddToCard = itemView.findViewById(R.id.btnAddToCard);
        }
    }

    private void fetchCategories(DialogProductBinding dialogBinding,String selectedCategory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        Call<List<CategoryModel>> call = apiService.getCategories();

        call.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                if (response.isSuccessful()) {
                    List<CategoryModel> categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    for (CategoryModel category : categories) {
                        categoryNames.add(category.getTitle());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dialogBinding.spCategories.setAdapter(adapter);

                    // Set the current category
//                    int categoryPosition = categoryNames.indexOf(productList.get(dialogBinding.spCategories.getSelectedItemPosition()).getCategory());
//                    dialogBinding.spCategories.setSelection(categoryPosition);
                    int categoryPosition = categoryNames.indexOf(selectedCategory);
                    if (categoryPosition >= 0) {
                        dialogBinding.spCategories.setSelection(categoryPosition);
                    }
                } else {
                    Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

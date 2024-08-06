package com.vdsl.myapplication.Cart.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdsl.myapplication.APIService;
import com.vdsl.myapplication.Cart.Model.CartModel;
import com.vdsl.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {


    private List<CartModel> cartList;
    Context context;
    APIService apiService;
    private CartUpdateListener cartUpdateListener;

    private static final String[] SIZES = {"130mm", "150mm", "160mm", "170mm"};


    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(List<CartModel> cartList, Context context, CartUpdateListener cartUpdateListener) {
        this.cartList = cartList;
        this.context = context;
        this.cartUpdateListener = cartUpdateListener;

        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.DOMAIN).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartModel cartModel = cartList.get(position);

        holder.txtProductName.setText(cartModel.getProduct().getName());
        holder.txtProductCategory.setText(cartModel.getProduct().getCategory());
        holder.txtProductPrice.setText(String.valueOf(cartModel.getProduct().getPrice()));
        holder.txtProductQuantity.setText(String.valueOf(cartModel.getQuantity()));
        Picasso.get().load(cartModel.getProduct().getImage()).into(holder.imgProduct);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, SIZES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerSize.setAdapter(adapter);

        holder.spinnerSize.setSelection(adapter.getPosition(cartModel.getProduct().getSize())); // Set previously selected size

        holder.spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSize = (String) parent.getItemAtPosition(position);
                if (!selectedSize.equals(cartModel.getProduct().getSize())) {
                    cartModel.getProduct().setSize(selectedSize);
                    updateSize(cartModel, position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        holder.btnAscQuantity.setOnClickListener(v -> {
            int newQuantity = cartModel.getQuantity() + 1;
            updateQuantity(cartModel, newQuantity, position);
        });

        holder.btnDescQuantity.setOnClickListener(v -> {
            if (cartModel.getQuantity() > 1) {
                int newQuantity = cartModel.getQuantity() - 1;
                updateQuantity(cartModel, newQuantity, position);
            }
        });

        holder.imgX.setOnClickListener(v -> {
            new AlertDialog.Builder(context).setTitle("Xác nhận xóa").setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?").setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Gọi API để xóa sản phẩm khỏi giỏ hàng
                    apiService.deleteCartItem(cartModel.get_id()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Xóa sản phẩm khỏi danh sách và cập nhật RecyclerView
                                cartList.remove(position);
                                Log.d("cart item id 1", "cart item id: " + cartModel.get_id());
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                                if (cartUpdateListener != null) {
                                    cartUpdateListener.onCartUpdated();
                                }
                            } else {
                                Log.d("cart item id 2", "cart item id: " + cartModel.get_id());
                                Toast.makeText(context, "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show();
                                String errorMessage = "Lỗi xóa sản phẩm: " + response.message();
                                Log.e("TAG", errorMessage);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("TAG", "Lỗi khi gọi API xóa sản phẩm: " + t.getMessage());
                            Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).setNegativeButton("Hủy", null).show();
        });


    }


    private void updateQuantity(CartModel cartModel, int newQuantity, int position) {
//        cartModel.setQuantity(newQuantity);
        Map<String, Integer> body = new HashMap<>();
        body.put("quantity", newQuantity);
        // Gửi yêu cầu cập nhật số lượng trên server
        apiService.updateCartItemQuantity(cartModel.get_id(), body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cartModel.setQuantity(newQuantity);
                    notifyItemChanged(position);
                    // Thông báo cho FragmentCart để cập nhật tổng tiền
                    if (cartUpdateListener != null) {
                        cartUpdateListener.onCartUpdated();
                    }
                } else {
                    Toast.makeText(context, "Không thể cập nhật số lượng", Toast.LENGTH_SHORT).show();
                    String errorMessage = "Lỗi cập nhật số lượng: " + response.message();
                    Log.e("TAG", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TAG", "Lỗi khi gọi API cập nhật số lượng: " + t.getMessage());
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSize(CartModel cartModel, int position) {
        Map<String, String> body = new HashMap<>();
        body.put("size", cartModel.getProduct().getSize());
        // Gửi yêu cầu cập nhật kích thước trên server
        apiService.updateCartItemSize(cartModel.get_id(), body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(context, "Không thể cập nhật kích thước", Toast.LENGTH_SHORT).show();
                    String errorMessage = "Lỗi cập nhật kích thước: " + response.message();
                    Log.e("TAG", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TAG", "Lỗi khi gọi API cập nhật kích thước: " + t.getMessage());
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductCategory, txtProductPrice, txtProductQuantity;
        Button btnDescQuantity, btnAscQuantity;
        ImageView imgProduct, imgX;
        Spinner spinnerSize;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductCategory = itemView.findViewById(R.id.txtProductCategory);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
            btnDescQuantity = itemView.findViewById(R.id.btnDescQuantity);
            btnAscQuantity = itemView.findViewById(R.id.btnAscQuantity);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgX = itemView.findViewById(R.id.imgX);
            spinnerSize = itemView.findViewById(R.id.spinnerSize);
        }
    }

}

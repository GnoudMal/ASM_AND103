package com.vdsl.myapplication.Pay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdsl.myapplication.APIService;
import com.vdsl.myapplication.Cart.Adapter.CartAdapter;
import com.vdsl.myapplication.Cart.Model.CartModel;
import com.vdsl.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.PayViewHolder>{

    ArrayList<CartModel> listItem ;
    Context context;
    APIService apiService;

    public PayAdapter(ArrayList<CartModel> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.DOMAIN).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);
    }

    @NonNull
    @Override
    public PayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new PayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayViewHolder holder, int position) {
        CartModel cartModel = listItem.get(position);

        holder.txtProduct.setText(cartModel.getProduct().getName());
        holder.txtSize.setText(cartModel.getProduct().getSize());
        holder.txtQuantity.setText(String.valueOf(cartModel.getQuantity()));
        holder.txtprice.setText(String.valueOf(cartModel.getProduct().getPrice()));
        Picasso.get().load(cartModel.getProduct().getImage()).into(holder.imgProduct);


    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    class PayViewHolder extends RecyclerView.ViewHolder{
        TextView txtProduct, txtSize, txtQuantity, txtprice;
        ImageView imgProduct;
        public PayViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProduct = itemView.findViewById(R.id.txtProduct);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtprice = itemView.findViewById(R.id.txtprice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }

}

package com.vdsl.myapplication;


import com.vdsl.myapplication.Cart.Model.CartModel;
import com.vdsl.myapplication.Category.CategoryModel;
import com.vdsl.myapplication.Home.Model.ProductModel;
import com.vdsl.myapplication.Pay.BillModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface APIService {

//     String DOMAIN = "http://192.168.1.10:3000/";
     String DOMAIN = "http://10.0.2.2:3000/";
//
     @GET("/api/listProducts")
     Call<List<ProductModel>> getProducts();

     // Create a new product
     @POST("/api/addProduct")
     Call<ProductModel> addProduct(@Body ProductModel product);

     // Update an existing product
     @PUT("/api/updateProduct/{id}")
     Call<ProductModel> updateProduct(@Path("id") String id, @Body ProductModel product);

     // Delete a product
     @DELETE("/api/deleteProduct/{id}")
     Call<Void> deleteProduct(@Path("id") String id);


    // get listCategories
    @GET("api/listCategories")
    Call<List<CategoryModel>> getCategories();

    // Get list of cart items
    @GET("api/getListCart/{uid}")
    Call<List<CartModel>> getCartItems(@Path("uid") String uid);

    // Phương thức thêm sản phẩm vào giỏ hàng
    @POST("/api/addToCart")
    Call<Void> addToCart(@Body CartModel cartItem);

    // xóa khỏi giỏ hàng
    @DELETE("/api/removeCartItem/{id}")
    Call<Void> deleteCartItem(@Path("id") String id);

    @PUT("/api/updateCartItem/{id}")
    Call<Void> updateCartItemQuantity(@Path("id") String id, @Body Map<String, Integer> body);

    @PUT("/api/updateCartSize/{id}")
    Call<Void> updateCartItemSize(@Path("id") String id, @Body Map<String, String> body);


    @Multipart
    @POST("/api/addProductWithImage")
    Call<ProductModel> addProductWithImage(
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("size") RequestBody size,
            @Part("category") RequestBody category,
            @Part("price") RequestBody price,
            @Part MultipartBody.Part image
    );


    @POST("/api/addBill")
    Call<BillModel> addBill(@Body BillModel bill);



}

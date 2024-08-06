package com.vdsl.myapplication.Cart.Model;

import com.vdsl.myapplication.Home.Model.ProductModel;

public class CartModel {
    private String _id;
    private String userId;
    private ProductModel product;
    private int quantity;

    public CartModel() {
    }

    public CartModel(String userId, ProductModel product, int quantity) {
        this.userId = userId;
        this.product = product;
        this.quantity = quantity;
    }

    public CartModel(String _id, String userId, ProductModel product, int quantity) {
        this._id = _id;
        this.userId = userId;
        this.product = product;
        this.quantity = quantity;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

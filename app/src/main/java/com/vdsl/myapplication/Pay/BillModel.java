package com.vdsl.myapplication.Pay;

import com.vdsl.myapplication.Cart.Model.CartModel;

import java.util.ArrayList;
import java.util.List;

public class BillModel {
    String _id;
    private String userId;

    private String fullname;

    private String address;

    private String phoneNumber;

    private String note;

    private String paymethod;

    private ArrayList<CartModel> product; // List of products


    public BillModel(String userId, String fullname, String address, String phoneNumber, String note, String paymethod, ArrayList<CartModel> product) {
        this.userId = userId;
        this.fullname = fullname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.paymethod = paymethod;
        this.product = product;
    }

    public BillModel(String _id, String userId, String fullname, String address, String phoneNumber, String note, String paymethod, ArrayList<CartModel> product) {
        this._id = _id;
        this.userId = userId;
        this.fullname = fullname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.paymethod = paymethod;
        this.product = product;
    }

    public BillModel() {
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymethod() {
        return paymethod;
    }

    public void setPaymethod(String paymethod) {
        this.paymethod = paymethod;
    }

    public List<CartModel> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<CartModel> product) {
        this.product = product;
    }
}

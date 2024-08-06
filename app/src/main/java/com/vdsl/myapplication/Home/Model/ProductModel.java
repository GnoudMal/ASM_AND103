package com.vdsl.myapplication.Home.Model;

public class ProductModel {
    String _id, name, address, image, size, category;
    double price;

    public ProductModel() {
    }

    public ProductModel(String _id, String name, String address, String image, String size, String category, double price) {
        this._id = _id;
        this.name = name;
        this.address = address;
        this.image = image;
        this.size = size;
        this.category = category;
        this.price = price;
    }

    public ProductModel(String name, String address, String image, String size, String category, double price) {
        this.name = name;
        this.address = address;
        this.image = image;
        this.size = size;
        this.category = category;
        this.price = price;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

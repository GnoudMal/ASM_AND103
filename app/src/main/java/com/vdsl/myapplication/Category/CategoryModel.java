package com.vdsl.myapplication.Category;

public class CategoryModel {
    private  String _id;
    private  String title;

    public CategoryModel() {
    }

    public CategoryModel(String _id, String title) {
        this._id = _id;
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

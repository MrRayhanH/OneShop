package com.example.oneshop.Catagory;

public class Category {
    private String category_id;
    private String category_name;
    private String image_url;

    // Required empty constructor for Firebase
    public Category() {}

    public Category(String category_id, String category_name, String image_url) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.image_url = image_url;
    }

    // Getters
    public String getCategory_id() {
        return category_id != null ? category_id : "";
    }

    public String getCategory_name() {
        return category_name != null ? category_name : "Unnamed Category";
    }

    public String getImage_url() {
        return image_url != null ? image_url : "";
    }

    // Setters (Needed for Firebase)
    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}

//package com.example.oneshop.Catagory;
//
//public class Category {
//    private String category_id; // Must match the exact Firebase key
//    private String category_name;
//    private String image_url;
//
//    // Required empty constructor for Firebase
//    public Category() {
//    }
//
//    public Category(String category_id, String category_name, String image_url) {
//        this.category_id = category_id;
//        this.category_name = category_name;
//        this.image_url = image_url;
//    }
//
//    public String getCategory_id() {
//        return category_id;
//    }
//
//    public String getCategory_name() {
//        return category_name;
//    }
//
//    public String getImage_url() {
//        return image_url;
//    }
//}

package com.example.oneshop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Test_DB";
    public static final int DATABASE_VERSION = 5;

    // Table Name for register, product , Card
    public static final String TABLE_REGISTER = "register";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CARD = "card";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_FAVOURITE = "favourite";

    // Column Names for register
    public static final String COL_ID = "_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_MOBILE = "mobile";
    public static final String COL_PROFILE = "profile";

    // Column Names for product
    public static final String COL_PRODUCT_NAME = "productName";
    public static final String COL_PRODUCT_PRICE = "productPrice";
    public static final String COL_PRODUCT_QUANTITY = "productQuantity";
    public static final String COL_PRODUCT_IMAGE_URI = "productImageUri";
    public static final String COL_PRODUCT_SIZES = "productSize";
    public static final String COL_PRODUCT_RATING = "productRating";

    // Column Names for card

    public static final String COL_CARD_ID = "card_id";
    public static final String COL_PRODUCT_NAME_CARD = "productNameCard";
    public static final String COL_PRODUCT_PRICE_CARD = "productPriceCard";
    public static final String COL_PRODUCT_QUANTITY_CARD = "productQuantityCard";
    public static final String COL_PRODUCT_IMAGE_URI_CARD = "productImageUriCard";

    // Column Names for Category
    public static final String COL_CATEGORY_NAME = "categoryName";
    public static final String COL_CATEGORY_IMAGE_URI = "categoryImageUri";

    //For table favourite
    public static  final String COL_FAVOURITE_ID = "_id";
    public static  final String COL_FAVOURITE_ID_PRUDUCT = "product_id";

    // Constructor for database
    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REGISTER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_MOBILE + " TEXT, " +
                COL_PROFILE + " BLOB)");

        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT, " +
                COL_PRODUCT_PRICE + " REAL, " +
                COL_PRODUCT_RATING + " REAL, " +
                COL_PRODUCT_QUANTITY + " INTEGER, " +
                COL_CATEGORY_NAME + " TEXT, " +
                COL_PRODUCT_SIZES + " JSON, " +
                COL_PRODUCT_IMAGE_URI + " BLOB)");

        db.execSQL("CREATE TABLE " + TABLE_CARD + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME_CARD + " TEXT, " +
                COL_CATEGORY_NAME + " TEXT, " +
                COL_PRODUCT_PRICE_CARD + " REAL, " +
                COL_PRODUCT_QUANTITY_CARD + " INTEGER, " +
                COL_PRODUCT_IMAGE_URI_CARD + " BLOB, " +
                "FOREIGN KEY(" + COL_PRODUCT_NAME_CARD + ") REFERENCES " + TABLE_PRODUCTS + "(" + COL_PRODUCT_NAME + "))");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CATEGORY_NAME + " TEXT, " +
                COL_CATEGORY_IMAGE_URI + " BLOB)");

        db.execSQL("CREATE TABLE " + TABLE_FAVOURITE + " (" +
                COL_FAVOURITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAVOURITE_ID_PRUDUCT + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }
    public boolean insertUser(String username, String email, String password, String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_MOBILE, mobile);

        long result =  db.insert(TABLE_REGISTER, null, contentValues);
        return result != -1;
    }

    public boolean checkUserByUsername(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REGISTER + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getPassword(String username, String email, String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PASSWORD + " FROM " + TABLE_REGISTER +
                " WHERE " + COL_USERNAME + " = ? AND " + COL_EMAIL + " = ? AND " + COL_MOBILE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] {username, email, mobile});

        String password = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                //password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
            }
            cursor.close();
        }
        db.close();
        return password;
    }


    public void insertProduct(String name, double price, int quantity, String catagory, byte[] imageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, name);
        values.put(COL_PRODUCT_PRICE, price);
        values.put(COL_PRODUCT_QUANTITY, quantity);
        values.put(COL_CATEGORY_NAME, catagory);
        values.put(COL_PRODUCT_IMAGE_URI, imageByteArray);
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }
    public boolean insertCategory(String category, byte[] imageByteArray) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CATEGORY_NAME, category);
        values.put(COL_CATEGORY_IMAGE_URI, imageByteArray);

        long result = db.insert(TABLE_CATEGORY, null, values);

        db.close();
        return result != -1;
    }


    public void addProductToCart(String name, double price, int quantity, byte[] imageByteArray, int cartId) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues values1 = new ContentValues();

        values1.put(COL_ID, cartId);
        values1.put(COL_PRODUCT_NAME_CARD, name);
        values1.put(COL_PRODUCT_PRICE_CARD, price);
        values1.put(COL_PRODUCT_QUANTITY_CARD, quantity);
        values1.put(COL_PRODUCT_IMAGE_URI_CARD, imageByteArray);
        db1.insert(TABLE_CARD, null, values1);
        db1.close();


    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }
    public Cursor getAllByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_CATEGORY_NAME + " = ?", new String[]{category});
    }



    public Cursor getProductByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_NAME + " = ?", new String[]{productName});

    }
    public Cursor getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(productId)});
    }
    public Cursor getProductsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_CATEGORY_NAME + " = ?", new String[]{category});
    }

    public void updateProduct(int productId, String productName, double price, int quantity, byte[] productImageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PRODUCT_NAME, productName);
        values.put(COL_PRODUCT_PRICE, price);
        values.put(COL_PRODUCT_QUANTITY, quantity);
        values.put(COL_PRODUCT_IMAGE_URI, productImageByteArray);
        db.update(TABLE_PRODUCTS, values, COL_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public void deleteProduct(String productName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COL_PRODUCT_NAME + " = ?", new String[]{productName});
        db.close();
    }


    // for card section
    public Cursor getAllCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CARD, null);
    }

    public boolean isProductInCart(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CARD + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(productId)});

        boolean exists = false;
        if (cursor != null) {
            exists = cursor.getCount() > 0; // Check if the cursor has at least one row
            cursor.close(); // Close the cursor to avoid memory leaks
        }

        db.close(); // Close the database connection
        return exists;
    }


    public void updateCartQuantity(int id, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_QUANTITY_CARD, newQuantity);
        db.update(TABLE_CARD, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

    }


    // Remove product from cart
    public void removeProductFromCart(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARD, COL_ID + " = ?", new String[]{String.valueOf(cartId)});
        db.close();
    }


    // Get all cart items with product details



    public Cursor getCartProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CARD, null);
    }


    // For Catagory
    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
    }

    public Cursor getAllCategoriesName() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT categoryName FROM " + TABLE_CATEGORY, null);
    }

    public Cursor getCategoryByName(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COL_CATEGORY_NAME + " = ?", new String[]{categoryName});
    }

    public boolean deleteCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CATEGORY, COL_CATEGORY_NAME + " = ?", new String[]{categoryName});
        db.close();
        return rowsDeleted !=-1;
    }

    public Cursor searchProductsOrCategories(String searchQuery) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " +
                COL_PRODUCT_NAME + " LIKE ? OR " +
                COL_CATEGORY_NAME + " LIKE ?";
        return db.rawQuery(query, new String[]{"%" + searchQuery + "%", "%" + searchQuery + "%"});
    }

    public void addToFavourite(int productId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAVOURITE_ID_PRUDUCT, productId);
        db.insert(TABLE_FAVOURITE, null, values);
        db.close();
    }
    public boolean isProductInFavourite(int productId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVOURITE + " WHERE " + COL_FAVOURITE_ID_PRUDUCT + " = ?", new String[]{String.valueOf(productId)});
        boolean isFavourite =  cursor.getCount() > 0;
        cursor.close();;
        db.close();
        return isFavourite;
    }

    public Cursor getFavouriteProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS +
                " WHERE " + COL_ID + " IN (" +
                "SELECT " + COL_FAVOURITE_ID_PRUDUCT + " FROM " + TABLE_FAVOURITE + ")";
        return db.rawQuery(query, null);
    }
    public boolean deleteProductFromFavourite(int productId){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_FAVOURITE, COL_FAVOURITE_ID_PRUDUCT + " = ?", new String[]{String.valueOf(productId)});
        db.close();
        return rowsDeleted !=-1;
    }




}

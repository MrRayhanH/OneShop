package com.example.oneshop.ProductDisplay;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oneshop.Catagory.Category;
import com.example.oneshop.Products.ProductS;
import com.example.oneshop.R;
import com.example.oneshop.SettingActivity;
import com.example.oneshop.CardActivity;
import com.example.oneshop.FavouriteActivity;
import com.google.firebase.database.*;
import com.mancj.materialsearchbar.MaterialSearchBar;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsDisplay extends AppCompatActivity {

    private static final int VOICE_SEARCH_REQUEST_CODE = 100;

    private RecyclerView recyclerViewProducts, recyclerViewCategories;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private List<ProductS> productList;
    private List<Category> categoryList;
    private MaterialSearchBar searchBar;
    private DatabaseReference productRef, categoryRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_display);

        // Firebase References
        productRef = FirebaseDatabase.getInstance().getReference("products");
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        // Initialize UI
        recyclerViewProducts = findViewById(R.id.recycler_view1);
        recyclerViewCategories = findViewById(R.id.recycler_view_catagory);
        searchBar = findViewById(R.id.search_bar);

        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 4));

        productList = new ArrayList<>();
        categoryList = new ArrayList<>();

        productAdapter = new ProductAdapter(this, productList);
        categoryAdapter = new CategoryAdapter(this, categoryList);

        recyclerViewProducts.setAdapter(productAdapter);
        recyclerViewCategories.setAdapter(categoryAdapter);

        loadCategories();
        loadProducts();
        setupNavigation();
        setupSearchBar();
    }

    private void loadCategories() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsDisplay.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });

        categoryAdapter.setOnItemClickListener((position, categoryName) -> filterProductsByCategory(categoryName));
    }

    private void loadProducts() {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    ProductS product = productSnapshot.getValue(ProductS.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsDisplay.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });

        productAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
            intent.putExtra("PRODUCT_ID", product.getProduct_id());
            intent.putExtra("PRODUCT_NAME", product.getProduct_name());
            intent.putExtra("PRODUCT_PRICE", product.getPrice());
            intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
            intent.putExtra("PRODUCT_IMAGE_URL", product.getImage_url());
            intent.putExtra("PRODUCT_QUANTITY", product.getStock_quantity());
            startActivity(intent);
        });
    }

    private void filterProductsByCategory(String categoryName) {

        productRef.orderByChild("category_name").equalTo(categoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            ProductS product = productSnapshot.getValue(ProductS.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProductsDisplay.this, "Failed to filter products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupNavigation() {
        findViewById(R.id.iv_home).setOnClickListener(v -> loadProducts());
        findViewById(R.id.iv_card).setOnClickListener(v -> startActivity(new Intent(this, CardActivity.class)));
        findViewById(R.id.iv_favourite).setOnClickListener(v -> startActivity(new Intent(this, FavouriteActivity.class)));
        findViewById(R.id.iv_setting).setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));
    }

    private void setupSearchBar() {
        searchBar.setSpeechMode(true);
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean b) {}

            @Override
            public void onSearchConfirmed(CharSequence text) {
                performSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_SPEECH) {
                    startVoiceRecognition();
                }
            }
        });
    }

    private void performSearch(String query) {
        productRef.orderByChild("product_name").startAt(query).endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            ProductS product = productSnapshot.getValue(ProductS.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProductsDisplay.this, "Search failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startVoiceRecognition() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search");
            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Voice search not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }
}


//package com.example.oneshop.ProductDisplay;
//
//import android.content.ActivityNotFoundException;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.speech.RecognizerIntent;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.oneshop.Adapter.CategoryAdapter;
//import com.example.oneshop.Adapter.ProductAdapter1;
//import com.example.oneshop.CardActivity;
//import com.example.oneshop.DatabaseHelper;
//import com.example.oneshop.FavouriteActivity;
//import com.example.oneshop.ProductDisplay.ProductDetailsActivity;
//import com.example.oneshop.R;
//import com.example.oneshop.SettingActivity;
//import com.mancj.materialsearchbar.MaterialSearchBar;
//import java.util.ArrayList;
//import java.util.Locale;
//
//public class ProductsDisplay extends AppCompatActivity {
//
//    private DatabaseHelper databaseHelper;
//    private Cursor cursor, cursorCatagory, cursorCataryView, cursorSearch;
//    private  MaterialSearchBar searchBar;
//    private static final String TAG = "ProductsDisplay";
//    private static final int VOICE_SEARCH_REQUEST_CODE = 100;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_products_display);
//
//        databaseHelper = new DatabaseHelper(this);
//        ImageView favourite, card, setting, home;
//        favourite = findViewById(R.id.iv_favourite);
//        card = findViewById(R.id.iv_card);
//        setting = findViewById(R.id.iv_setting);
//        home = findViewById(R.id.iv_home);
//        home.setEnabled(false);
//
//        searchBar = findViewById(R.id.search_bar);
//        searchBar.setSpeechMode(true);
//
//        home.setOnClickListener(v -> {home.setEnabled(false);Intent intent = new Intent(ProductsDisplay.this, ProductsDisplay.class);startActivity(intent);});
//        card.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, CardActivity.class);startActivity(intent);});
//        favourite.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, FavouriteActivity.class);startActivity(intent);});
//        setting.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, SettingActivity.class);startActivity(intent);});
//
//        try {
//            databaseHelper = new DatabaseHelper(this);
//            cursor = databaseHelper.getAllProducts();
//            cursorCatagory = databaseHelper.getAllCategories();
//        } catch (Exception e) {
//            Log.e(TAG, "Error initializing database or fetching products", e);
//        }
//
//        // for search bar
//        try {
//            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//                @Override
//                public void onSearchStateChanged(boolean enabled) {
//                    // Do something when the search bar state changes
//                }
//
//                @Override
//                public void onSearchConfirmed(CharSequence text) {
//                    // Retrieve text when search is confirmed
//                    String query = searchBar.getText();
//                    if(!query.isEmpty()){
//                        Toast.makeText(ProductsDisplay.this, "Search Text: " + query, Toast.LENGTH_SHORT).show();
//                        cursorSearch = databaseHelper.searchProductsOrCategories(query);
//                        if (cursorSearch != null && cursorSearch.getCount() > 0) {
//                            // Update RecyclerView with search results
//                            RecyclerView recyclerView = findViewById(R.id.recycler_view1);
//                            recyclerView.setLayoutManager(new GridLayoutManager(ProductsDisplay.this, 2));
//                            ProductAdapter1 searchAdapter = new ProductAdapter1(ProductsDisplay.this, cursorSearch);
//                            recyclerView.setAdapter(searchAdapter);
//
//                            // Handle product clicks from search results
//                            searchAdapter.setOnItemClickListener(position -> {
//                                if (cursorSearch.moveToPosition(position)) {
//                                    String productName = cursorSearch.getString(cursorSearch.getColumnIndexOrThrow("productName"));
//                                    Integer productId = cursorSearch.getInt(cursorSearch.getColumnIndexOrThrow("_id"));
//
//                                    // Start product detail activity
//                                    Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
//                                    intent.putExtra("PRODUCT_NAME", productName);
//                                    intent.putExtra("PRODUCT_ID", productId);
//                                    //Toast.makeText(ProductsDisplay.this, "Clicked= " + productId, Toast.LENGTH_SHORT).show();
//                                    startActivity(intent);
//                                }
//                            });
//                        } else {
//                            Toast.makeText(ProductsDisplay.this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                @Override
//                public void onButtonClicked(int buttonCode) {
//                    if (buttonCode == MaterialSearchBar.BUTTON_SPEECH) {
//                        startVoiceRecognition();
//                    }
//                }
//            });
//        }catch (Exception e) {
//            Log.e(TAG, "Error Search Display", e);
//        }
//
//        try {
//
//            // Catagory View and display
//
//            RecyclerView recyclerView_catagory = findViewById(R.id.recycler_view_catagory);
//            recyclerView_catagory.setLayoutManager(new GridLayoutManager(this, 4));
//            CategoryAdapter categoryAdapter = new CategoryAdapter(this, cursorCatagory);
//            recyclerView_catagory.setAdapter(categoryAdapter);
//
//            categoryAdapter.setOnItemClickListener(position -> {
//                if (cursorCatagory.moveToPosition(position)) {
//                   home.setEnabled(true);
//                   // Toast.makeText(this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
//
//                    String categoryName = cursorCatagory.getString(cursorCatagory.getColumnIndexOrThrow("categoryName"));
//                    cursorCataryView = databaseHelper.getProductsByCategory(categoryName);
//
//                    RecyclerView recyclerView = findViewById(R.id.recycler_view1);
//                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
//                    ProductAdapter1 adapter = new ProductAdapter1(this, cursorCataryView);
//                    recyclerView.setAdapter(adapter);
//
//                    // for product click
//                    adapter.setOnItemClickListener(position1 -> {
//                        if (cursorCataryView.moveToPosition(position1)) {
//                            String productName = cursorCataryView.getString(cursorCataryView.getColumnIndexOrThrow("productName"));
//                            int productId = cursorCataryView.getInt(cursorCataryView.getColumnIndexOrThrow("_id"));
//                            // Start product detail activity
//                            Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
//                            intent.putExtra("PRODUCT_NAME", productName);
//                            intent.putExtra("PRODUCT_ID", productId);
//                            //Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
//                            startActivity(intent);
//
//                        }
//                    });
//                }
//            });
//
//            //Products display
//            RecyclerView recyclerView = findViewById(R.id.recycler_view1);
//            recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
//            ProductAdapter1 adapter = new ProductAdapter1(this, cursor);
//            adapter.setOnItemClickListener(position -> {
//                if (cursor.moveToPosition(position)) {
//                    String productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"));
//
//                    // Start product detail activity
//                    Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
//                    intent.putExtra("PRODUCT_NAME", productName);
//                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
//                    intent.putExtra("PRODUCT_ID", productId);
//                    //Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
//                    startActivity(intent);
//
//                    //Toast.makeText(this, "Clicked: " + productName, Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            recyclerView.setAdapter(adapter);
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error setting up ProductDisplay", e);
//        }
//
//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
//        // Make the status bar icons light (for dark background)
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        // Close the cursor and database with error handling
//        try {
//            if (cursor != null && !cursor.isClosed()) {
//                cursor.close();
//            }
//            if (databaseHelper != null) {
//                databaseHelper.close();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error closing database or cursor", e);
//        }
//    }
//
//    private void startVoiceRecognition(){
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search");
//
//        try {
//            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "Voice search is not supported on your device", Toast.LENGTH_SHORT).show();
//        }
//    }
//    // Handle the result from voice recognition
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            if (results != null && !results.isEmpty()) {
//                String voiceQuery = results.get(0); // Get the top result
//                // set the voice text in the search bar
//                runOnUiThread(() -> {
//                    searchBar.setText(voiceQuery);
//                    searchBar.openSearch(); // Ensure the search bar opens
//                    searchBar.requestFocus(); // Ensure the search bar is focused
//                    // Manually trigger text change listener
//                    searchBar.getSearchEditText().setText(voiceQuery);
//                    searchBar.getSearchEditText().setSelection(voiceQuery.length());
//                });
//                performSearch(voiceQuery);
//            }
//        }
//    }
//    // Perform a search using the query
//    private void performSearch(String query) {
//        searchBar.setText(query);
//        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
//
//        // Implement search logic here (reuse the search logic for the text input)
//        Cursor cursorSearch = databaseHelper.searchProductsOrCategories(query);
//
//        if (cursorSearch != null && cursorSearch.getCount() > 0) {
//            // Update RecyclerView with search results
//            RecyclerView recyclerView = findViewById(R.id.recycler_view1);
//            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//            ProductAdapter1 searchAdapter = new ProductAdapter1(this, cursorSearch);
//            recyclerView.setAdapter(searchAdapter);
//
//            // Handle product clicks from search results
//            searchAdapter.setOnItemClickListener(position -> {
//                if (cursorSearch.moveToPosition(position)) {
//                    String productName = cursorSearch.getString(cursorSearch.getColumnIndexOrThrow("productName"));
//                    int productId = cursorSearch.getInt(cursorSearch.getColumnIndexOrThrow("_id"));
//                    // Start product detail activity
//                   // Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
//                    intent.putExtra("PRODUCT_NAME", productName);
//                    intent.putExtra("PRODUCT_ID", productId);
//                    //Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
//                    startActivity(intent);
//                }
//            });
//        } else {
//            Toast.makeText(this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//}
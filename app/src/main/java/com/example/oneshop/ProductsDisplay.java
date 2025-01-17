package com.example.oneshop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.Adapter.CategoryAdapter;
import com.example.oneshop.Adapter.ProductAdapter1;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Locale;

public class ProductsDisplay extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Cursor cursor, cursorCatagory, cursorCataryView, cursorSearch;

    private static final String TAG = "ProductsDisplay";
    private static final int VOICE_SEARCH_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_display);

        databaseHelper = new DatabaseHelper(this);
        ImageView favourite, card, setting, home;
        favourite = findViewById(R.id.iv_favourite);
        card = findViewById(R.id.iv_card);
        setting = findViewById(R.id.iv_setting);
        home = findViewById(R.id.iv_home);
        home.setEnabled(false);

        MaterialSearchBar searchBar = findViewById(R.id.search_bar);

        String SearchBar = searchBar.getText().trim();
        searchBar.setSpeechMode(true);

        home.setOnClickListener(v -> {home.setEnabled(false);Intent intent = new Intent(ProductsDisplay.this, ProductsDisplay.class);startActivity(intent);});
        card.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, CardActivity.class);startActivity(intent);});
        favourite.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, FavouriteActivity.class);startActivity(intent);});
        setting.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, SettingActivity.class);startActivity(intent);});

        try {
            databaseHelper = new DatabaseHelper(this);
            cursor = databaseHelper.getAllProducts();
            cursorCatagory = databaseHelper.getAllCategories();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database or fetching products", e);
        }

        // for search bar
        try {
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                    // Do something when the search bar state changes
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    // Retrieve text when search is confirmed
                    String query = searchBar.getText();
                    if(!query.isEmpty()){
                        Toast.makeText(ProductsDisplay.this, "Search Text: " + query, Toast.LENGTH_SHORT).show();
                        cursorSearch = databaseHelper.searchProductsOrCategories(query);
                        if (cursorSearch != null && cursorSearch.getCount() > 0) {
                            // Update RecyclerView with search results
                            RecyclerView recyclerView = findViewById(R.id.recycler_view1);
                            recyclerView.setLayoutManager(new GridLayoutManager(ProductsDisplay.this, 2));
                            ProductAdapter1 searchAdapter = new ProductAdapter1(ProductsDisplay.this, cursorSearch);
                            recyclerView.setAdapter(searchAdapter);

                            // Handle product clicks from search results
                            searchAdapter.setOnItemClickListener(position -> {
                                if (cursorSearch.moveToPosition(position)) {
                                    String productName = cursorSearch.getString(cursorSearch.getColumnIndexOrThrow("productName"));
                                    Integer productId = cursorSearch.getInt(cursorSearch.getColumnIndexOrThrow("_id"));

                                    // Start product detail activity
                                    Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
                                    intent.putExtra("PRODUCT_NAME", productName);
                                    intent.putExtra("PRODUCT_ID", productId);
                                    //Toast.makeText(ProductsDisplay.this, "Clicked= " + productId, Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(ProductsDisplay.this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
                        }

                    }


                }

                @Override
                public void onButtonClicked(int buttonCode) {
                    if (buttonCode == MaterialSearchBar.BUTTON_SPEECH) {
                        startVoiceRecognition();
                    }
                }
            });
        }catch (Exception e) {
            Log.e(TAG, "Error Search Display", e);
        }

        try {

            // Catagory View and display
            
            RecyclerView recyclerView_catagory = findViewById(R.id.recycler_view_catagory);
            recyclerView_catagory.setLayoutManager(new GridLayoutManager(this, 4));
            CategoryAdapter categoryAdapter = new CategoryAdapter(this, cursorCatagory);
            recyclerView_catagory.setAdapter(categoryAdapter);

            categoryAdapter.setOnItemClickListener(position -> {
                if (cursorCatagory.moveToPosition(position)) {
                   home.setEnabled(true);
                   // Toast.makeText(this, "Clicked: " + position, Toast.LENGTH_SHORT).show();

                    String categoryName = cursorCatagory.getString(cursorCatagory.getColumnIndexOrThrow("categoryName"));
                    cursorCataryView = databaseHelper.getProductsByCategory(categoryName);

                    RecyclerView recyclerView = findViewById(R.id.recycler_view1);
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
                    ProductAdapter1 adapter = new ProductAdapter1(this, cursorCataryView);
                    recyclerView.setAdapter(adapter);

                    // for product click
                    adapter.setOnItemClickListener(position1 -> {
                        if (cursorCataryView.moveToPosition(position1)) {
                            String productName = cursorCataryView.getString(cursorCataryView.getColumnIndexOrThrow("productName"));
                            int productId = cursorCataryView.getInt(cursorCataryView.getColumnIndexOrThrow("_id"));
                            // Start product detail activity
                            Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
                            intent.putExtra("PRODUCT_NAME", productName);
                            intent.putExtra("PRODUCT_ID", productId);
                            //Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            
                        }
                    });
                }
            });


            //Products display
            RecyclerView recyclerView = findViewById(R.id.recycler_view1);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
            ProductAdapter1 adapter = new ProductAdapter1(this, cursor);
            adapter.setOnItemClickListener(position -> {
                if (cursor.moveToPosition(position)) {
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"));


                    // Start product detail activity
                    Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_NAME", productName);
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    intent.putExtra("PRODUCT_ID", productId);
                    //Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                    //Toast.makeText(this, "Clicked: " + productName, Toast.LENGTH_SHORT).show();
                }
            });


            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, "Error setting up ProductDisplay", e);
        }

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        // Make the status bar icons light (for dark background)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the cursor and database with error handling
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (databaseHelper != null) {
                databaseHelper.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing database or cursor", e);
        }
    }

    private void startVoiceRecognition(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search");

        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Voice search is not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }
    // Handle the result from voice recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String voiceQuery = results.get(0); // Get the top result
                performSearch(voiceQuery);
            }
        }
    }
    // Perform a search using the query
    private void performSearch(String query) {
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();

        // Implement search logic here (reuse the search logic for the text input)
        Cursor cursorSearch = databaseHelper.searchProductsOrCategories(query);

        if (cursorSearch != null && cursorSearch.getCount() > 0) {
            // Update RecyclerView with search results
            RecyclerView recyclerView = findViewById(R.id.recycler_view1);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            ProductAdapter1 searchAdapter = new ProductAdapter1(this, cursorSearch);
            recyclerView.setAdapter(searchAdapter);

            // Handle product clicks from search results
            searchAdapter.setOnItemClickListener(position -> {
                if (cursorSearch.moveToPosition(position)) {
                    String productName = cursorSearch.getString(cursorSearch.getColumnIndexOrThrow("productName"));
                    int productId = cursorSearch.getInt(cursorSearch.getColumnIndexOrThrow("_id"));
                    // Start product detail activity
                   // Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_NAME", productName);
                    intent.putExtra("PRODUCT_ID", productId);
                    //Toast.makeText(this, "Clicked: " + productId, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
        }
    }



}
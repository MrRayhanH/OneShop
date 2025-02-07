package com.example.oneshop.User.Card;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.User.Favourite.FavouriteActivity;
import com.example.oneshop.OrderClass.OrderItem;
import com.example.oneshop.User.ProductDisplay.ProductsDisplay;
import com.example.oneshop.ProductsClass.ProductS;
import com.example.oneshop.R;
import com.example.oneshop.User.Setting.SettingActivity;
import com.example.oneshop.User.Order.OrderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CardActivity extends AppCompatActivity implements CardAdapter.OnItemClickListener {

    private TextView priceTextView, totalProductTextView;
    private double totalPrice = 0;
    private int totalProducts = 0;
    private FirebaseDatabase database;
    private DatabaseReference cartRef;
    private List<ProductS> productList;
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        priceTextView = findViewById(R.id.tv_total_price);
        totalProductTextView = findViewById(R.id.tv_total_product);
        Button paymentButton = findViewById(R.id.btn_payment);

        // Firebase Database Reference Initialization
        database = FirebaseDatabase.getInstance();
        cartRef = database.getReference("Cart");

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Grid layout for RecyclerView
        productList = new ArrayList<>();
        cardAdapter = new CardAdapter(this, productList, this);
        recyclerView.setAdapter(cardAdapter);

        paymentButton.setOnClickListener(v -> {
            createOrder();
            Intent intent = new Intent(CardActivity.this, OrderActivity.class);
            startActivity(intent);
            Toast.makeText(CardActivity.this, "Order Created and Payment Done", Toast.LENGTH_SHORT).show();
        });

        setupNavigation();
        loadCartItem();
        statusBar();

    }

    private void loadCartItem() {
        updateTotals();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            loadCartItems(userId);
        } else {
            Toast.makeText(this, "Please log in to view your cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCartItems(String userId) {
        cartRef.orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                totalPrice = 0;
                totalProducts = 0;

                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    String productId = cartSnapshot.child("product_id").getValue(String.class);
                    if (productId != null) {
                        fetchProductDetails(productId, cartSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductDetails(String productId, DataSnapshot cartSnapshot) {
        DatabaseReference productRef = database.getReference("products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductS product = snapshot.getValue(ProductS.class);
                if (product != null) {
                    productList.add(product);

                    int quantity = cartSnapshot.child("quantity").getValue(Integer.class);
                    if (quantity != 0) {
                        product.setQuantity(quantity);  // Set quantity
                        totalProducts += quantity;
                        totalPrice += product.getPrice() * quantity; // Update total price with the new quantity
                    }

                    cardAdapter.notifyDataSetChanged();
                    updateTotals();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardActivity.this, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotals() {
        // Recalculate the total price and total quantity
        double updatedTotalPrice = 0;
        int updatedTotalProducts = 0;

        for (ProductS product : productList) {
            updatedTotalProducts += product.getQuantity();  // Sum up quantities
            updatedTotalPrice += product.getPrice() * product.getQuantity();  // Sum up total price for all items
        }

        totalPrice = updatedTotalPrice;
        totalProducts = updatedTotalProducts;

        priceTextView.setText(String.format("Total BDT : %.2f", totalPrice));
        totalProductTextView.setText("Total Products : " + totalProducts);
    }
    private void createOrder() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(CardActivity.this, "Please log in to place an order", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        for (ProductS product : productList) {
            String orderId = ordersRef.push().getKey(); // Generate unique order ID for each product

            if (orderId != null) {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("orderId", orderId);
                orderDetails.put("totalPrice", product.getPrice() * product.getQuantity());
                orderDetails.put("totalProduct", product.getQuantity());
                orderDetails.put("productId", product.getProduct_id());
                orderDetails.put("userId", userId);
                orderDetails.put("sellerId", product.getSeller_user_id());
                orderDetails.put("orderStatus", "Pending");
                orderDetails.put("orderDate", getCurrentTimestamp());
                orderDetails.put("image_url", product.getImage_public_id());
                orderDetails.put("product_name", product.getProduct_name());
                ordersRef.child(orderId).setValue(orderDetails)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(CardActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(CardActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
    }
//    private void createOrder() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        String userId = currentUser.getUid();
//        String orderId = FirebaseDatabase.getInstance().getReference("Orders").push().getKey();
//
//        Map<String, List<ProductS>> productsGroupedBySeller = groupProductsBySeller();
//
//        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
//        Map<String, Object> orderDetails = new HashMap<>();
//        orderDetails.put("user_id", userId);
//        orderDetails.put("total_price", totalPrice);  // Use the correctly calculated totalPrice
//        orderDetails.put("total_quantity", totalProducts);  // Use the correctly calculated totalQuantity
//        orderDetails.put("order_status", "Pending");
//        orderDetails.put("order_id", orderId);
//        ordersRef.setValue(orderDetails);
//
//        // Create sub-orders for each seller
//        for (Map.Entry<String, List<ProductS>> entry : productsGroupedBySeller.entrySet()) {
//            String sellerId = entry.getKey();
//            List<ProductS> sellerProducts = entry.getValue();
//
//            String sellerSubOrderId = ordersRef.child("sub_orders").push().getKey();
//            DatabaseReference sellerSubOrderRef = ordersRef.child("sub_orders").child(sellerSubOrderId);
//
//            Map<String, Object> sellerOrderDetails = new HashMap<>();
//            sellerOrderDetails.put("seller_id", sellerId);
//            sellerOrderDetails.put("products", sellerProducts);
//            sellerOrderDetails.put("total_price", calculateTotalPriceForSeller(sellerProducts));
//            sellerOrderDetails.put("order_status", "Pending");
//            sellerSubOrderRef.setValue(sellerOrderDetails);
//
//            // Create OrderItems for each product and save to Order_Items
//            for (ProductS product : sellerProducts) {
//                OrderItem orderItem = new OrderItem(orderId, product.getProduct_id(), sellerId, product.getPrice(), product.getQuantity());
//                DatabaseReference orderItemsRef = FirebaseDatabase.getInstance().getReference("Order_Items");
//                orderItemsRef.push().setValue(orderItem);
//            }
//        }
//    }

    private Map<String, List<ProductS>> groupProductsBySeller() {
        Map<String, List<ProductS>> groupedProducts = new HashMap<>();

        for (ProductS product : productList) {
            String sellerId = product.getSeller_user_id();
            if (!groupedProducts.containsKey(sellerId)) {
                groupedProducts.put(sellerId, new ArrayList<>());
            }
            groupedProducts.get(sellerId).add(product);
        }
        return groupedProducts;
    }

    private double calculateTotalPriceForSeller(List<ProductS> sellerProducts) {
        double total = 0;
        for (ProductS product : sellerProducts) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    @Override
    public void onItemClick(ProductS product) {
        // Handle item click if needed
    }

    @Override
    public void onQuantityUpdated() {
        loadCartItem();  // Recalculate totals when quantity changes
    }

    private void setupNavigation() {
        findViewById(R.id.iv_home).setOnClickListener(v -> startActivity(new Intent(CardActivity.this, ProductsDisplay.class)));
        findViewById(R.id.iv_favourite).setOnClickListener(v -> startActivity(new Intent(CardActivity.this, FavouriteActivity.class)));
        findViewById(R.id.iv_setting).setOnClickListener(v -> startActivity(new Intent(CardActivity.this, SettingActivity.class)));
    }
    private void statusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        }

        // Make the status bar icons light (for dark background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}

package com.example.oneshop.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oneshop.CardActivity;
import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.R;

public class CartAdapter extends CursorAdapter {

    public CartAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.cart_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.tv_product_name);
        TextView priceTextView = view.findViewById(R.id.tv_product_price);
        TextView quantityTextView = view.findViewById(R.id.tv_product_quantity);
        ImageView productImageView = view.findViewById(R.id.iv_product_image);

        ImageView addProduct = view.findViewById(R.id.iv_add);
        ImageView minusProduct = view.findViewById(R.id.iv_minas);
        ImageView deleteProduct = view.findViewById(R.id.iv_cartDelete);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME_CARD));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE_CARD));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_QUANTITY_CARD));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE_URI_CARD));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));

        // Set text and image
        nameTextView.setText(name);
        priceTextView.setText(String.valueOf("BDT: "+price));
        quantityTextView.setText(String.valueOf("Quantity: "+quantity));
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        productImageView.setImageBitmap(bitmap);


        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            // Increase quantity
            addProduct.setOnClickListener(v -> {

                databaseHelper.updateCartQuantity(id, quantity + 1); // Increment quantity
                Cursor updatedCursor = databaseHelper.getAllCartItems();
                changeCursor(updatedCursor);
                // Notify the activity to refresh totals
                if (context instanceof CardActivity) {
                    ((CardActivity) context).refreshTotals();
                }
            });

            // Decrease quantity
            minusProduct.setOnClickListener(v -> {

                if (quantity > 1) { // Prevent negative quantity
                    databaseHelper.updateCartQuantity(id, quantity - 1);
                    Cursor updatedCursor = databaseHelper.getAllCartItems();
                    changeCursor(updatedCursor);
                } else {
                    Toast.makeText(context, "Quantity can't be less than 1", Toast.LENGTH_SHORT).show();
                }
                // Notify the activity to refresh totals
                if (context instanceof CardActivity) {
                    ((CardActivity) context).refreshTotals();
                }
            });

            deleteProduct.setOnClickListener(v -> {

                Toast.makeText(context, "Item Deleted!", Toast.LENGTH_SHORT).show();
                databaseHelper.removeProductFromCart(id);
                Cursor updatedCursor = databaseHelper.getAllCartItems();
                changeCursor(updatedCursor);
                // Notify the activity to refresh totals
                if (context instanceof CardActivity) {
                    ((CardActivity) context).refreshTotals();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

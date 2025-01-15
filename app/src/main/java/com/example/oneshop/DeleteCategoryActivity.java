package com.example.oneshop;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.R;

public class DeleteCategoryActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_category);

        EditText et_CategoryName = findViewById(R.id.et_category_name);
        Button btnSearch = findViewById(R.id.btn_search);
        Button btnDeleteCategory = findViewById(R.id.btn_delete_category);
        ImageView iv_category = findViewById(R.id.iv_category);
        TextView tv_catagory_name = findViewById(R.id.tv_catagory_name);
        ImageView backArrow = findViewById(R.id.deleteCatagoryBackArrow);

        databaseHelper = new DatabaseHelper(this);

        backArrow.setOnClickListener(v -> finish());

        // Search for category when search button is clicked
        btnSearch.setOnClickListener(v -> {
            String categoryName = et_CategoryName.getText().toString().trim();
            cursor = databaseHelper.getCategoryByName(categoryName);

            if (cursor != null && cursor.moveToFirst()) {

                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY_NAME));
                if(name != null){tv_catagory_name.setText(" Catagory Found \n Catagory Name : "+name);}
                else {tv_catagory_name.setText("Catagory Not Found");}

                byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY_IMAGE_URI));
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
                iv_category.setImageBitmap(bitmap);

                btnDeleteCategory.setEnabled(true); // Enable delete button
            } else {
                Toast.makeText(DeleteCategoryActivity.this, "Category not found", Toast.LENGTH_SHORT).show();
                btnDeleteCategory.setEnabled(false);
            }
        });

        // Delete the category when delete button is clicked
        btnDeleteCategory.setOnClickListener(v -> {
            String categoryName = et_CategoryName.getText().toString().trim();
            boolean isDeleted = databaseHelper.deleteCategory(categoryName);

            if (isDeleted) {Toast.makeText(DeleteCategoryActivity.this, "Category deleted successfully", Toast.LENGTH_SHORT).show();}
            else {Toast.makeText(DeleteCategoryActivity.this, "Failed to delete category", Toast.LENGTH_SHORT).show();}

        });

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

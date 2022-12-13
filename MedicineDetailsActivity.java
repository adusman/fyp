package com.example.emedcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.emedcare.adapters.CartProductsAdapter;
import com.example.emedcare.sqlLite.GroceryContract;
import com.example.emedcare.sqlLite.GroceryDBHelper;

public class MedicineDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView productImage;
    private TextView productNameTextView, productDescTextView, productPriceTextView, productQuantityTextView;
    private TextView ratingValue;
    private TextView minusItem, plusItem;
    private String productName, productDesc;
    private String imagePath;
    private RatingBar ratingBar;
    private Integer /*productId,*/ productQuantity;
    private Integer productId;
    private float productRating, productPrice;
    private Button addToCartBtn;
    private SQLiteDatabase database;

    private float proPrice = 0, tempPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);

        setUpToolbar();
        GroceryDBHelper dbHelper = new GroceryDBHelper(this);
        database = dbHelper.getWritableDatabase();

        initViews();
        getParentActivityData();
        setUpProductQuantity();
        //setUpRatingBar();

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToDb();
            }
        });
    }

    private void addProductToDb(){
        if (proPrice != 0) {
            productPrice = proPrice;
        }
        if (productQuantity <= 0) {
            Toast.makeText(this,"Please select quantity", Toast.LENGTH_SHORT).show();
            return;

        } else {

            ContentValues contentValues = new ContentValues();
            contentValues.put(GroceryContract.CartEntry.COLUMN_PRODUCT_ID, productId);
            contentValues.put(GroceryContract.CartEntry.COLUMN_PRODUCT_NAME, productName);
            contentValues.put(GroceryContract.CartEntry.COLUMN_PRODUCT_IMG_PATH, imagePath);
            contentValues.put(GroceryContract.CartEntry.COLUMN_PRODUCT_PRICE, productPrice);
            contentValues.put(GroceryContract.CartEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

            database.insert(GroceryContract.CartEntry.TABLE_NAME, null, contentValues);
            CartProductsAdapter adapter = new CartProductsAdapter();
            adapter.swapCursor(getAllProducts());
            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
        }
        /*// accessing getAllProducts() function from CartFragment
        cartFragment = new CartFragment();
        adapter.swapCursor(cartFragment.getAllProducts());*/

    }

    public Cursor getAllProducts() {
        return database.query(
                GroceryContract.CartEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                GroceryContract.CartEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }

    private void setUpProductQuantity() {
        minusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantity > 1) {
                    productQuantity = productQuantity -1;
                    productQuantityTextView.setText(String.valueOf(productQuantity));
                    tempPrice = productPrice;
                    proPrice = tempPrice * productQuantity;
                    productPriceTextView.setText("Rs."+ String.valueOf(proPrice));
                }
                Log.d("ProductQuantity: ", String.valueOf(productQuantity));
            }
        });

        plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productQuantity = productQuantity + 1;
                productQuantityTextView.setText(String.valueOf(productQuantity));
                tempPrice = productPrice;
                proPrice = tempPrice * productQuantity;
                productPriceTextView.setText("Rs."+ String.valueOf(proPrice));
                Log.d("ProductQuantity: ", String.valueOf(productQuantity));
            }
        });

    }

    private void getParentActivityData() {
        Intent intent = getIntent();

        if (intent != null) {
            productId = intent.getIntExtra("productId", 0);
            productName = intent.getStringExtra("productName");
            productDesc = intent.getStringExtra("productDesc");
            imagePath = intent.getStringExtra("productImagePath");
            productPrice = Float.parseFloat(intent.getStringExtra("productPrice"));
            productQuantity = intent.getIntExtra("productQuantity", 1);
        }

        /*Picasso.get().load(new StringBuilder("https://raftmart.pk/")
                .append(imagePath).toString())
                .error(R.mipmap.ic_launcher)
                .into(productImage);*/

        Glide.with(this).load(imagePath).error(R.mipmap.ic_launcher).into(productImage);

        productNameTextView.setText(productName);
        productDescTextView.setText(productDesc);
        productPriceTextView.setText("Rs." + productPrice);
        productQuantityTextView.setText(String.valueOf(productQuantity));
    }

    private void initViews() {
        productImage = findViewById(R.id.product_image);
        productNameTextView = findViewById(R.id.product_name);
        productDescTextView = findViewById(R.id.product_desc);
        productPriceTextView = findViewById(R.id.product_price);
        productQuantityTextView = findViewById(R.id.product_quantity);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);

        minusItem = findViewById(R.id.minus_item);
        plusItem = findViewById(R.id.plus_item);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Medicine");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
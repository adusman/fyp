package com.example.emedcare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emedcare.adapters.CartProductsAdapter;
import com.example.emedcare.data.medicine.MedicineDatum;
import com.example.emedcare.interfaces.RecyclerViewClickInterface;
import com.example.emedcare.sqlLite.GroceryContract;
import com.example.emedcare.sqlLite.GroceryDBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class CartActivity extends AppCompatActivity implements RecyclerViewClickInterface {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private Toolbar toolbar;
    private GroceryDBHelper groceryDBHelper;
    private SQLiteDatabase database;
    private ArrayList<MedicineDatum> productModelArrayList;
    private RecyclerView cartRecyclerView;
    private CartProductsAdapter cartAdapter;
    Button stipe;


    private TextView subtotalAmountTextView;
    private float subtotalAmount;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setUpToolbar();

        // Initialize Firebase Auth and User
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        stipe=findViewById(R.id.checkout_btnn);
        stipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(CartActivity.this,stripe.class);
                startActivity(i);
            }
        });

        groceryDBHelper = new GroceryDBHelper(this);
        database = groceryDBHelper.getReadableDatabase();

        productModelArrayList = new ArrayList<>();
        initCartRecyclerView();

        // subtotal amount initialization
        subtotalAmountTextView = findViewById(R.id.cart_subtotal_amount);
        calculateCartSubtotalAmount();
        subtotalAmountTextView.setText("Rs." + String.valueOf(subtotalAmount));

        checkOut();
    }

    private void initCartRecyclerView() {
        cartRecyclerView = findViewById(R.id.cart_recyclerview);
        cartRecyclerView.setHasFixedSize(true);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartProductsAdapter(this, getAllProducts(),this);
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void checkOut() {
        Button checkOutBtn = findViewById(R.id.checkout_btn);
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groceryDBHelper.isCartEmpty()) {
                    Toast.makeText(CartActivity.this, "Add products to the cart first", Toast.LENGTH_SHORT).show();
                } else if (mUser != null) {// if the user is logged in
                    startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
                } else {
                    Intent intent = new Intent(CartActivity.this, RegistrationActivity.class);
                    saveDataInSharedPrefs(CartActivity.this,"fromShoppingCart", true);

                    //going to checkout after successful registration of user

                    startActivity(intent);
                }
            }
        });
    }

    public static void saveDataInSharedPrefs(Context context, String variable, Boolean data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(variable, data).apply();
    }

    @SuppressLint("Range")
    private void calculateCartSubtotalAmount() {

        float subtotal = 0.0f;
        SQLiteDatabase database1 = null;
        try {
            Cursor cursor = database.query(
                    GroceryContract.CartEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                subtotal = subtotal + cursor.getFloat(cursor.getColumnIndex(GroceryContract.CartEntry.COLUMN_PRODUCT_PRICE));
                subtotalAmount = subtotal;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getting subtotal amount" + e.toString());
        }

    }

    private void removeProduct(Long id) {
        database.delete(GroceryContract.CartEntry.TABLE_NAME,
                GroceryContract.CartEntry._ID + "=" + id, null);
        cartAdapter.swapCursor(getAllProducts());
        Log.i(TAG, "CartProductDeleteId: " + id);
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

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cart");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // when we click on the cart item
        // not implemented yet...
    }

    @Override
    public void onViewClick(int position, String pId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete this product?");
        // Add the buttons
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // remove product from database
                removeProduct(Long.parseLong(pId));
                // remove product from recyclerview
                //productModelArrayList.remove(position);
                //cartAdapter.notifyItemRemoved(position);
                Toast.makeText(CartActivity.this, "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
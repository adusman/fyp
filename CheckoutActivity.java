package com.example.emedcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.emedcare.adapters.CheckoutAdapter;
import com.example.emedcare.api.CareServiceApi;
import com.example.emedcare.data.checkout.CheckOutData;
import com.example.emedcare.data.medicine.MedicineDatum;
import com.example.emedcare.sqlLite.GroceryContract;
import com.example.emedcare.sqlLite.GroceryDBHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.ContentValues.TAG;

public class CheckoutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout firstNameText;
    private TextInputLayout lastNameText;
    private TextInputLayout cityNameText;
    private TextInputLayout homeAddressText;
    private TextInputLayout phoneText;
    private TextInputLayout provinceText;

    private CheckBox checkBox;
    private TextView privacyPolicyText, termsAndCondText;
    private TextView subtotalAmountTextView, totalAmountTextView, shippingAmountTextView, countryNameText;
    private float subtotalAmount, totalAmount, shippingAmount = 200.0f;
    private Button placeOrderBtn;

    private GroceryDBHelper groceryDBHelper;
    private SQLiteDatabase database;
    private List<MedicineDatum> productModelArrayList;
    private CheckoutAdapter checkoutAdapter;
    private RecyclerView checkoutRecyclerView;

    private CareServiceApi careServiceApi;
    private Integer pharmacy_id = 1;
    private Integer customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        setUpToolbar();
        initViews();

        groceryDBHelper = new GroceryDBHelper(this);
        database = groceryDBHelper.getReadableDatabase();

        productModelArrayList = new ArrayList<>();
        getAllProducts();

        initCheckoutRecyclerView();
        calculateCartSubtotalAmount();
        calculateTotalAmount();
        setUpPrivacyPolicyAndTerms();

        initRetrofit();


        Intent intent = getIntent();
        if (intent != null) {
            customer_id = intent.getIntExtra("customer_id", 0);
            String firstName = intent.getStringExtra("first_name");
            String lastName = intent.getStringExtra("last_name");

            firstNameText.getEditText().setText(firstName);
            lastNameText.getEditText().setText(lastName);
        }

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkNotAvailable()) {
                    Toast.makeText(getApplicationContext(), "Internet not available.Please connect.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmInput()) {

                    if (checkBox.isChecked()) {
                        postCheckOutData();
                        //addTransactionToHistory();

//                        clearCart();
                        Toast.makeText(CheckoutActivity.this, "Placing order...", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(CheckoutActivity.this, OrderConfirmActivity.class);
//                        startActivity(intent);
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Accept terms and conditions first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://emidcare.levelupsolutions.tech/api/v1/")
                .addConverterFactory(ScalarsConverterFactory.create()) //important
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        careServiceApi = retrofit.create(CareServiceApi.class);
    }


    private void clearCart() {
        if (groceryDBHelper.clearCart()) {
            Log.i(TAG, "Cart Cleared");
        } else {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkNotAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting();
    }

    private void postCheckOutData() {
        String fName = firstNameText.getEditText().getText().toString();
        String lName = lastNameText.getEditText().getText().toString();
        String country = "Pakistan";
        String paymentMethod = "Cash on delivery";
        String province = provinceText.getEditText().getText().toString();
        String city = cityNameText.getEditText().getText().toString();
        String phone = phoneText.getEditText().getText().toString();
        String address = homeAddressText.getEditText().getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        customer_id = sharedPreferences.getInt("id", 0);

        Log.i(TAG, "postCheckOutData: " + customer_id);


        List<HashMap<String, Integer>> list = new ArrayList<>();
        for (MedicineDatum item : productModelArrayList) {
            HashMap<String, Integer> hashMap = new HashMap<>();
            hashMap.put("id", item.getId());
            hashMap.put("quantity", item.getProductQuantity());
            list.add(hashMap);
        }


        Order order = new Order();
        order.address = address;
        order.city = city;
        order.customer_id = customer_id;
        order.pharmacy_id = pharmacy_id;
        order.phone = phone;
        order.province = province;
        order.medicines = list;

        Gson gson = new Gson();
        // Java object to JSON string
        String jsonInString = gson.toJson(order);
        Log.d(TAG, "postCheckOutData: jsonString: " + jsonInString);

        Call<CheckOutData> call = careServiceApi.placeOrder(jsonInString);
        call.enqueue(new Callback<CheckOutData>() {
            @Override
            public void onResponse(Call<CheckOutData> call, Response<CheckOutData> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CheckoutActivity.this, "Response failed!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "ApiResponse Code: " + response.code());
                    Log.d(TAG, "ApiResponse Message: " + response.message());
                } else {

                    clearCart();
                    /*CheckOutData checkOutData = response.body();
                    Integer orderId = checkOutData.getOrderId();
                    String responseMessage = checkOutData.getResponseMessage();*/


                    Intent intent = new Intent(CheckoutActivity.this, OrderConfirmActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<CheckOutData> call, Throwable t) {
                Log.d("OnResponseFailure: ", t.getMessage());
            }
        });

    }

    private void calculateTotalAmount() {
        totalAmountTextView = findViewById(R.id.checkout_total_amount);
        shippingAmountTextView = findViewById(R.id.checkout_shipping_charges_amount);
        totalAmount = subtotalAmount + shippingAmount;
        shippingAmountTextView.setText("Rs." + shippingAmount);
        totalAmountTextView.setText("Rs." + totalAmount);
    }

    private void calculateCartSubtotalAmount() {

        Float subtotal = 0.0f;
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

        // subtotal amount initialization
        subtotalAmountTextView = findViewById(R.id.checkout_subtotal_amount);
        subtotalAmountTextView.setText("Rs." + String.valueOf(subtotalAmount));

    }

    public void getAllProducts() {
        Cursor cursor = database.query(
                GroceryContract.CartEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                GroceryContract.CartEntry.COLUMN_TIMESTAMP + " DESC"
        );

        Log.i(TAG, "Products in Checkout: " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                MedicineDatum productModel = new MedicineDatum();
                productModel.setId(cursor.getInt(cursor.getColumnIndex(GroceryContract.CartEntry.COLUMN_PRODUCT_ID)));
                productModel.setImageUrl(cursor.getString(cursor.getColumnIndex(GroceryContract.CartEntry.COLUMN_PRODUCT_IMG_PATH)));
                productModel.setName(cursor.getString(cursor.getColumnIndex(GroceryContract.CartEntry.COLUMN_PRODUCT_NAME)));
                productModel.setPrice(String.valueOf(cursor.getFloat(cursor.getColumnIndex(GroceryContract.CartEntry.COLUMN_PRODUCT_PRICE))));
                productModel.setProductQuantity(cursor.getInt(cursor.getColumnIndex(GroceryContract.CartEntry.COLUMN_PRODUCT_QUANTITY)));
                productModelArrayList.add(productModel);
            }
        } finally {
            cursor.close();
        }


    }

    private void initCheckoutRecyclerView() {
        checkoutRecyclerView = findViewById(R.id.checkout_order_recyclerview);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutAdapter(this, productModelArrayList);
        checkoutRecyclerView.setAdapter(checkoutAdapter);

    }

    private boolean confirmInput() {
        if (/*!validateFirstName() | !validateLastName() |*/ !validateCityName() | !validateHomeAddress() | !validatePhone() | !validateProvince()) {
            Toast.makeText(this, "Fill data", Toast.LENGTH_SHORT).show();
            return false;
        }

        /*String input = "First name: "+ firstNameText.getEditText().getText().toString();
        input += "\n";
        input += "Last name: "+ lastNameText.getEditText().getText().toString();
        Toast.makeText(this, "Data found", Toast.LENGTH_SHORT).show();*/
        return true;
    }

    private boolean validateProvince() {
        String provinceInput = provinceText.getEditText().getText().toString().trim();

        if (provinceInput.isEmpty()) {
            provinceText.setError("Field can't be empty");
            return false;
        } else {
            provinceText.setError(null);
            return true;
        }
    }

    private boolean validatePhone() {
        String phoneInput = phoneText.getEditText().getText().toString().trim();

        if (phoneInput.isEmpty()) {
            phoneText.setError("Field can't be empty");
            return false;
        } else {
            phoneText.setError(null);
            return true;
        }
    }

    private boolean validateHomeAddress() {
        String homeAddressInput = homeAddressText.getEditText().getText().toString().trim();

        if (homeAddressInput.isEmpty()) {
            homeAddressText.setError("Field can't be empty");
            return false;
        } else {
            homeAddressText.setError(null);
            return true;
        }
    }

    private boolean validateCityName() {
        String cityNameInput = cityNameText.getEditText().getText().toString().trim();

        if (cityNameInput.isEmpty()) {
            cityNameText.setError("Field can't be empty");
            return false;
        } else {
            cityNameText.setError(null);
            return true;
        }
    }

    private boolean validateLastName() {
        String lastNameInput = lastNameText.getEditText().getText().toString().trim();

        if (lastNameInput.isEmpty()) {
            lastNameText.setError("Field can't be empty");
            return false;
        } else {
            lastNameText.setError(null);
            return true;
        }
    }

    private boolean validateFirstName() {
        String firstNameInput = firstNameText.getEditText().getText().toString().trim();

        if (firstNameInput.isEmpty()) {
            firstNameText.setError("Field can't be empty");
            return false;
        } else {
            firstNameText.setError(null);
            return true;
        }
    }

    private void initViews() {
        firstNameText = findViewById(R.id.checkout_first_name);
        lastNameText = findViewById(R.id.checkout_last_name);
        cityNameText = findViewById(R.id.checkout_city);
        homeAddressText = findViewById(R.id.checkout_address);
        phoneText = findViewById(R.id.checkout_phone);
        provinceText = findViewById(R.id.checkout_province);
        countryNameText = findViewById(R.id.checkout_country);
        checkBox = findViewById(R.id.checkbox);
        privacyPolicyText = findViewById(R.id.privacy_policy_txt);
        termsAndCondText = findViewById(R.id.terms_and_conditions);
        placeOrderBtn = findViewById(R.id.place_order_btn);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Check Out");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpPrivacyPolicyAndTerms() {

        privacyPolicyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("https://www.google.com/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                }
            }
        });

        termsAndCondText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("https://www.yahoo.com/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                }
            }
        });

    }
}
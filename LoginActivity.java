package com.example.emedcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.emedcare.api.CareServiceApi;
import com.example.emedcare.data.registration.SignInData;
import com.example.emedcare.data.registration.SignInModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private TextInputEditText editText_email, editText_password;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private CareServiceApi careServiceApi;
    private boolean fromShoppingCart = false;
    int customer_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        initRetrofit();

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        findViewById(R.id.tv_new_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
    }

    private void loginUserAccount() {
        String email, password;
        if (TextUtils.isEmpty(editText_email.getText().toString())) {
            textInputLayoutEmail.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(editText_password.getText().toString())) {
            textInputLayoutPassword.setError("Password required");
            return;
        }

        email = editText_email.getText().toString();
        password = editText_password.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            signInUserToLocalApi(email, password);
                            fromShoppingCart = getDataFromSharedPrefs(LoginActivity.this, "fromShoppingCart", false);

                            Intent intent;
                            if (fromShoppingCart) {
                                intent = new Intent(LoginActivity.this, CheckoutActivity.class);
                                intent.putExtra("customer_id", customer_id);
                            } else {
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void signInUserToLocalApi(String email, String password) {

        AndroidNetworking.post("https://emidcare.levelupsolutions.tech/api/v1/login")
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        try {
                            customer_id = response.getJSONObject("data").getInt("id");
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            sharedPreferences.edit().putInt("id", customer_id).apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i(TAG, "onResponse: " + response.toString());

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });

    /*    SignInData data = new SignInData();
        data.setEmail(email);
        data.setPassword(password);

        SignInModel model = new SignInModel();
        model.setSignInData(data);

        Gson gson = new Gson();
        // 2. Java object to JSON string
        String jsonInString = gson.toJson(data);
        Log.d(TAG, "signInUserToLocalApi: jsonString: " + jsonInString);


        Call<SignInModel> call = careServiceApi.signInUser(model);
        call.enqueue(new Callback<SignInModel>() {
            @Override
            public void onResponse(Call<SignInModel> call, Response<SignInModel> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse Code: " + response.code());
                    Log.d(TAG, "onResponse Message: " + response.message());
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                SignInModel signInModel = response.body();
                if (signInModel != null) {
                    customer_id = signInModel.getSignInData().getId();
                    Log.d(TAG, "onResponse: userId: " + customer_id);
//                    Log.d(TAG, "onResponse: userEmail: "+ response.body().getEmail());
                }
            }

            @Override
            public void onFailure(Call<SignInModel> call, Throwable t) {
                Log.d("OnResponseFailure: ", t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });*/
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

    public static Boolean getDataFromSharedPrefs(Context context, String variable, Boolean defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(variable, defaultValue);
    }

    private void initViews() {
        editText_email = findViewById(R.id.edit_text_email);
        editText_password = findViewById(R.id.edit_text_password);
        textInputLayoutEmail = findViewById(R.id.text_input_layout_email);
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password);
        progressBar = findViewById(R.id.progress_bar);
    }
}
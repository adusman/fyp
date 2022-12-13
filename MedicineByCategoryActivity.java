package com.example.emedcare;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emedcare.adapters.MedicineAdapter;
import com.example.emedcare.adapters.MedicineCategoriesAdapter;
import com.example.emedcare.adapters.MedicineSearchAdapter;
import com.example.emedcare.api.CareServiceApi;
import com.example.emedcare.api.RetrofitClientInstance;
import com.example.emedcare.data.medicine.MedicineDatum;
import com.example.emedcare.data.medicine.MedicineList;
import com.example.emedcare.interfaces.RecyclerViewClickInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineByCategoryActivity extends AppCompatActivity implements RecyclerViewClickInterface {
    private static final String TAG = MedicineByCategoryActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private String categoryId;
    private String categoryName;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CareServiceApi careServiceApi;
    private MedicineList productByCategoryList;
    private MedicineSearchAdapter adapter;

    private EditText editTextSearchMedicine;
    List<MedicineDatum> list= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_by_category);

        // getting category name and id
        Intent intent = getIntent();
        if (intent != null) {
            categoryName = intent.getStringExtra("categoryName");
            categoryId = intent.getStringExtra("categoryId");
        }

        setUpToolbar();
        progressBar = findViewById(R.id.progress_bar);
        editTextSearchMedicine = findViewById(R.id.edit_text_search_medicine);
        editTextSearchMedicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


        handleApiResponse();
        initRecyclerView();
    }

    private void filter(String text) {
        List<MedicineDatum> filteredList = new ArrayList<>();
        for (MedicineDatum item : list) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                Log.d(TAG, "filter: list: "+ filteredList.size());
            }
        }
        adapter.filterList(filteredList);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.product_by_category_recyclerview);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void handleApiResponse() {
        CareServiceApi careServiceApi = RetrofitClientInstance.getRetrofitInstance().create(CareServiceApi.class);
        Call<MedicineList> call = careServiceApi.getMedicineByCategory(categoryId);
        call.enqueue(new Callback<MedicineList>() {
            @Override
            public void onResponse(Call<MedicineList> call, Response<MedicineList> response) {

                if (!response.isSuccessful()) {
                    Log.d("Code: ", String.valueOf(response.code()));
                    return;
                }

                productByCategoryList = response.body();
                list = productByCategoryList.getData();

                progressBar.setVisibility(View.VISIBLE);
                displayProductsByCategories(list);
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(ProductsByCategoryActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<MedicineList> call, Throwable t) {
                Log.d("Message: ", t.getMessage());
            }
        });
    }

    private void displayProductsByCategories(List<MedicineDatum> products) {
        adapter = new MedicineSearchAdapter(this, products);
        recyclerView.setAdapter(adapter);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(categoryName);
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

    }

    @Override
    public void onViewClick(int position, String id) {

    }
}
package com.example.emedcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emedcare.adapters.MedicineAdapter;
import com.example.emedcare.api.CareServiceApi;
import com.example.emedcare.api.RetrofitClientInstance;
import com.example.emedcare.data.medicine.MedicineDatum;
import com.example.emedcare.data.medicine.MedicineList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PharmacyDetailsActivity extends AppCompatActivity {
    private static final String TAG = PharmacyDetailsActivity.class.getSimpleName();
    private TextView tvPharmacyName;
    private String pharmacyId;
    private Toolbar toolbar;

    private MedicineAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private EditText editTextSearchMedicine;
    MedicineList list = new MedicineList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_details);
        initViews();
        getParentActivityData();
        setUpToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

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


    }

    private void filter(String text) {
        List<MedicineDatum> filteredList = new ArrayList<>();
        for (MedicineDatum item : list.getData()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                Log.d(TAG, "filter: list: "+ filteredList.size());
            }
        }

        MedicineList medicineList = new MedicineList();
        medicineList.setData(filteredList);

        adapter.filterList(medicineList);
    }

    private void handleApiResponse() {
        /*Create handle for the RetrofitInstance interface*/
        CareServiceApi service = RetrofitClientInstance.getRetrofitInstance().create(CareServiceApi.class);
        Call<MedicineList> call = service.getMedicineByPharmacy(pharmacyId);
        call.enqueue(new Callback<MedicineList>() {
            @Override
            public void onResponse(Call<MedicineList> call, Response<MedicineList> response) {
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: "+ response.body());
                list = response.body();
                populateRecyclerview(response.body());
            }

            @Override
            public void onFailure(Call<MedicineList> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PharmacyDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateRecyclerview(MedicineList medicineList) {
        adapter = new MedicineAdapter(this,medicineList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getParentActivityData() {
        Intent intent = getIntent();
        if (intent != null) {
            tvPharmacyName.setText(intent.getStringExtra("pharmacyName"));
            pharmacyId = intent.getStringExtra("pharmacyId");
        }
    }

    private void initViews() {
        tvPharmacyName = findViewById(R.id.tv_pharmacy_name);
        recyclerView = findViewById(R.id.recyclerview_medicine);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pharmacy "+ pharmacyId);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
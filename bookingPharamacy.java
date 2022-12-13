package com.example.emedcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class bookingPharamacy extends AppCompatActivity {
    private EditText tasbeehcountss,details,name;
    private Button ok,cancel;
    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    private DatabaseReference root= db.getReference().child("Doctors Khalid");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_pharamacy);
        details=findViewById(R.id.tdetails);
        name=findViewById(R.id.tname);
        tasbeehcountss=findViewById(R.id.tasbeehcounts);
        ok=findViewById(R.id.k);
        cancel=findViewById(R.id.c);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tasbehcounts = tasbeehcountss.getText().toString();
                String tdetails=details.getText().toString();
                String tname=name.getText().toString();
                HashMap<String, String> userMap= new HashMap<>();
                userMap.put("tasbehcounts", tasbehcounts);
                userMap.put("tname", tname);
                userMap.put("tdetails", tdetails);
                root.push().setValue(userMap);



                Toast.makeText(bookingPharamacy.this, "Suceesfully added", Toast.LENGTH_SHORT).show();




            }
        });


        String textview = getIntent().getStringExtra("keyname");
        tasbeehcountss.setText(textview);
    }
}
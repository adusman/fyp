package com.example.emedcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class cart4 extends AppCompatActivity {
    Button cart4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart4);
        cart4=findViewById(R.id.cartmed4);
        cart4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cart4.this,stripe.class);
                startActivity(i);
            }
        });
    }
    }

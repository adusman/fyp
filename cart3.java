package com.example.emedcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class cart3 extends AppCompatActivity {
    Button cart3;
    int minteger = 0;
    TextView integer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart3);
        cart3=findViewById(R.id.cartmednew3);
        integer=findViewById(R.id.integer_number);
        cart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = integer.getText().toString();
                Intent i = new Intent(getApplicationContext(),CheckoutActivity3.class);
                i.putExtra("message", str);
                startActivity(i);
            }
        });
    }

    public void increaseInteger(View view) {
        minteger = minteger - 1;
        display(minteger);

    }
    private void display(int minteger) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + minteger);
    }

    public void decreaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);
    }
}
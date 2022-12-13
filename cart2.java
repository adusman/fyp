package com.example.emedcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class cart2 extends AppCompatActivity {
    Button cart2;
    int minteger = 0;
    TextView integer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart2);
        cart2=findViewById(R.id.cartmed2);
        integer=findViewById(R.id.integer_number);

        cart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = integer.getText().toString();
                Intent i = new Intent(getApplicationContext(),CheckoutActivitytwo.class);
                i.putExtra("message", str);
                startActivity(i);

            }
        });
    }

    public void decreaseInteger(View view) {
        minteger = minteger - 1;
        display(minteger);

    }

    private void display(int minteger) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + minteger);
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);
    }
}
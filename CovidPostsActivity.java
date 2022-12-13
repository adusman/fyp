package com.example.emedcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class CovidPostsActivity extends AppCompatActivity {

    JSONObject jsonObject ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_posts);

        String post = getIntent().getStringExtra("post");

        try {
            jsonObject = new JSONObject(post);

            TextView textView = findViewById(R.id.title);
            textView.setText(jsonObject.getString("title"));

            ImageView imageView = findViewById(R.id.image);

            Glide.with(this).load(jsonObject.getString("image_url")).error(R.drawable.app_icon).into(imageView);

            TextView details = findViewById(R.id.details);
            details.setText(jsonObject.getString("detail"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
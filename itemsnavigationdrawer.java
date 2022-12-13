package com.example.emedcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class itemsnavigationdrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout11;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemsnavigationdrawer);
        toolbar=findViewById(R.id.toolbar1);
        drawerLayout11=findViewById(R.id.drawerlayout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout11.openDrawer(Gravity.LEFT);
                // Toast.makeText(example.this, "kkkkkkk", Toast.LENGTH_SHORT).show();
            }
        });
        //
        drawerLayout11 = findViewById(R.id.drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout11, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout11.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //

        actionBarDrawerToggle=new ActionBarDrawerToggle(itemsnavigationdrawer.this,drawerLayout11,R.string.app_name,R.string.app_name);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navview);
        navigationView.setNavigationItemSelectedListener(this);




    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_pharmacies){
            startActivity(new Intent(itemsnavigationdrawer.this,NearbyplacesMapsActivity.class));
        }
        if(id == R.id.nav_medicine_categories){
            startActivity(new Intent(itemsnavigationdrawer.this,searchpharmicies.class));
        }
        if(id == R.id.nav_medicine){
            startActivity(new Intent(itemsnavigationdrawer.this,searchdoctor.class));
        }
        if(id == R.id.nav_blog){
            startActivity(new Intent(itemsnavigationdrawer.this,CheckoutActivity.class));

        }
        return true;
    }


}
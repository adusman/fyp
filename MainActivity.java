package com.example.emedcare;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View navHeader =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView ivUserProfile = navHeader.findViewById(R.id.iv_user_profile);
        TextView tvUserName = navHeader.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = navHeader.findViewById(R.id.tv_user_email);

        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));

        if (mUser != null) {
            Glide.with(this)
                    .load(mUser.getPhotoUrl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .error(R.drawable.ic_user)
                    .into(ivUserProfile);

            tvUserName.setVisibility(View.VISIBLE);
            tvUserEmail.setVisibility(View.VISIBLE);
            tvUserName.setText(mUser.getDisplayName());
            tvUserEmail.setText(mUser.getEmail());
        }

        ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.user_profile_popup_layout);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                ImageView userProfile = dialog.findViewById(R.id.image_view_user_profile);
                TextView tvUserName = dialog.findViewById(R.id.tv_user_name);
                TextView tvUserEmail = dialog.findViewById(R.id.tv_user_email);
                Button btnLogout = dialog.findViewById(R.id.button_logout);

                if (mUser != null) {
                    tvUserName.setVisibility(View.VISIBLE);
                    tvUserEmail.setVisibility(View.VISIBLE);
                    Glide.with(MainActivity.this)
                            .load(mUser.getPhotoUrl())
                            .into(userProfile);
                    tvUserName.setText(mUser.getDisplayName());
                    tvUserEmail.setText(mUser.getEmail());
                    btnLogout.setVisibility(View.VISIBLE);
                }

                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUser != null) {
                            mAuth.signOut();
//                            Auth.GoogleSignInApi.signOut(mUser);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Sign out successfully!", Toast.LENGTH_SHORT).show();
                            drawer.close();
                        }
                    }
                });
                dialog.show();
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_medicine_categories, R.id.nav_pharmacies, R.id.nav_medicine)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //


    //


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_pharmacies) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.nav_medicine_categories) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.nav_medicine) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.nav_blog) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //



        //
    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.exit_alert_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.custom_btn_white));
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        //dialog.getWindow().getAttributes().windowAnimations = R.style.exitDialogAnimation;

        TextView btnYes = dialog.findViewById(R.id.button_ok);
        TextView btnNo = dialog.findViewById(R.id.button_cancel);
        TextView icRateUs = dialog.findViewById(R.id.ic_rate_us);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // exit application
                dialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        icRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                    final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(rateAppIntent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "ActivityNotFoundException: Error " + e.getMessage());
                }
            }
        });

        dialog.show();
        //






    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_pharmacies){
            startActivity(new Intent(MainActivity.this,newnavigatiom.class));
        }
        if(id == R.id.nav_medicine_categories){
            Toast.makeText(MainActivity.this, "kkkkkkk", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.nav_medicine){
            Toast.makeText(MainActivity.this, "kkkkkkk", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.nav_blog){
            Toast.makeText(MainActivity.this, "kkkkkkk", Toast.LENGTH_SHORT).show();

        }
        return false;
    }
}
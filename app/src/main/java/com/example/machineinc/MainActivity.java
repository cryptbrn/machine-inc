package com.example.machineinc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    TextView tvTemperature;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    LinearLayout progress;
    SwipeRefreshLayout refreshLayout;
    NavigationView navigation;
    ImageView btnNavigation;
    User user;
    TextView tvLogout;
    LinearLayout temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        onClick();
    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        tvTemperature = findViewById(R.id.main_tv_temp);
        drawerLayout = findViewById(R.id.drawer_layout);
        progress = findViewById(R.id.main_progress);
        refreshLayout = findViewById(R.id.home_swipe_refresh);
        btnNavigation = findViewById(R.id.main_iv_nav);
        navigation = findViewById(R.id.navigation_view);
        tvLogout = navigation.findViewById(R.id.nav_tv_logout);
        temp = findViewById(R.id.main_ll);
        Glide.with(this)
                .load(R.drawable.background_temperature)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        temp.setBackground(resource);
                    }
                });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                getTemperature();
            }
        });
        getTemperature();
        getUserInfo();
    }

    private void getUserInfo() {
        showProgress(true);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if(user!=null){
                    showProgress(false);
                    TextView name = navigation.findViewById(R.id.nav_tv_name);
                    TextView role = navigation.findViewById(R.id.nav_tv_role);
                    name.setText(user.name);
                    role.setText(user.role);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTemperature() {
        showProgress(true);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hasil_Pembacaan");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Temperature temperature = snapshot.getValue(Temperature.class);
                if(temperature!=null){
                    showProgress(false);
                    tvTemperature.setText(temperature.suhu.toString());
                }
                else{
                    showProgress(false);
                    Toast.makeText(MainActivity.this, "Failed to load temperature data, please refresh later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgress(false);
                Toast.makeText(MainActivity.this, "Failed to load temperature data, please refresh later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClick() {
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);
                showConfirmDialog();
            }
        });
    }

    private void showConfirmDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirmation,null);

        TextView cancel = mView.findViewById(R.id.confirm_tv_cancel);
        TextView yes = mView.findViewById(R.id.confirm_tv_yes);


        mBuilder.setView(mView);
        Dialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgress(boolean show){
        if(show){
            progress.setVisibility(View.VISIBLE);
        }
        else {
            progress.setVisibility(View.GONE);
        }
        disableTouch(show);

    }

    private void disableTouch(boolean status) {
        if(status){
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}

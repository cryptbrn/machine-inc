package com.example.machineinc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    ImageView navigation;
    FirebaseUser firebaseUser;
    TextView tvTemperature;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        onClick();
    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        navigation = findViewById(R.id.main_iv_nav);
        tvTemperature = findViewById(R.id.main_tv_temp);
        setData();
    }

    private void setData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hasil_Pembacaan");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Temperature temperature = snapshot.getValue(Temperature.class);
                if(temperature!=null){
                    tvTemperature.setText(temperature.suhu.toString() + "°");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onClick() {
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNavigationMenu();
            }
        });
    }

    private void showNavigationMenu() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.nav_dialog,null);

        TextView name = mView.findViewById(R.id.nav_tv_name);
        TextView role = mView.findViewById(R.id.nav_tv_role);
        TextView logout = mView.findViewById(R.id.nav_tv_logout);
        LinearLayout root = mView.findViewById(R.id.nav_root);
        mBuilder.setView(mView);
        Dialog dialog = mBuilder.create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);


    }


}

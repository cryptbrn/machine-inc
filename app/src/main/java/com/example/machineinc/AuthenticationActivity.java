package com.example.machineinc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticationActivity extends AppCompatActivity {

    TextView registerTv;
    TextView loginTv;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser !=null){
            Intent intent = new Intent(AuthenticationActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
        init();
        onClick();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        registerTv = findViewById(R.id.auth_tv_register);
        loginTv = findViewById(R.id.auth_tv_login);
    }


    private void onClick() {
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetRegister();
            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetLogin();
            }
        });
    }

    private void showBottomSheetLogin() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
        );

        View bottomSheetView = LayoutInflater.from(this)
                .inflate(
                        R.layout.bottom_sheet_login,
                        (LinearLayout) findViewById(R.id.bottom_container)
                );

        EditText etEmail = bottomSheetView.findViewById(R.id.login_et_email);
        EditText etPassword = bottomSheetView.findViewById(R.id.login_et_password);
        Button submit = bottomSheetView.findViewById(R.id.login_btn_submit);
        LinearLayout progressBar = bottomSheetView.findViewById(R.id.login_progress);


        if (submit != null) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etPassword != null && etEmail!= null) {
                        checkLogin(etEmail, etPassword, progressBar);
                    }
                }
            });
        }

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void checkLogin(EditText etEmail, EditText etPassword, LinearLayout progressBar) {
        etEmail.setError(null);
        etPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            etEmail.setError(getString(R.string.error_empty_field));
            focusView = etEmail;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)){
            etPassword.setError(getString(R.string.error_empty_field));
            focusView = etPassword;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }
        else {
            attemptLogin(email, password, progressBar);
        }


    }

    private void attemptLogin(String email, String password, LinearLayout progressBar) {
        showProgress(true,progressBar);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    showProgress(false,progressBar);
                    Intent intent = new Intent(AuthenticationActivity.this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    showProgress(false,progressBar);
                    Toast.makeText(AuthenticationActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showBottomSheetRegister() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
        );

        View bottomSheetView = LayoutInflater.from(this)
                .inflate(
                        R.layout.bottom_sheet_register,
                        (LinearLayout) findViewById(R.id.bottom_container)
                );

        EditText etEmail = bottomSheetView.findViewById(R.id.register_et_email);
        EditText etPassword = bottomSheetView.findViewById(R.id.register_et_password);
        EditText etName = bottomSheetView.findViewById(R.id.register_et_name);
        Spinner spnRole = bottomSheetView.findViewById(R.id.register_spinner_role);
        Button submit = bottomSheetView.findViewById(R.id.register_btn_submit);
        LinearLayout progressBar = bottomSheetView.findViewById(R.id.register_progress);

        if (submit != null) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etPassword != null && etEmail!= null && etName!=null && spnRole!=null) {
                        checkRegister(etEmail, etPassword, etName, spnRole, progressBar);

                    }
                }
            });
        }


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void checkRegister(EditText etEmail, EditText etPassword, EditText etName, Spinner spnRole, LinearLayout progressBar) {
        etEmail.setError(null);
        etPassword.setError(null);
        etName.setError(null);

        boolean cancel = false;
        View focusView = null;

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String name = etName.getText().toString();
        String role = spnRole.getSelectedItem().toString();

        if(TextUtils.isEmpty(name)){
            etName.setError(getString(R.string.error_empty_field));
            focusView = etName;
            cancel = true;
        }

        if(TextUtils.isEmpty(email)){
            etEmail.setError(getString(R.string.error_empty_field));
            focusView = etEmail;
            cancel = true;
        }

        if(role.equals("-- Select Type --")){
            Toast.makeText(this, "Please choose role first",
                    Toast.LENGTH_LONG).show();
            focusView = spnRole;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)){
            etPassword.setError(getString(R.string.error_empty_field));
            focusView = etPassword;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }
        else {
            attemptRegister(email, password, name, role, progressBar);
        }
    }

    private void attemptRegister(String email, String password, String name, String role, LinearLayout progress) {
        showProgress(true,progress);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User(
                                    name,
                                    role
                            );
                            UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(firebaseUser.getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        showProgress(false,progress);
                                        Toast.makeText(AuthenticationActivity.this, "Registration Success", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(AuthenticationActivity.this, AuthenticationActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        showProgress(false, progress);
                                        Toast.makeText(AuthenticationActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                        }
                        else {
                            showProgress(false, progress);
                            Toast.makeText(AuthenticationActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }


    private void showProgress(boolean show, LinearLayout progress){
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
package com.example.uasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    private EditText nama_regis, email_regis, password_regis, passwordconfirm_regis;
    private Button btn_regis;
    private TextView signin_regis;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        nama_regis = findViewById(R.id.nama_regis);
        email_regis = findViewById(R.id.email_regis);
        password_regis = findViewById(R.id.password_regis);
        passwordconfirm_regis = findViewById(R.id.passwordconfirm_regis);
        btn_regis = findViewById(R.id.btn_regis);
        signin_regis = findViewById(R.id.signin_regis);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // progress message
        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);

        signin_regis.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        btn_regis.setOnClickListener(v -> {
            if (nama_regis.getText().length() > 0 && email_regis.getText().length() > 0 &&
                    password_regis.getText().length() > 0){

                if (password_regis.getText().toString().equals(passwordconfirm_regis.getText().toString())){
                    register(nama_regis.getText().toString(),
                            email_regis.getText().toString(),
                            password_regis.getText().toString());
                }

                else{
                    Toast.makeText(getApplicationContext(), "Silahkan Masukkan Password Yang Sesuai!", Toast.LENGTH_SHORT).show();
                }
            }

            else{
                Toast.makeText(getApplicationContext(), "Silahkan Isi Semua Data!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void register(String nama, String email, String password){
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null){
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(nama).build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                reload();
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Registrasi Gagal!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void reload(){
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
}
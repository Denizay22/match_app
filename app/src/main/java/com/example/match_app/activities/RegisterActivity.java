package com.example.match_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.match_app.R;
import com.example.match_app.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {


    EditText et_name, et_surname, et_email, et_password;
    Button b_register, b_go_back;
    String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar progressbar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = findViewById(R.id.register_et_name);
        et_surname = findViewById(R.id.register_et_surname);
        et_email = findViewById(R.id.register_et_email);
        et_password = findViewById(R.id.register_et_password);
        b_register = findViewById(R.id.register_b_register);
        b_go_back = findViewById(R.id.register_r_goback);
        progressbar = findViewById(R.id.register_progressbar);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();


        b_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                perform_auth();
            }
        });

        b_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_start();
            }
        });

    }

    private void perform_auth() {
        progressbar.setVisibility(View.VISIBLE);
        String name = et_name.getText().toString();
        String surname = et_surname.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (name.isEmpty()){
            progressbar.setVisibility(View.GONE);
            et_name.setError("Lütfen isminizi giriniz.");
            et_name.requestFocus();
        }
        else if (surname.isEmpty()){
            progressbar.setVisibility(View.GONE);
            et_surname.setError("Lütfen soyadınızı giriniz.");
            et_name.requestFocus();
        }
        else if (!email.matches(email_pattern)){
            progressbar.setVisibility(View.GONE);
            et_email.setError("Lütfen geçerli bir e-posta giriniz.");
            et_email.requestFocus();
        }
        else if (password.isEmpty() || password.length() < 8){
            progressbar.setVisibility(View.GONE);
            et_password.setError("Lütfen şifrenizi en az 8 karakter olacak şekilde giriniz.");
            et_password.requestFocus();
        }
        else {

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        save_data_to_database(name, surname, email);
                    }
                    else{
                        progressbar.setVisibility(View.GONE);
                        try{
                            throw Objects.requireNonNull(task.getException());
                        }
                        catch (FirebaseAuthUserCollisionException e){
                            et_email.setError("Bu e-posta ile zaten mevcut bir hesap var.");
                            et_email.requestFocus();
                        }
                        catch (Exception e){
                            Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    }

    private void save_data_to_database(String name, String surname, String email) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        User writeUserDetails = new User(name, surname, email, "", user.getUid(), "",
                0, "", 0, 0, "");


        Log.d("asd", writeUserDetails.toString());

        //foto hariç verilerin realtime database'e yazılması
        databaseReference.child(user.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("asd", "servera yazma basladı...");
                if (task.isSuccessful()){
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                    send_user_to_profile();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Kayıt başarısız.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void send_user_to_profile() {
        Intent intent = new Intent(RegisterActivity.this, ProfileSetupActivity.class);
        startActivity(intent);
        finish();
    }

    private void send_user_to_start() {
        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
package com.example.match_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.match_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button b_login, b_forgotp, b_go_back;
    String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; //TODO std.yildiz.edu.tr
    ProgressBar progressBar;
    FirebaseAuth m_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        et_email = findViewById(R.id.login_et_mail);
        et_password = findViewById(R.id.login_et_password);
        b_forgotp = findViewById(R.id.login_b_change_password);
        b_login = findViewById(R.id.login_b_login);
        b_go_back = findViewById(R.id.login_b_go_back);
        progressBar = findViewById(R.id.login_progressbar);
        m_auth = FirebaseAuth.getInstance();

        b_login.setOnClickListener(new View.OnClickListener(){
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

        b_forgotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialog_view = getLayoutInflater().inflate(R.layout.forgot_password, null);
                EditText et_email = dialog_view.findViewById(R.id.forgot_password_et_email);


                builder.setView(dialog_view);

                AlertDialog dialog = builder.create();

                dialog_view.findViewById(R.id.forgot_password_b_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mail = et_email.getText().toString();
                        if (mail.isEmpty() || !mail.matches(email_pattern)){
                            Toast.makeText(LoginActivity.this, "Lütfen geçerli bir e-posta adresi giriniz.", Toast.LENGTH_SHORT).show();
                        }
                        m_auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Şifre sıfırlama e-postası gönderildi.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Doğrulama postası gönderilemedi.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                dialog_view.findViewById(R.id.forgot_password_b_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

    }

    private void perform_auth() {
        progressBar.setVisibility(View.VISIBLE);
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (!email.matches(email_pattern)){
            progressBar.setVisibility(View.GONE);
            et_email.setError("Lütfen geçerli bir e-posta giriniz.");
            et_email.requestFocus();
        }
        else if (password.isEmpty() || password.length() < 8){
            progressBar.setVisibility(View.GONE);
            et_password.setError("Lütfen şifrenizi en az 8 karakter olacak şekilde giriniz.");
            et_password.requestFocus();
        }
        else {

            m_auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //progressDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        send_user_to_main();
                        Toast.makeText(LoginActivity.this, "Giriş başarılı.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (FirebaseAuthInvalidUserException e){
                            et_email.setError("Girdiğiniz e-posta hatalı.");
                            et_email.requestFocus();
                        }
                        catch (FirebaseAuthInvalidCredentialsException e){
                            et_password.setError("Girdiğiniz şifre hatalı.");
                            et_password.requestFocus();
                        }
                        catch (Exception e){
                            Toast.makeText(LoginActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    }
    private void send_user_to_main() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void send_user_to_start() {
        Intent intent = new Intent(LoginActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
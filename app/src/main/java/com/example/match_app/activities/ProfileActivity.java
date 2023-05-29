package com.example.match_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.match_app.R;
import com.example.match_app.helpers.Match;
import com.example.match_app.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    EditText et_name, et_surname, et_email, et_phone,
            et_status, et_duration, et_job_company,
            et_department, et_year_class, et_distance_to_campus;

    Button b_update_profile, b_go_back_to_main, b_send_match_request;
    ImageView profilepic;

    ProgressBar progressbar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    int mode;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        et_name = findViewById(R.id.profile_et_name);
        et_name.setEnabled(false);
        et_surname = findViewById(R.id.profile_et_surname);
        et_surname.setEnabled(false);
        et_email = findViewById(R.id.profile_et_email);
        et_email.setEnabled(false);
        et_phone = findViewById(R.id.profile_et_phone);
        et_phone.setEnabled(false);
        et_department = findViewById(R.id.profile_et_department);
        et_department.setEnabled(false);
        et_year_class = findViewById(R.id.profile_et_year_class);
        et_year_class.setEnabled(false);
        et_distance_to_campus = findViewById(R.id.profile_et_distance_to_campus);
        et_distance_to_campus.setEnabled(false);
        et_status = findViewById(R.id.profile_et_status);
        et_status.setEnabled(false);
        et_duration = findViewById(R.id.profile_et_duration);
        et_duration.setEnabled(false);


        progressbar = findViewById(R.id.profile_progressbar);


        profilepic = findViewById(R.id.profile_i_profilepic);

        b_update_profile = findViewById(R.id.profile_button_saveprofile);
        b_go_back_to_main = findViewById(R.id.profile_button_go_main_menu);
        b_send_match_request = findViewById(R.id.profile_button_send_match_request);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();

        fill_fields(bundle);

        b_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_profile_setup();
            }
        });

        b_go_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_back(mode);
            }
        });

        b_send_match_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_match_request();
            }
        });



    }

    private void send_match_request() {
        progressbar.setVisibility(View.VISIBLE);
        Match match = new Match(user.getUid(), userID, "pending");
        String match_ID;
        if (user.getUid().compareTo(userID) > 0){
            match_ID = user.getUid() + "+" + userID;
        }
        else{
            match_ID = userID + "+" + user.getUid();
        }
        databaseReference.child("matches").child(match_ID).setValue(match).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Eşleşme isteği başarıyla yollandı.", Toast.LENGTH_SHORT).show();
                    b_send_match_request.setEnabled(false);
                }
                else{
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Eşleşme isteği yollanamadı.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fill_fields(Bundle bundle) {
        progressbar.setVisibility(View.VISIBLE);
        //eğer bundle varsa user_class, serverdan gelen UID ile çekilecek
        //eğer yoksa auth.getcurrentUser ile çekilecek.


        if (bundle == null){
            //kullanıcı kendi profilini görüntülüyor
            userID = user.getUid();
            mode = 1;
            b_send_match_request.setVisibility(View.GONE);
            b_send_match_request.setEnabled(false);
        }
        else{
            //kullanıcı başka profili görüntülüyor
            userID = bundle.getString("userID");
            b_update_profile.setVisibility(View.GONE);
            b_update_profile.setEnabled(false);
            b_go_back_to_main.setText(getResources().getString(R.string.go_back));
            mode = 2;
        }

        databaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_class = snapshot.getValue(User.class);

                if (user_class != null){
                    et_name.setText(user_class.getName());
                    et_surname.setText(user_class.getSurname());
                    et_email.setText(user_class.getEmail());
                    et_phone.setText(user_class.getPhone_no());
                    et_status.setText(user_class.getStatus());
                    et_duration.setText(user_class.getDuration() + " ay");
                    et_department.setText(user_class.getDepartment());
                    et_year_class.setText(Integer.toString(user_class.getYear()));
                    et_distance_to_campus.setText(user_class.getDistance_to_campus() + " dk");

                    if (user_class.getStatus().equals("Aramıyor")){
                        et_duration.setText("");
                        et_distance_to_campus.setText("");
                    }
                    //eger kullancının fotosu varsa onu göster
                    String photo_link = user_class.getPhoto();
                    if (photo_link.length() != 0){
                        Uri uri_photo = Uri.parse(photo_link);
                        if (uri_photo != null && !uri_photo.toString().isEmpty()){
                            Picasso.with(ProfileActivity.this).load(uri_photo).into(profilepic);
                        }
                    }
                }
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Sunucudan veriler alınırken hata meydana geldi, lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();
            }
        });

        if (!user.getUid().equals(userID)){
            String match_ID;
            if (user.getUid().compareTo(userID) > 0){
                match_ID = user.getUid() + "+" + userID;
            }
            else{
                match_ID = userID + "+" + user.getUid();
            }
            databaseReference.child("matches").child(match_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Match match = snapshot.getValue(Match.class);
                    if (match == null){
                        databaseReference.child("users").child(user.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user_class = snapshot.getValue(User.class);
                                        if (user_class != null){
                                            if ((user_class.getStatus().equals("Ev Arkadaşı Arıyor") && et_status.getText().toString().equals("Ev Arıyor")) ||
                                                    (user_class.getStatus().equals("Ev Arıyor") && et_status.getText().toString().equals("Ev Arkadaşı Arıyor"))){
                                                b_send_match_request.setVisibility(View.VISIBLE);
                                                b_send_match_request.setEnabled(true);
                                            }
                                            else{
                                                b_send_match_request.setVisibility(View.GONE);
                                                b_send_match_request.setEnabled(false);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ProfileActivity.this,
                                                "Sunucudan veriler alınırken hata meydana geldi, lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else{
                        b_send_match_request.setEnabled(false);
                        b_send_match_request.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this,
                            "Sunucudan veriler alınırken hata meydana geldi, lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();

                }
            });


        }
        else{
            b_send_match_request.setVisibility(View.GONE);
            b_send_match_request.setEnabled(false);
        }
    }
    private void send_user_to_profile_setup() {
        Intent intent = new Intent(ProfileActivity.this, ProfileSetupActivity.class);
        startActivity(intent);
        finish();
    }

    private void send_user_back(int mode) {
        Intent intent;
        if (mode == 1){
            //kendi profiline bakıyor
            intent = new Intent(ProfileActivity.this, MainActivity.class);
        }
        else{
            //başka birinin profiline bakıyor
            intent = new Intent(ProfileActivity.this, UserSearchActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
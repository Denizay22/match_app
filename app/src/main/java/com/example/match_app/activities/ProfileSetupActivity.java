package com.example.match_app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.match_app.R;
import com.example.match_app.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

@SuppressWarnings("ALL")
public class ProfileSetupActivity extends AppCompatActivity {

    EditText et_name, et_surname, et_email, et_phone, et_distance_to_campus, et_duration, et_department;
    RadioButton rb_looking_for_home, rb_looking_for_roommate, rb_none;
    Spinner sp_year_class;
    Button b_save_profile;
    String status_global;

    ImageView profilepic;
    Uri uri;
    Bitmap bitmap;

    ProgressBar progressbar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        et_name = findViewById(R.id.profilesetup_et_name);
        et_surname = findViewById(R.id.profilesetup_et_surname);
        et_email = findViewById(R.id.profilesetup_et_email);
        et_email.setEnabled(false);
        et_phone = findViewById(R.id.profilesetup_et_phone);
        et_department = findViewById(R.id.profilesetup_et_department);
        et_distance_to_campus = findViewById(R.id.profilesetup_et_distance_to_campus);
        et_duration = findViewById(R.id.profilesetup_et_duration);

        sp_year_class = findViewById(R.id.profilesetup_sp_year_class);

        rb_looking_for_roommate = findViewById(R.id.radio_looking_for_roommate);
        rb_looking_for_home = findViewById(R.id.radio_looking_for_home);
        rb_none = findViewById(R.id.radio_none);


        progressbar = findViewById(R.id.profilesetup_progressbar);


        profilepic = findViewById(R.id.profilesetup_i_profilepic);
        b_save_profile = findViewById(R.id.profilesetup_button_saveprofile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] years = {"1", "2", "3", "4"};
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_year_class.setAdapter(spinner_adapter);


        fill_fields(user, spinner_adapter);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_profile_pic();
            }
        });


        b_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_inputs();
            }
        });


    }

    private void fill_fields(FirebaseUser fb_user, ArrayAdapter<String> spinner_adapter) {
        progressbar.setVisibility(View.VISIBLE);
        String userID = fb_user.getUid();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_class = snapshot.getValue(User.class);
                progressbar.setVisibility(View.GONE);
                if (user_class != null){
                    et_name.setText(user_class.getName());
                    et_surname.setText(user_class.getSurname());
                    et_email.setText(user_class.getEmail());
                    et_phone.setText(user_class.getPhone_no());
                    et_department.setText(user_class.getDepartment());
                    et_distance_to_campus.setText(Integer.toString(user_class.getDistance_to_campus()));
                    et_duration.setText(Integer.toString(user_class.getDuration()));
                    sp_year_class.setSelection(spinner_adapter.getPosition(Integer.toString(user_class.getYear())));
                    status_global = user_class.getStatus();
                    if (status_global.equals(rb_looking_for_roommate.getText().toString())){
                        rb_looking_for_roommate.setChecked(true);
                    }
                    else if (status_global.equals(rb_looking_for_home.getText().toString())){
                        rb_looking_for_home.setChecked(true);
                    }
                    else if (status_global.equals(rb_none.getText().toString())){
                        rb_none.setChecked(true);
                        et_duration.setText("");
                        et_duration.setEnabled(false);
                        et_distance_to_campus.setText("");
                        et_distance_to_campus.setEnabled(false);
                    }

                    //eger serverda foto varsa onu goster
                    Uri uri_server = user.getPhotoUrl();

                    if (uri_server != null && !uri_server.toString().isEmpty()){
                        Picasso.with(ProfileSetupActivity.this).load(uri_server).into(profilepic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileSetupActivity.this,
                        "Sunucudan veriler alınırken hata meydana geldi, lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void check_inputs() {
        progressbar.setVisibility(View.VISIBLE);
        String name = et_name.getText().toString();
        String surname = et_surname.getText().toString();
        String email = et_email.getText().toString();
        String phone_no = et_phone.getText().toString();
        String distance_to_campus = et_distance_to_campus.getText().toString();
        String duration = et_duration.getText().toString();
        String department = et_department.getText().toString();
        String year = sp_year_class.getSelectedItem().toString();

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
        else if(phone_no.length() != 11){
            progressbar.setVisibility(View.GONE);
            et_phone.setError("Lütfen geçerli bir telefon numarası giriniz.");
            et_phone.requestFocus();
        }
        else if(department.isEmpty()){
            progressbar.setVisibility(View.GONE);
            et_department.setError("Lütfen bölümünüzü giriniz.");
            et_department.requestFocus();
        }
        else if(status_global.isEmpty()){
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, "Lütfen durumunuzu seçiniz.", Toast.LENGTH_SHORT).show();
        }
        else if(!status_global.equals("Aramıyor") && distance_to_campus.isEmpty()){
            progressbar.setVisibility(View.GONE);
            et_distance_to_campus.setError("Lütfen eğer ev arkadaşı arıyorsanız evinizin kampüse" +
                    " olan uzaklığını, ev arıyorsanız aradığınız evin kampüse olabilecek" +
                    " maksimum uzaklığını giriniz.");
            et_distance_to_campus.requestFocus();
        }
        else if(!status_global.equals("Aramıyor") && duration.isEmpty()){
            progressbar.setVisibility(View.GONE);
            et_duration.setError("Lütfen süre giriniz.");
            et_duration.requestFocus();
        }
        else {
            save_profile(name, surname, email, user.getUid(), department, year, status_global, distance_to_campus, duration, phone_no);
        }

    }

    public void onRadioButtonClicked(@NonNull View view) {
        if(view.getId() == R.id.radio_looking_for_roommate){
            status_global = "Ev Arkadaşı Arıyor";
            et_duration.setEnabled(true);
            et_distance_to_campus.setEnabled(true);
        }
        else if (view.getId() == R.id.radio_looking_for_home){
            status_global = "Ev Arıyor";
            et_duration.setEnabled(true);
            et_distance_to_campus.setEnabled(true);
        }
        else if (view.getId() == R.id.radio_none){
            status_global = "Aramıyor";
            et_duration.setEnabled(false);
            et_duration.setText("");
            et_distance_to_campus.setEnabled(false);
            et_distance_to_campus.setText("");
        }
    }

    private void save_profile(String name, String surname, String email,
                              String user_ID, String department,
                              String year, String status, String distance_to_campus,
                              String duration, String phone_no) {
        User user_class = new User(name, surname, email, "", user_ID, department,
                Integer.parseInt(year), status, 0, 0, phone_no);
        if (!distance_to_campus.isEmpty()) user_class.setDistance_to_campus(Integer.parseInt(distance_to_campus));
        if (!duration.isEmpty()) user_class.setDuration(Integer.parseInt(duration));
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        if (uri != null){
            StorageReference fileReference =  storageReference.child("profile_pics").child(user.getUid() + "." + getFileExtension(uri));
            fileReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //fotonun yüklenmesi
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user_class.setPhoto(uri.toString());
                            user = auth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri).build();
                            user.updateProfile(profileUpdates);


                            save_data_to_server(user_class);
                        }
                    });

                }
            });
        }

        else{
            save_data_to_server(user_class);
        }

    }

    private void save_data_to_server(User user_class){
        progressbar.setVisibility(View.VISIBLE);
        databaseReference.child(user.getUid()).setValue(user_class).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(ProfileSetupActivity.this, "Profil başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                    send_user_to_profile();
                }
                else{
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(ProfileSetupActivity.this, "Profil kaydedilemedi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void select_profile_pic() {
        Dexter.withActivity(ProfileSetupActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Resim Dosyası Seçiniz"), 1);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1  && resultCode==RESULT_OK && data != null && data.getData() != null)
        {
            uri=data.getData();
            try{
                InputStream inputStream=getContentResolver().openInputStream(uri);
                bitmap= BitmapFactory.decodeStream(inputStream);
                profilepic.setImageBitmap(bitmap);
            }catch (Exception ex)
            {
                Toast.makeText(this, "Sunucudan veri alırken hata meydana geldi.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void send_user_to_profile() {
        Intent intent = new Intent(ProfileSetupActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

}
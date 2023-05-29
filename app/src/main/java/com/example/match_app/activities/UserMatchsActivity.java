package com.example.match_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.match_app.R;
import com.example.match_app.helpers.Match;
import com.example.match_app.helpers.User;
import com.example.match_app.helpers.MatchViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserMatchsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    Button b_go_back;
    DatabaseReference databaseReference;
    MatchViewAdapter matchViewAdapter;
    ArrayList<User> users;
    ArrayList<Match> matches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_matchs);

        recyclerView = findViewById(R.id.user_matchs_rcview);
        b_go_back = findViewById(R.id.user_matchs_b_goback);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        users = new ArrayList<>();
        matches = new ArrayList<>();

        matchViewAdapter = new MatchViewAdapter(this, matches, users);
        recyclerView.setAdapter(matchViewAdapter);
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }
                matchViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Match match = dataSnapshot.getValue(Match.class);
                    matches.add(match);
                }
                matchViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_main();
            }
        });


    }


    private void send_user_to_main() {
        Intent intent = new Intent(UserMatchsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
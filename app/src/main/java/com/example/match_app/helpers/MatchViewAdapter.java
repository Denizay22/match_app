package com.example.match_app.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.match_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MatchViewAdapter extends RecyclerView.Adapter<MatchViewAdapter.Holder>{
    Context context;
    ArrayList<Match> matches;
    ArrayList<User> users;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    public static class Holder extends RecyclerView.ViewHolder{
        TextView name_surname, status, department, distance_to_campus, year_class, duration, mail, phone;
        ImageView profile_pic;
        Button b_accept, b_reject, b_withdraw;
        LinearLayout ll_mail, ll_phone;

        public Holder(@NonNull View itemView){
            super(itemView);

            name_surname = itemView.findViewById(R.id.match_item_name_surname);
            status = itemView.findViewById(R.id.match_item_status);
            department = itemView.findViewById(R.id.match_item_department);
            distance_to_campus = itemView.findViewById(R.id.match_item_distance_to_campus);
            year_class = itemView.findViewById(R.id.match_item_year_class);
            duration = itemView.findViewById(R.id.match_item_duration);
            profile_pic = itemView.findViewById(R.id.match_item_profilepic);
            b_accept = itemView.findViewById(R.id.match_item_accept_button);
            b_reject = itemView.findViewById(R.id.match_item_reject_button);
            b_withdraw = itemView.findViewById(R.id.match_item_withdraw_button);
            mail = itemView.findViewById(R.id.match_item_email);
            phone = itemView.findViewById(R.id.match_item_phone);
            ll_mail = itemView.findViewById(R.id.match_ll_mail);
            ll_phone = itemView.findViewById(R.id.match_ll_phone);
        }
    }

    public MatchViewAdapter(Context context, ArrayList<Match> matches, ArrayList<User> users){
        this.context = context;
        this.matches = matches;
        this.users = users;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.database = FirebaseDatabase.getInstance();
        this.databaseReference = this.database.getReference();
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position){
        //görüntüleyen kullanıcı sender mı receiver mı kontrol et
        //eğer sender ise iptal etme koy
        //eğer receiver ise kabul etme koy
        Match match = matches.get(position);

        User sender = find_user_by_id(match.getSender_UID());
        User receiver = find_user_by_id(match.getReceiver_UID());

        if (!match.getStatus().equals("pending")){
            holder.itemView.setVisibility(View.GONE);
        }
        else{
            holder.ll_mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //intent.setData(Uri.parse("mailto:"));

                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{holder.mail.getText().toString()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Eşleşme İsteği");
                    intent.putExtra(Intent.EXTRA_TEXT, "Mesajınızı yazınız.");
                    intent.setType("message/rfc822");
                    if (intent.resolveActivity(context.getPackageManager()) != null){
                        context.startActivity(intent);
                    }
                    else{
                        Toast.makeText(context, "E-posta yollamak için bir uygulama bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.ll_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + "9" + holder.phone.getText().toString() +
                            "&text" + "Mesajınızı giriniz."));
                    context.startActivity(intent);
                }
            });

            holder.b_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String match_ID;
                    if (match.getSender_UID().compareTo(match.getReceiver_UID()) > 0){
                        match_ID = match.getSender_UID() + "+" + match.getReceiver_UID();
                    }
                    else{
                        match_ID = match.getReceiver_UID() + "+" + match.getSender_UID();
                    }
                    match.setStatus("accepted");
                    databaseReference.child("matches").child(match_ID).setValue(match).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Eşleşme isteği kabul edildi!", Toast.LENGTH_SHORT).show();
                                databaseReference.child("users").child(match.getSender_UID()).child("status").setValue("Aramıyor");
                                databaseReference.child("users").child(match.getReceiver_UID()).child("status").setValue("Aramıyor");
                                matches.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(context, "Server ile bağlantı kurulamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            holder.b_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String match_ID;
                    if (match.getSender_UID().compareTo(match.getReceiver_UID()) > 0){
                        match_ID = match.getSender_UID() + "+" + match.getReceiver_UID();
                    }
                    else{
                        match_ID = match.getReceiver_UID() + "+" + match.getSender_UID();
                    }
                    databaseReference.child("matches").child(match_ID).child("status").setValue("rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Eşleşme isteği reddedildi.", Toast.LENGTH_SHORT).show();
                                matches.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(context, "Server ile bağlantı kurulamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            holder.b_withdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String match_ID;
                    if (match.getSender_UID().compareTo(match.getReceiver_UID()) > 0){
                        match_ID = match.getSender_UID() + "+" + match.getReceiver_UID();
                    }
                    else{
                        match_ID = match.getReceiver_UID() + "+" + match.getSender_UID();
                    }
                    databaseReference.child("matches").child(match_ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context, "Eşleşme isteği başarıyla geri çekildi.", Toast.LENGTH_SHORT).show();
                                matches.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });


            if (match.getReceiver_UID().equals(this.user.getUid())){
                //görüntüleyen kullanıcı receiver
                fill_item(sender, holder);
                holder.b_withdraw.setEnabled(false);
                holder.b_withdraw.setVisibility(View.GONE);



            }
            else if (match.getSender_UID().equals(this.user.getUid())){
                //görüntüleyen kullanıcı sender
                fill_item(receiver, holder);
                holder.b_accept.setEnabled(false);
                holder.b_accept.setVisibility(View.GONE);
                holder.b_reject.setEnabled(false);
                holder.b_reject.setVisibility(View.GONE);
            }
            else{
                holder.itemView.setVisibility(View.GONE);
            }
        }

    }

    public User find_user_by_id(String id){
        for (User u : this.users){
            if (u.getUser_ID().equals(id)){
                return u;
            }
        }
        return null;
    }

    public void fill_item(User user, Holder holder){
        String full_name = user.getName() + " " + user.getSurname();
        String distance_text = "Mesafe: " + user.getDistance_to_campus() + " dk";
        String duration_text = "Süre: " + user.getDuration() + " ay";
        String year_class_text = user.getYear() + ". Sınıf";

        holder.name_surname.setText(full_name);
        holder.status.setText(user.getStatus());
        holder.department.setText(user.getDepartment());
        holder.year_class.setText(year_class_text);
        holder.distance_to_campus.setText(distance_text);
        holder.duration.setText(duration_text);
        holder.mail.setText(user.getEmail());
        holder.phone.setText(user.getPhone_no());

        if(user.getPhoto() != null && !user.getPhoto().isEmpty()){
            Picasso.with(context.getApplicationContext()).load(user.getPhoto()).into(holder.profile_pic);
        }

    }

    @Override
    public int getItemCount() { return matches.size(); }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(context).inflate(R.layout.match_item, parent, false);
        return new Holder(v);
    }


}

package com.example.match_app.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.match_app.R;
import com.example.match_app.activities.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.Holder>  implements Filterable {

    Context context;
    ArrayList<User> users;
    ArrayList<User> users_full;

    public static class Holder extends RecyclerView.ViewHolder{
        TextView name_surname, status, department, distance_to_campus, year_class, duration;
        ImageView profile_pic;


        public Holder(@NonNull View itemView) {
            super(itemView);

            name_surname = itemView.findViewById(R.id.item_name_surname);
            status = itemView.findViewById(R.id.item_status);
            department = itemView.findViewById(R.id.item_department);
            distance_to_campus = itemView.findViewById(R.id.item_distance_to_campus);
            year_class = itemView.findViewById(R.id.item_year_class);
            duration = itemView.findViewById(R.id.item_duration);
            profile_pic = itemView.findViewById(R.id.item_profilepic);
        }
    }

    public UserViewAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        User user = users.get(position);
        String full_name = user.getName() + " " + user.getSurname();
        holder.name_surname.setText(full_name);
        holder.status.setText(user.getStatus());
        holder.department.setText(user.getDepartment());
        String year_class_text = user.getYear() + ". Sınıf";
        holder.year_class.setText(year_class_text);

        if (!user.getStatus().equals("Aramıyor")){
            String distance_text = "Mesafe: " + user.getDistance_to_campus() + " dk";
            String duration_text = "Süre: " + user.getDuration() + " ay";
            holder.distance_to_campus.setText(distance_text);
            holder.duration.setText(duration_text);
        }
        else{
            holder.distance_to_campus.setText("");
            holder.duration.setText("");
        }

        if(user.getPhoto() != null && !user.getPhoto().isEmpty()){
            Picasso.with(context.getApplicationContext()).load(user.getPhoto()).into(holder.profile_pic);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userID", user.getUser_ID());
                Intent intent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                intent.putExtras(b);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return filter_users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        users_full = new ArrayList<>(users);
        return new Holder(v);
    }

    private Filter filter_users = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filtered_users = new ArrayList<>();
            if (constraint == null ||constraint.length() == 0){
                filtered_users.addAll(users_full);
            }
            else{
                int filter_distance = Integer.parseInt(constraint.toString().trim());
                for (User u : users_full){
                    if(u.getDistance_to_campus() < filter_distance){
                        filtered_users.add(u);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered_users;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

}

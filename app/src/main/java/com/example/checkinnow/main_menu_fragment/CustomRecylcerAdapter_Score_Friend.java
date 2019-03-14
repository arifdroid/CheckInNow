package com.example.checkinnow.main_menu_fragment;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.checkinnow.Employee;
import com.example.checkinnow.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class CustomRecylcerAdapter_Score_Friend extends RecyclerView.Adapter<CustomRecylcerAdapter_Score_Friend.InsideHolder> {

    private Context mContext;
    private ArrayList<Employee> employees;

    public CustomRecylcerAdapter_Score_Friend(Context context, ArrayList<Employee> employees) {

        this.mContext = context;
        this.employees=employees;

    }

    @NonNull
    @Override
    public InsideHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_card_score_friend,viewGroup,false);


        return new InsideHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InsideHolder insideHolder, int i) {

        insideHolder.textView.setText(employees.get(i).getName());

        if(employees.get(i).getImageurl()!=null ||employees.get(i).getImageurl()!="") {
            insideHolder.circleImageView.setImageURI(Uri.parse(employees.get(i).getImageurl()));
        }

        insideHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class InsideHolder extends RecyclerView.ViewHolder{

        public RatingBar ratingBar;
        public TextView textView;
        public CircleImageView circleImageView;

        public InsideHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.custom_scoreFriend_circleImageViewID);
            textView = itemView.findViewById(R.id.custom_scoreFriend_textViewID);
            ratingBar = itemView.findViewById(R.id.custom_scoreFriend_RatingbarID);
        }
    }
}

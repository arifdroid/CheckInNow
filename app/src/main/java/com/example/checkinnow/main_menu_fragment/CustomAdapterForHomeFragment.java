package com.example.checkinnow.main_menu_fragment;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.checkinnow.Employee;
import com.example.checkinnow.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapterForHomeFragment extends RecyclerView.Adapter<CustomAdapterForHomeFragment.InsideHolder> {

    private ArrayList<Employee> listReceived;

    public CustomAdapterForHomeFragment(ArrayList<Employee> listPass) {

        this.listReceived = listPass;
    }

    @NonNull
    @Override
    public InsideHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       // return null;

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_rating_sqlite_home_fragment,viewGroup,false);

        return new InsideHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InsideHolder insideHolder, int i) {
        final int j =i;
        insideHolder.textViewHere.setText(listReceived.get(i).getName());
        insideHolder.circleImageViewHere.setImageURI(Uri.parse(listReceived.get(i).getImageurl()));
        insideHolder.ratingBarHere.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if(fromUser==true){

                        listReceived.get(j).setRating(ratingBar.getRating());

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return listReceived.size();
    }

    public class InsideHolder extends RecyclerView.ViewHolder{

        public RatingBar ratingBarHere;
        public TextView textViewHere;
        public CircleImageView circleImageViewHere;

        public InsideHolder(@NonNull View itemView) {
            super(itemView);

            ratingBarHere = itemView.findViewById(R.id.ratingBarHereID);
            textViewHere = itemView.findViewById(R.id.textviewHereID);
            circleImageViewHere = itemView.findViewById(R.id.circleImageViewHereID);

        }
    }
}

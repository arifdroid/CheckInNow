package com.example.checkinnow.main_menu_fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.checkinnow.Employee;
import com.example.checkinnow.PassingResultInterface;
import com.example.checkinnow.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapterForHomeFragment extends RecyclerView.Adapter<CustomAdapterForHomeFragment.InsideHolder> {

    private ArrayList<Employee> listReceived;

    private Cursor cursor;

    private Context mContext;

    //setup our interface instance, to get callback for rating.

    private PassingResultInterface passingResultInterface;

    public void setPassingResultInterface(PassingResultInterface passingResultInterface){

        this.passingResultInterface = passingResultInterface;
    }

    public CustomAdapterForHomeFragment(ArrayList<Employee> listPass, Context context, Cursor cursor) {

        this.mContext = context;
        this.cursor=cursor;
        this.listReceived = listPass;
    }

    @NonNull
    @Override
    public InsideHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       // return null;
        Log.i("check fragment :"," 77 oncreate adapter");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_rating_sqlite_home_fragment,viewGroup,false);

        return new InsideHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InsideHolder insideHolder, int i) {

        if(!cursor.move(i)){

            return;         //to make sure cursor existing.
        }


        final int j =i;

        Log.i("check fragment :"," 88 adapter");
        insideHolder.textViewHere.setText(listReceived.get(i).getName());
        insideHolder.circleImageViewHere.setImageURI(Uri.parse(listReceived.get(i).getImageurl()));
        insideHolder.ratingBarHere.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if(fromUser==true){

                        listReceived.get(j).setRating(ratingBar.getRating());


//
                    if(passingResultInterface!=null){

                        passingResultInterface.passingArray(listReceived);

                    }
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

            Log.i("check fragment :"," 66 inside holder adapter");

            ratingBarHere = itemView.findViewById(R.id.ratingBarHereID);
            textViewHere = itemView.findViewById(R.id.textviewHereID);
            circleImageViewHere = itemView.findViewById(R.id.circleImageViewHereID);

        }
    }
}

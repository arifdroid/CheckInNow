package com.example.checkinnow.main_menu_fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.checkinnow.Employee;
import com.example.checkinnow.R;

import java.util.ArrayList;

public class Score_Friend_Fragment extends Fragment {

    //recyler view without data instantiate

    private RecyclerView recyclerView;

    private CustomRecylcerAdapter_Score_Friend adapter_score_friend;

    private ArrayList<Employee> returnedList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notification_fragment,container,false);

        recyclerView = view.findViewById(R.id.recyler_noti_fragmentID);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //we are sending zero, then read from sqlite
        adapter_score_friend = new CustomRecylcerAdapter_Score_Friend(getActivity(),new ArrayList<Employee>());

        //here we need asynctask to load from sqlite

        AsyncHelper_LoadSQLite help =new AsyncHelper_LoadSQLite(getActivity(), new OnCustomListener() {
            @Override
            public void onSuccess(ArrayList<Employee> employeeArrayList) {

                returnedList = employeeArrayList;
                //here notify data change, pass back completed list, update adapter
                adapter_score_friend = new CustomRecylcerAdapter_Score_Friend(getActivity(), returnedList);

                adapter_score_friend.notifyDataSetChanged();

                recyclerView.setAdapter(adapter_score_friend);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });


        recyclerView.setAdapter(adapter_score_friend);


        return view;


    }
}

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
import com.example.checkinnow.sqlite_creation.FS_to_SQLite_DBHelper;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    private RecyclerView.Adapter recyclerAdapter;

    private boolean doneLoadFromSQLite;

    private ArrayList<Employee> listReturnFromSQLite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        listReturnFromSQLite = new ArrayList<>();
        doneLoadFromSQLite=false;

        getSQLiteData();

        while(doneLoadFromSQLite==false) {


        }

        recyclerView = container.findViewById(R.id.fragment_Score_recyclerID);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        recyclerAdapter = new CustomAdapterForHomeFragment(listReturnFromSQLite);

        recyclerView.setAdapter(recyclerAdapter);



        return inflater.inflate(R.layout.home_fragment,container,false);
    }

    private ArrayList<Employee> getSQLiteData() {

        FS_to_SQLite_DBHelper get = FS_to_SQLite_DBHelper.getInstance(getContext());

        listReturnFromSQLite = get.getAll_FSToSQLite();

        if(listReturnFromSQLite!=null) {

            doneLoadFromSQLite=true;

            return listReturnFromSQLite;

        }else {
            return null;
        }
    }
}

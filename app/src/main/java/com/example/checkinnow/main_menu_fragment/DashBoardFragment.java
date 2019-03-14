package com.example.checkinnow.main_menu_fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.checkinnow.R;

import java.util.ArrayList;

public class DashBoardFragment extends Fragment {

    private ListView listView;

    private ArrayList<String> insertArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard_fragment, container,false);
        insertArray = new ArrayList<>();

        insertArray.add("hello");
        insertArray.add("progress");
        insertArray.add("you are");
        insertArray.add("CRUSHING IT");


        listView = rootView.findViewById(R.id.list_dashboardFragID);

        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,insertArray);

        listView.setAdapter(adapter);

        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);


    }
}

package com.example.checkinnow.main_menu_fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.checkinnow.Employee;
import com.example.checkinnow.PassingResultInterface;
import com.example.checkinnow.R;
import com.example.checkinnow.sqlite_creation.FS_to_SQLite_DBHelper;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    private RecyclerView.Adapter recyclerAdapter;

    private boolean doneLoadFromSQLite;

    private Button testButton;

    private ArrayList<Employee> listReturnFromSQLite;

    private ArrayList<Employee> setToSendList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        Log.i("check fragment :"," 000 oncreate java home fragment");

        listReturnFromSQLite = new ArrayList<>();
        setToSendList = new ArrayList<>();

        testButton = container.findViewById(R.id.home_frag_buttonID);
        doneLoadFromSQLite=false;

        getSQLiteData();

        while(doneLoadFromSQLite==false) {


        }

        Log.i("check fragment :"," 111 after donload java home fragment");

        recyclerView = container.findViewById(R.id.fragment_Score_recyclerID);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


       // recyclerAdapter = new CustomAdapterForHomeFragment(listReturnFromSQLite);


        recyclerView.setAdapter(recyclerAdapter);

        Log.i("check fragment :"," 222 after set adapter java home fragment");

        ((CustomAdapterForHomeFragment) recyclerAdapter).setPassingResultInterface(new PassingResultInterface() {
            @Override
            public void passingArray(ArrayList<Employee> list) {

                Log.i("check fragment :"," 9999 callback result list java home fragment");

                setToSendList = list;



            }
        });


        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int j=0;j<setToSendList.size();j++) {

                    Log.i("checkk finally: ", j+" name"+ setToSendList.get(j).getName() + " rating: "+ setToSendList.get(j).getRating());
                }

            }
        });



        return inflater.inflate(R.layout.home_fragment,container,false);
    }

    private ArrayList<Employee> getSQLiteData() {

        FS_to_SQLite_DBHelper get = FS_to_SQLite_DBHelper.getInstance(getContext());

        Log.i("check fragment :"," 444 before get from sqlite adapter java home fragment");


        listReturnFromSQLite = get.getAll_FSToSQLite();

        if(listReturnFromSQLite!=null) {

            doneLoadFromSQLite=true;
            Log.i("check fragment :"," 555 after sqlite adapter java home fragment");

            return listReturnFromSQLite;

        }else {
            return null;
        }
    }
}

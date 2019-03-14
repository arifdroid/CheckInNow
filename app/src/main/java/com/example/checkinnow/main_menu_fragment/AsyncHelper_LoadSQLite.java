package com.example.checkinnow.main_menu_fragment;

import android.content.Context;
import android.os.AsyncTask;

import com.example.checkinnow.Employee;
import com.example.checkinnow.sqlite_creation.FS_to_SQLite_DBHelper;

import java.util.ArrayList;

public class AsyncHelper_LoadSQLite extends AsyncTask<Void,Void, ArrayList<Employee>> {

    //setting up listener to return result

    private OnCustomListener listener;

    private Context mContext;

    public Exception exception;

    //constructor to load
    public AsyncHelper_LoadSQLite(Context context, OnCustomListener onCustomListener){
        this.mContext =context;
        this.listener = onCustomListener;
    }


    @Override
    protected ArrayList<Employee> doInBackground(Void... voids) {
        // our job here to load only, dont write anything, write in other segment

        FS_to_SQLite_DBHelper object = FS_to_SQLite_DBHelper.getInstance(mContext);
        //object.getAll_FSToSQLite();

        //
        return new ArrayList<>(object.getAll_FSToSQLite());


    }

    @Override
    protected void onPostExecute(ArrayList<Employee> employees) {
        // this will get data from sqlite

        if(listener!=null){

            if(exception==null){

                listener.onSuccess(employees);
            }else {

                listener.onFailure(exception);
            }
        }
    }
}

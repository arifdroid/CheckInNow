package com.example.checkinnow.main_menu_fragment;

import com.example.checkinnow.Employee;

import java.util.ArrayList;

public interface OnCustomListener {

    void onSuccess(ArrayList<Employee> employeeArrayList);

    void onFailure(Exception e);
}

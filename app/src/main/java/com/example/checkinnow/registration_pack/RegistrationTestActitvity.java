package com.example.checkinnow.registration_pack;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkinnow.Employee;
import com.example.checkinnow.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RegistrationTestActitvity extends AppCompatActivity {

    private EditText editTextNameUser, editTextPhoneUser, editTextCode;
    private Button buttonLogIn, buttonGetCode;
    private TextView textViewMessage;

    //global here constant
    private String codeFromFirebase;
    private String userNameGlobal;
    private String userPhoneGlobal;

    private String codeEnteredbyUser;

    private String whichAdminEmployeeGlobal;

    //for phone auth provider callback
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    //admin constant

    private String adminName;
    private String adminPhone;

    //boolean found which admin correspond, create document
    private boolean foundWhichAdmin;

    //loading all employees in one admin, for sqlite creation

    private ArrayList<Employee> employeeArrayListLoaded;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_test_actitvity);

        whichAdminEmployeeGlobal ="";
        codeFromFirebase="";
        foundWhichAdmin=false;
        employeeArrayListLoaded = new ArrayList<>();

        editTextNameUser = findViewById(R.id.testNameUserID);
        editTextPhoneUser = findViewById(R.id.testPhoneUserID);
        editTextCode = findViewById(R.id.testCodeID);

        buttonGetCode = findViewById(R.id.test_button2);
        buttonLogIn = findViewById(R.id.test_button);

        textViewMessage = findViewById(R.id.test_textView);

        Intent intent =getIntent();

        adminName = intent.getStringExtra("admin_name");
        adminPhone = intent.getStringExtra("admin_phone");

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userphone = editTextPhoneUser.getText().toString();
                String username = editTextNameUser.getText().toString();

                userNameGlobal=username;
                userPhoneGlobal=userphone;

                verifyUserFromAdmin();
            }
        });


    }

    private void verifyUserFromAdmin() {


        final CollectionReference cR_ifRegistered = FirebaseFirestore.getInstance().collection("admins_offices");
        Query query_ifRegistered = cR_ifRegistered.whereArrayContains("employee_this_admin",userPhoneGlobal);


        query_ifRegistered.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    QuerySnapshot querySnapshot = task.getResult();

                    int k = querySnapshot.size();

                    if(querySnapshot.size()>=1) {
                        //here we must check admin name, so pass admin data from before
                        //using intent, admin name, and admin phone.

                        //remap document snapshot

                        Log.i("checkk 6", "user registered, get admin details");

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {


                            Map<String, Object> map;
                            map = documentSnapshot.getData();

                            //to store to refer to our structure later
                            String adminNameHere = "";
                            String adminPhonehere = "";
                            String adminEmployeeDocUID = "";

                            //admin must have different name, can have same number, but must have different name
                            String whichAdmin = adminName + adminPhone;

                            //here we pull value and field from each document.
                            for (Map.Entry<String, Object> remap : map.entrySet()) {

                                //problem if they its mapped,

                                if (remap.getKey().equals("admin_name")) {
                                    adminNameHere = remap.getValue().toString();
                                }

                                if (remap.getKey().equals("admin_phone")) {
                                    adminPhonehere = remap.getValue().toString();
                                }

                                if (remap.getKey().equals("uid")) {
                                    adminEmployeeDocUID = remap.getValue().toString();
                                }

                                String whichAdminV2 = adminNameHere+adminPhonehere;

                                if (((adminNameHere + adminPhonehere).equals(whichAdmin))) { //must make sure all field has
                                    //means we found which document

                                    //so only proceed if we here, problem is we dont process else.

                                    //which admin global will be used as admin document in empoyee side,
                                    //so need extra UID
                                    if(!adminEmployeeDocUID.equals(null)||!adminEmployeeDocUID.equals("")){
                                    Log.i("checkk 7", "check admin here, user here");
                                    whichAdminEmployeeGlobal = whichAdmin + adminEmployeeDocUID;
                                    //should be         >>    arif+60175555555(uid)
                                    foundWhichAdmin = true;
                                    }
                                }
                            }


                        }

                    }else {

                        textViewMessage.setText("check admin details");

                        FirebaseAuth.getInstance().signOut();

                        Toast.makeText(RegistrationTestActitvity.this,"please contact your admin",
                                Toast.LENGTH_LONG).show();
                    }


                    if(foundWhichAdmin==true){

                        Log.i("checkk 6", "admin found, now get user doc");
                        //now process to find if document for this user, for this admin, is existed.
                        //if not existed, create,
                        //else please contact admin.
                        if(!whichAdminEmployeeGlobal.equals(null)|| !whichAdminEmployeeGlobal.equals("")) {
                            final CollectionReference cR_uid_employee_this = FirebaseFirestore.getInstance().collection("employees_to_offices")
                                    .document(whichAdminEmployeeGlobal).collection("uid_employee_this");

                            Query query_CheckDoc = cR_uid_employee_this.whereEqualTo("phone",userPhoneGlobal);

                            query_CheckDoc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if(task.isSuccessful()){

                                        QuerySnapshot querySnapshot =task.getResult();

                                        if(querySnapshot.size()==1){

                                            FirebaseAuth.getInstance().signOut();

                                            Toast.makeText(RegistrationTestActitvity.this,"already registered? please contact your admin",
                                                    Toast.LENGTH_LONG).show();


                                        }else if(querySnapshot.size()>1){
                                            //means error, user has 2 or more documents in same admin
                                            FirebaseAuth.getInstance().signOut();

                                            Toast.makeText(RegistrationTestActitvity.this,"please contact your admin",
                                                    Toast.LENGTH_LONG).show();
                                        }else {

                                            //zero document found, so create new doc
                                            //here we can create document for first time user.

                                            //score card reference also saved in document
                                            String docName = "card_here"+userPhoneGlobal;

                                            Employee this_user_employee = new Employee(userNameGlobal,userPhoneGlobal,docName);

                                            cR_uid_employee_this.document(userPhoneGlobal).set(this_user_employee);

                                            //cant setup sqLite here since, maybe not all data loaded just yet.

                                            //setup ref collection

                                            CollectionReference cR_score_card = FirebaseFirestore.getInstance().collection("employees_to_offices")
                                                    .document(whichAdminEmployeeGlobal).collection("score_ref_collection");



                                            Employee user_score_card = new Employee(userNameGlobal,userPhoneGlobal,docName,0.0f);

                                            cR_score_card.document(docName).set(user_score_card);

                                            settingupSQLiteTable();

                                            Toast.makeText(RegistrationTestActitvity.this,"success registration",
                                                    Toast.LENGTH_LONG).show();


                                        }





                                    }else {



                                    }



                                }
                            });


                        }else {

                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(RegistrationTestActitvity.this,"please contact your admin",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                    //else, return please register again.

                    else {

                        Log.i("checkk 99", "not found admin");

                        textViewMessage.setText("user not registered");

                        FirebaseAuth.getInstance().signOut();

                        Toast.makeText(RegistrationTestActitvity.this,"please contact your admin",
                                Toast.LENGTH_LONG).show();


                    }



                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {

                FirebaseAuth.getInstance().signOut();

                Toast.makeText(RegistrationTestActitvity.this,"please contact your admin",
                        Toast.LENGTH_LONG).show();

            }
        });



        //here we process after found which admin,



    }

    private void settingupSQLiteTable() {

        //creating in background.


        //first need to download all other employee details first.
        final CollectionReference cR_gettingRef = FirebaseFirestore.getInstance().collection("employees_to_offices")
                .document(whichAdminEmployeeGlobal).collection("uid_employee_this");

        cR_gettingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){

                    QuerySnapshot documentSnapshots = task.getResult();

                    //cheking purposes
                    int j = documentSnapshots.size();
                    Log.i("checkk ", "number of employee document : "+j);

                    for(QueryDocumentSnapshot snapshots:documentSnapshots){

                        Map<String, Object> map;
                        map = snapshots.getData();

                        //we want to extract other employee,
                        //name, phone,reference, image

                        String name_coworker="";
                        String phone_coworker="";
                        String image_url = "";
                        String ref_score_card = "";

                        for(Map.Entry<String,Object> remap : map.entrySet()){

                            if(remap.getKey().equals("name")){

                                name_coworker = remap.getValue().toString();
                            }


                            if(remap.getKey().equals("phone")){

                                phone_coworker = remap.getValue().toString();
                            }


                            if(remap.getKey().equals("imageurl")){

                                image_url = remap.getValue().toString();
                            }


                            if(remap.getKey().equals("ref_score_card")){

                                ref_score_card = remap.getValue().toString();
                            }

                            if(!name_coworker.equals("")&&!phone_coworker.equals("")&&!image_url.equals("")
                                    && !ref_score_card.equals(""))  {

                                employeeArrayListLoaded.add(new Employee(name_coworker,phone_coworker,ref_score_card));

                            }

                        }



                    } //finish load all data.








                }else { //somehow task is failed, we must load again to create sqlite.


                }

            }
        });











}
}

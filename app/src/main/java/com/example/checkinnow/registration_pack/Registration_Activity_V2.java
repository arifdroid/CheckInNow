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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Registration_Activity_V2 extends AppCompatActivity {

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
        setContentView(R.layout.activity_registration___v2);

        employeeArrayListLoaded = new ArrayList<>();

        FirebaseApp.initializeApp(Registration_Activity_V2.this);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        //sign out any user if any, to start register
        if(user!=null){
            FirebaseAuth.getInstance().signOut();
        }


        whichAdminEmployeeGlobal ="";
        codeFromFirebase="";
        foundWhichAdmin=false;

        editTextNameUser = findViewById(R.id.registrationV2_editText_userNameID);
        editTextPhoneUser = findViewById(R.id.registrationV2_editText_userPhoneID);
        editTextCode = findViewById(R.id.registrationV2_editText_CodeID);

        buttonGetCode = findViewById(R.id.registrationV2_buttonGetCodeID);
        buttonLogIn = findViewById(R.id.registrationV2_buttonSignInID);

        textViewMessage = findViewById(R.id.registrationV2_textViewMessageID);

        Intent intent =getIntent();

        adminName = intent.getStringExtra("admin_name");
        adminPhone = intent.getStringExtra("admin_phone");

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                textViewMessage.setText("logging in automatically");
                Log.i("checkk 1", "check verification");
                gettingVerification(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                textViewMessage.setText("verification failed");
                Log.i("checkk 2", "verification failed");
                Toast.makeText(Registration_Activity_V2.this,e.getMessage(),Toast.LENGTH_LONG ).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                codeFromFirebase =s;

                if(codeFromFirebase!=null||codeFromFirebase!=""){

                    Log.i("checkk 3", "code firebase received");

                    textViewMessage.setText("please enter code and log in");
                }
            }
        };

        buttonGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name_user = editTextNameUser.getText().toString();
                String phone_user = editTextPhoneUser.getText().toString();
                userNameGlobal = name_user; //just check again, in case user change after got code.
                userPhoneGlobal = phone_user;
                if((userNameGlobal!=null && userPhoneGlobal!=null)||(userNameGlobal!=""&& userPhoneGlobal!="")||
                        (userNameGlobal!=null&& userNameGlobal!="")||(userNameGlobal!="" && userPhoneGlobal!=null)) {


                    Log.i("checkk 4", "get callback");
                   getCallback(userPhoneGlobal);

                }
            }
        });

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                codeEnteredbyUser = editTextCode.getText().toString();

                String name_user = editTextNameUser.getText().toString();
                String phone_user = editTextPhoneUser.getText().toString();

                if((name_user!=null && phone_user!=null )||(name_user!=""&& phone_user!="" )||
                        (name_user!=null&& name_user!="")||(name_user!="" && phone_user!=null)){

                    //set to global make it easy

                    if(codeEnteredbyUser!=null||codeEnteredbyUser!="") {
                        userNameGlobal = name_user; //just check again, in case user change after got code.
                        userPhoneGlobal = phone_user;

                        Log.i("checkk 4", "get credential");
                        Log.i("checkk 4", codeFromFirebase + " : "+ codeEnteredbyUser);
                        checkCredential(codeEnteredbyUser, codeFromFirebase);
                    }else {

                        textViewMessage.setText("please enter code");
                    }

                }else {
                    textViewMessage.setText("please enter admin number and name");
                }
            }
        });

    }

    private void checkCredential(String codeEnteredbyUser, String codeFromFirebase) {

       textViewMessage.setText("getting credential..");
       PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeEnteredbyUser,codeFromFirebase);
       gettingVerification(credential);

    }


    private void getCallback(String userPhoneGlobal) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(userPhoneGlobal,
                60,
                TimeUnit.SECONDS,
                Registration_Activity_V2.this,
                mCallback);

    }

    private void gettingVerification(PhoneAuthCredential phoneAuthCredential) {

        // if task succesful, continue to check if user is registered by admin.



        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Log.i("checkk 5", "authenticateed, now check database");

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    final CollectionReference cR_ifRegistered = FirebaseFirestore.getInstance().collection("admins_offices");

                    Query query_ifRegistered = cR_ifRegistered.whereArrayContains("employee_this_admin",user.getPhoneNumber());

                    //this might return more than one document, means the user registered to more than two admin.

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

                                            if (((adminNameHere + adminPhonehere) == whichAdmin) && (adminEmployeeDocUID != null || adminEmployeeDocUID != "")) { //must make sure all field has
                                                //means we found which document

                                                //so only proceed if we here, problem is we dont process else.

                                                //which admin global will be used as admin document in empoyee side,
                                                //so need extra UID
                                                Log.i("checkk 7", "check admin here, user here");
                                                whichAdminEmployeeGlobal = whichAdmin + adminEmployeeDocUID;
                                                //should be         >>    arif+60175555555(uid)
                                                foundWhichAdmin = true;
                                            }
                                        }


                                    }

                                }   else {

                                    textViewMessage.setText("check admin details");

                                    FirebaseAuth.getInstance().signOut();

                                    Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                            Toast.LENGTH_LONG).show();
                                }


                            }
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                    Toast.LENGTH_LONG).show();

                        }
                    });



                    //here we process after found which admin,

                    if(foundWhichAdmin==true){

                        Log.i("checkk 6", "admin found, now get user doc");
                        //now process to find if document for this user, for this admin, is existed.
                        //if not existed, create,
                        //else please contact admin.
                        if(whichAdminEmployeeGlobal !=null|| whichAdminEmployeeGlobal !="") {
                            final CollectionReference cR_uid_employee_this = FirebaseFirestore.getInstance().collection("employees_to_offices")
                                    .document(whichAdminEmployeeGlobal).collection("uid_employee_this");

                            Query query_CheckDoc = cR_uid_employee_this.whereEqualTo("phone",user.getPhoneNumber());

                            query_CheckDoc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if(task.isSuccessful()){

                                        QuerySnapshot querySnapshot =task.getResult();

                                        if(querySnapshot.size()==1){

                                            FirebaseAuth.getInstance().signOut();

                                            Toast.makeText(Registration_Activity_V2.this,"already registered? please contact your admin",
                                                    Toast.LENGTH_LONG).show();


                                        }else if(querySnapshot.size()>1){
                                            //means error, user has 2 or more documents in same admin
                                            FirebaseAuth.getInstance().signOut();

                                            Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
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

                                            Toast.makeText(Registration_Activity_V2.this,"success registration",
                                                    Toast.LENGTH_LONG).show();


                                        }





                                    }else {



                                    }



                                }
                            });





                        }else {

                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                    //else, return please register again.

                    else {

                        Log.i("checkk 99", "not found admin");

                        textViewMessage.setText("user not registered");

                        FirebaseAuth.getInstance().signOut();

                        Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                Toast.LENGTH_LONG).show();


                    }






                }else {


                textViewMessage.setText("please try again..");
                //task is not succesful
            }


            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    private void settingupSQLiteTable() { //continue running even task finish.
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

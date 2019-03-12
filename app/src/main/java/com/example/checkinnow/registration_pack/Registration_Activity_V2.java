package com.example.checkinnow.registration_pack;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkinnow.Employee;
import com.example.checkinnow.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration___v2);

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
                gettingVerification(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                textViewMessage.setText("verification failed");
                Toast.makeText(Registration_Activity_V2.this,e.getMessage(),Toast.LENGTH_LONG ).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                codeFromFirebase =s;

                if(codeFromFirebase!=null||codeFromFirebase!=""){

                    textViewMessage.setText("please enter code and log in");
                }
            }
        };

        buttonGetCode.setOnClickListener(new View.OnClickListener() {
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


                        checkCredential(codeEnteredbyUser, codeFromFirebase);
                    }else {

                        textViewMessage.setText("please enter code");
                    }

                }else {
                    textViewMessage.setText("please enter admin number and name");
                }
            }
        });

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if((userNameGlobal!=null && userPhoneGlobal!=null)||(userNameGlobal!=""&& userPhoneGlobal!="")||
                        (userNameGlobal!=null&& userNameGlobal!="")||(userNameGlobal!="" && userPhoneGlobal!=null)) {

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

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    final CollectionReference cR_ifRegistered = FirebaseFirestore.getInstance().collection("admins_offices");

                    Query query_ifRegistered = cR_ifRegistered.whereArrayContains("employee_this_admin",user.getPhoneNumber());

                    //this might return more than one document, means the user registered to more than two admin.

                    query_ifRegistered.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            //means document exist, user is registered with admin
                            if(queryDocumentSnapshots.size()>=1){

                                //here we must check admin name, so pass admin data from before
                                //using intent, admin name, and admin phone.

                                //remap document snapshot



                                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){


                                    Map <String,Object> map ;
                                    map = documentSnapshot.getData();

                                    //to store to refer to our structure later
                                    String adminNameHere="";
                                    String adminPhonehere="";
                                    String adminEmployeeDocUID ="";

                                    //admin must have different name, can have same number, but must have different name
                                    String whichAdmin = adminName+adminPhone;

                                    //here we pull value and field from each document.
                                    for (Map.Entry<String,Object> remap: map.entrySet()){

                                            //problem if they its mapped,

                                            if(remap.getKey().equals("admin_name")){
                                                adminNameHere=remap.getValue().toString();
                                            }

                                            if(remap.getKey().equals("admin_phone")){
                                                adminPhonehere=remap.getValue().toString();
                                            }

                                            if(remap.getKey().equals("admin_employee_doc_uid")){
                                                adminEmployeeDocUID=remap.getValue().toString();
                                            }

                                            if(((adminNameHere+adminPhonehere)==whichAdmin)&& (adminEmployeeDocUID!=null||adminEmployeeDocUID!="")){ //must make sure all field has
                                                //means we found which document

                                                //so only proceed if we here, problem is we dont process else.

                                                //which admin global will be used as admin document in empoyee side,
                                                //so need extra UID

                                                whichAdminEmployeeGlobal =whichAdmin+adminEmployeeDocUID;
                                                foundWhichAdmin=true;
                                            }
                                    }




                                }

                            }else {

                                textViewMessage.setText("user not registered");

                                FirebaseAuth.getInstance().signOut();

                                Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                        Toast.LENGTH_LONG).show();
                            }




                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                    Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                    Toast.LENGTH_LONG).show();

                        }
                    });

                    //here we process after found which admin,

                    if(foundWhichAdmin==true){

                        //now process to find if document for this user, for this admin, is existed.
                        //if not existed, create,
                        //else please contact admin.
                        if(whichAdminEmployeeGlobal !=null|| whichAdminEmployeeGlobal !="") {
                            final CollectionReference cR_uid_employee_this = FirebaseFirestore.getInstance().collection("employees_to_offices")
                                    .document(whichAdminEmployeeGlobal).collection("uid_employee_this");

                            Query query_CheckDoc = cR_uid_employee_this.whereEqualTo("phone",user.getPhoneNumber());

                            query_CheckDoc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    //this is exist, else, must be error, cannot have two document of same employee in same admin
                                    if(queryDocumentSnapshots.size()==1){

                                        FirebaseAuth.getInstance().signOut();

                                        Toast.makeText(Registration_Activity_V2.this,"already registered? please contact your admin",
                                                Toast.LENGTH_LONG).show();


                                    }else if(queryDocumentSnapshots.size()>1){
                                          //means error, user has 2 or more documents in same admin
                                        FirebaseAuth.getInstance().signOut();

                                        Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                                Toast.LENGTH_LONG).show();
                                    }else {

                                        //here we can create document for first time user.


                                        Employee this_user_employee = new Employee(userNameGlobal,userPhoneGlobal);

                                        cR_uid_employee_this.document(userPhoneGlobal).set(this_user_employee);

                                        Toast.makeText(Registration_Activity_V2.this,"success registration",
                                                Toast.LENGTH_LONG).show();

                                        
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {

                                }
                            });


                        }

                    }
                    //else, return please register again.

                    else {

                        textViewMessage.setText("user not registered");

                        FirebaseAuth.getInstance().signOut();

                        Toast.makeText(Registration_Activity_V2.this,"please contact your admin",
                                Toast.LENGTH_LONG).show();


                    }






                }else {


                textViewMessage.setText("please try again..");
            }




            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {

            }
        });


    }

}

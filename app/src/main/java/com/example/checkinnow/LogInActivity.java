package com.example.checkinnow;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {

    private TextView textViewMessage,textViewRegister;

    private Button button_SignIn, button_GetCode;

    private EditText editText_PhoneNumber, editText_Code;

    //code firebase sent to us, set as global
    private String codeFirebaseSent;

    //call back is response from phone auth provider, we request for code
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMessage = findViewById(R.id.logIn_Activity_textViewMessageID);
        textViewRegister = findViewById(R.id.logIn_Activity_textView_RegisterNowID);

        button_GetCode = findViewById(R.id.logIn_Activity_buttonGetCodeID);
        button_SignIn = findViewById(R.id.logIn_Activity_buttonLogInID);

        editText_PhoneNumber = findViewById(R.id.logIn_Activity_editTextPhoneID);
        editText_Code = findViewById(R.id.logIn_Activity_editTextCodeID);

        // here we try to sign, must load data from firestore, check whether this number
        // from authentication page is registered anywhere.

        //load using authentication firebase. >> add dependencies.

        //initialize firebase
        FirebaseApp.initializeApp(this);

        button_GetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //we need to ensure user has put in phone number to click get code

                String phone_here = editText_PhoneNumber.getText().toString();

                if(phone_here!=null || phone_here!="") {
                    getCallback(phone_here);

                }
            }
        });

        button_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if phone number and code is not empty again

                String phone_here=editText_PhoneNumber.getText().toString();
                String code_here = editText_Code.getText().toString();

                //check if firebase sent code as well,

                if((phone_here!=null && code_here!=null && codeFirebaseSent!=null)||
                        (!phone_here.equals("") && !code_here.equals("") && !codeFirebaseSent.equals(""))){
                    checkCredential(code_here,codeFirebaseSent);
                }
            }
        });


        //callback override here

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //here if auto verification in process
                textViewMessage.setText("logging in automatically..");
                verifyCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //here if code sent by firebase manually
                //code firebase sent is s variable, saved to global
                codeFirebaseSent=s;
                textViewMessage.setText("please enter code received..");
                //then we can enter code, and click log in.


            }
        };








    }

    //check credential received.
    private void checkCredential(String code_here, String codeFirebaseSent) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code_here,codeFirebaseSent);
        verifyCredential(credential);

    }

    private void verifyCredential(PhoneAuthCredential phoneAuthCredential) {

        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    //here we got verification to sign, but we need to check wheter customer is
                    //registered by admin or not., this is registered in admin document only, not from employee collection

                    //get the user, from current user, firebase authentication page

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //get the reference to admin collection,
                    final CollectionReference cR_ifRegistered = FirebaseFirestore.getInstance().collection("admins_offices");

                    //check array of field "employee_this_admin", if contain user phone number
                    Query query_ifRegistered = cR_ifRegistered.whereArrayContains("employee_this_admin",user.getPhoneNumber());

                    query_ifRegistered.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            //here we dont have to retrieve the number or, get from which document,
                            //if it is exist, means number of document >=1, //though as this code written, or
                            //the way the data is structured, one phone number can belong to only one admin at a time.

                            //we just check. size of document.

                            if(queryDocumentSnapshots.size()>=1){

                                //then here we can log in
                                logInNow();

                            }else {

                                //return as user is not verified by any admin,
                                //sign him out from firebase authentication page.

                                logOutNow();
                            }





                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //failure to retrieve data, ask user to try again, log in.

                            onStart();


                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            //canceled to retrieve data, ask user to enter again if consent.

                            onStart();


                        }
                    });



                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {

                //cancel getting credential
                onStart();

            }
        });

    }

    private void logOutNow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){

            FirebaseAuth.getInstance().signOut();

        }


    }

    private void logInNow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){

            //move to next activity


        }



    }

    private void getCallback(String phone_nummber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber( phone_nummber,
                60,
                TimeUnit.SECONDS,
                LogInActivity.this,
                mCallback);


    }
}

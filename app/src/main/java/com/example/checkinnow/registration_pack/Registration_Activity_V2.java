package com.example.checkinnow.registration_pack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkinnow.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Registration_Activity_V2 extends AppCompatActivity {

    private EditText editTextNameUser, editTextPhoneUser, editTextCode;
    private Button buttonLogIn, buttonGetCode;
    private TextView textViewMessage;

    //global here constant
    private String codeFromFirebase;
    private String userNameGlobal;
    private String userPhoneGlobal;

    //for phone auth provider callback
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration___v2);

        codeFromFirebase="";

        editTextNameUser = findViewById(R.id.registrationV2_editText_userNameID);
        editTextPhoneUser = findViewById(R.id.registrationV2_editText_userPhoneID);
        editTextCode = findViewById(R.id.registrationV2_editText_CodeID);

        buttonGetCode = findViewById(R.id.registrationV2_buttonGetCodeID);
        buttonLogIn = findViewById(R.id.registrationV2_buttonSignInID);

        textViewMessage = findViewById(R.id.registrationV2_textViewMessageID);


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

                String name_user = editTextNameUser.getText().toString();
                String phone_user = editTextPhoneUser.getText().toString();

                if((name_user!=null && phone_user!=null)||(name_user!=""&& phone_user!="")||
                        (name_user!=null&& name_user!="")||(name_user!="" && phone_user!=null)){

                    //set to global make it easy

                    userNameGlobal = name_user;
                    userPhoneGlobal=phone_user;

                    textViewMessage.setText("getting code..");

                    getCallback(userPhoneGlobal);

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

    private void gettingVerification(PhoneAuthCredential phoneAuthCredential) {


    }

    private void getCallback(String userPhoneGlobal) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(userPhoneGlobal,
                60,
                TimeUnit.SECONDS,
                Registration_Activity_V2.this,
                mCallback);

    }
}

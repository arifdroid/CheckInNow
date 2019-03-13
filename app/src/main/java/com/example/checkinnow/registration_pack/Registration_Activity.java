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

import com.example.checkinnow.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Registration_Activity extends AppCompatActivity {




    //here we check whether admin doc exist, in admins collections

    private EditText editTextAdminPhone, editTextAdminName;
    private TextView textViewMessage;

    private Button buttonCheckAdmin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_);

        editTextAdminName = findViewById(R.id.registration_editText_adminNameID);
        editTextAdminPhone= findViewById(R.id.registration_editText_adminPhoneID);

        textViewMessage = findViewById(R.id.registrationn_textViewMessageID);

        buttonCheckAdmin = findViewById(R.id.registration_button_adminID);




        buttonCheckAdmin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name_admin = editTextAdminName.getText().toString();
                String phone_admin = editTextAdminPhone.getText().toString();

                if((name_admin!=null && phone_admin!=null)||(name_admin!=""&& phone_admin!="")||
                (name_admin!=null&& name_admin!="")||(name_admin!="" && phone_admin!=null)){

                    textViewMessage.setText("checking admin..");

                    checkAdminDetail(name_admin,phone_admin);

                }else {
                    textViewMessage.setText("please enter admin number and name");
                }

            }
        });

    }

    private void checkAdminDetail(final String name_admin, final String phone_admin) {

        //check if admin exist in collection
        final CollectionReference collectionReference =FirebaseFirestore.getInstance().collection("admins_offices");

        Query query_admin = collectionReference.whereEqualTo("admin_phone",phone_admin);

        query_admin.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    QuerySnapshot querySnapshot = task.getResult();

                    if (querySnapshot.size() > 0) {

//                if(queryDocumentSnapshots.size()==0){
//
//                    noAdminDetected();
//
//                }else {

                        //admin exist , log to next activity
                        //we need to pass admin name and phone using
                        //now we need to set name as well, before passing

                        Query query_nameAdmin = collectionReference.whereEqualTo("admin_name", name_admin);

                        query_nameAdmin.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {

                                    QuerySnapshot querySnapshot = task.getResult();

                                    if (querySnapshot.size() == 1) {    //means valid and go next

                                        //Intent intent = new Intent(Registration_Activity.this, Registration_Activity_V2.class);

                                        Intent intent = new Intent(Registration_Activity.this,RegistrationTestActitvity.class);

                                        intent.putExtra("admin_name", name_admin);
                                        intent.putExtra("admin_phone", phone_admin);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        startActivity(intent);

                                    } else if (querySnapshot.size() == 0) {

                                        textViewMessage.setText("admin not exist");
                                        noAdminDetected();

                                    } else {

                                        textViewMessage.setText("please contact admin");
                                    }


                                } else { //task is not succesful

                                    textViewMessage.setText("please try again");

                                }


                            }
                        });


                    }

                    else {

                        noAdminDetected();

                    }

                }else {

                    //task check phone not succesfull
                   noAdminDetected();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    textViewMessage.setText(e.getMessage());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                textViewMessage.setText("please check admin phone number again");
            }
        });



    }

    private void noAdminDetected() {
    //no admin detected

        textViewMessage.setText("admin phone not recognized");
        Toast.makeText(Registration_Activity.this,"please enter admin phone and name again", Toast.LENGTH_LONG).show();

    }
}

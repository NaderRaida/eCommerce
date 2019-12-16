package com.goldcode.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.nio.file.Files;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText inputName,inputPhoneNumber,inputPassword;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createAccountButton = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputPhoneNumber = findViewById(R.id.register_phone_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private void createAccount() {
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(name)){
            MDToast.makeText(RegisterActivity.this,"Please enter your username",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();

        }else if (TextUtils.isEmpty(phone)) {
            MDToast.makeText(RegisterActivity.this,"Please enter your phoneNumber",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();

        }else if (TextUtils.isEmpty(password)){
            MDToast.makeText(RegisterActivity.this,"Please enter your password",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();

        }else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name, phone, password);
        }
    }

    private void ValidatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",password);
                    userDataMap.put("name",name);
                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                MDToast.makeText(RegisterActivity.this,"Congratulations, your account has been created.",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();

                                loadingBar.dismiss();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();
                            }else{
                                MDToast.makeText(RegisterActivity.this,"Network Error : please try again after some time",MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }else{
                    MDToast.makeText(RegisterActivity.this,"This phone number : "+phone+" already't exists,try again using another phone number",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();

                    loadingBar.dismiss();
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.goldcode.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goldcode.ecommerce.Model.Users;
import com.goldcode.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

public class MainActivity extends AppCompatActivity {
private Button joinNowButton,loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowButton = findViewById(R.id.main_join_now_btn);
        loginButton = findViewById(R.id.main_login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(MainActivity.this,LoginActivity.class));
              finish();
            }
        });
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
        String userPhone = Paper.book().read(Prevalent.userPhoneKey);
        String userPassword = Paper.book().read(Prevalent.userPasswordKey);

        if (userPhone != null
                && userPassword != null){
            if (!TextUtils.isEmpty(userPhone)
            && !TextUtils.isEmpty(userPassword)){
                AllowAccess(userPhone,userPassword);
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void AllowAccess(final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(phone).exists()){
                    Users userData = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if(userData.getPassword().equals(password)){
                        loadingBar.dismiss();
                        Prevalent.currentOnlineUser = userData;
                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                        finish();
                    }else{
                        MDToast.makeText(MainActivity.this,"inCorrected password, try again",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                        loadingBar.dismiss();
                    }
                }else{
                    MDToast.makeText(MainActivity.this,"Account with this phone "+phone+" doesn't exists,you need to create a new account.",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

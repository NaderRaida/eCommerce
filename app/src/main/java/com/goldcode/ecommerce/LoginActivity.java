package com.goldcode.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goldcode.ecommerce.Model.Users;
import com.goldcode.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.valdesekamdem.library.mdtoast.MDToast;

public class LoginActivity extends AppCompatActivity {
    private EditText inputPhoneNumber, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private String parentDBName = "Users";
    private CheckBox chkbRememberMe;
    private TextView adminLink, notAdminLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputPassword = findViewById(R.id.login_password_input);
        inputPhoneNumber = findViewById(R.id.login_phone_number_input);
        loginButton = findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);
        chkbRememberMe = findViewById(R.id.remember_me_chkb);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);
        Paper.init(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login");
                notAdminLink.setVisibility(View.INVISIBLE);
                adminLink.setVisibility(View.VISIBLE);
                parentDBName = "Users";
            }
        });
        /* to make done keyboard key login direct*/
        /*inputPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    closeKeyboard();
                    loginUser();
                    return true;
                }
                return false;
            }
        });*/

    }

    private void loginUser() {
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            MDToast.makeText(this,"Please enter your phoneNumber",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();

        }else if (TextUtils.isEmpty(password)){
            MDToast.makeText(this,"Please enter your password",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();

        }else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }
    private void AllowAccessToAccount(final String phone, final String password) {
        if (chkbRememberMe.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDBName).child(phone).exists()){
                    Users userData = dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);
                    if(userData.getPassword().equals(password)){
                        if(parentDBName.equals("Admins")){
                            loadingBar.dismiss();
                            startActivity(new Intent(LoginActivity.this,AdminCategoryActivity.class));
                            finish();
                        }else if(parentDBName.equals("Users")){
                            loadingBar.dismiss();
                            Prevalent.currentOnlineUser = userData;
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            finish();
                        }
                    }else{
                        MDToast.makeText(LoginActivity.this,"inCorrected password, try again",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();

                        loadingBar.dismiss();
                    }
                }else{
                    MDToast.makeText(LoginActivity.this,"Account with this phone "+phone+" doesn't exists,you need to create a new account.",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();

                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

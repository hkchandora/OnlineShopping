package com.himanshu.onlineshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.Admin.AdminDashBoard;
import com.himanshu.onlineshopping.Buyers.DashBoard;
import com.himanshu.onlineshopping.Buyers.ResetPassword;
import com.himanshu.onlineshopping.Model.SignUpMember;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LogIn extends AppCompatActivity {

    EditText PhoneTxt, PasswordTxt;
    CheckBox RememberMe;
    TextView UserTxt, AdminTxt, ForgotTxt;
    DatabaseReference reference;
    String PresentDbName = "Users";
    ProgressDialog loadBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        PhoneTxt = findViewById(R.id.inPhoneNo);
        PasswordTxt = findViewById(R.id.inPassword);
        RememberMe = findViewById(R.id.rememberMeChk);
        UserTxt = findViewById(R.id.userTxt);
        AdminTxt = findViewById(R.id.adminTxt);
        ForgotTxt = findViewById(R.id.forgotTxt);
        reference = FirebaseDatabase.getInstance().getReference().child("SignUp Members");
        loadBar = new ProgressDialog(this);
        Paper.init(this);


        ForgotTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResetPassword.class);
                i.putExtra("check", "login");
                startActivity(i);
            }
        });

        UserTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTxt.setVisibility(View.INVISIBLE);
                AdminTxt.setVisibility(View.VISIBLE);
                PresentDbName = "Admin";
                PhoneTxt.setHint("  Admin Phone Number");
                ForgotTxt.setVisibility(View.INVISIBLE);
            }
        });

        AdminTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTxt.setVisibility(View.VISIBLE);
                AdminTxt.setVisibility(View.INVISIBLE);
                PresentDbName = "Users";
                PhoneTxt.setHint("  Phone Number");
                ForgotTxt.setVisibility(View.VISIBLE);
            }
        });
    }

    public void LogInCheck(View view) {

        final String PhoneNo = PhoneTxt.getText().toString();
        final String Password = PasswordTxt.getText().toString();

        if (TextUtils.isEmpty(PhoneNo)) {
            PhoneTxt.setError("Phone No is required");
        } else if (TextUtils.isEmpty(Password)) {
            PasswordTxt.setError("Password is required");
        } else {

            loadBar.show();
            loadBar.setContentView(R.layout.progress_dialog);
            loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadBar.setCanceledOnTouchOutside(false);

            if (RememberMe.isChecked()) {
                if (PresentDbName.equals("Users")) {
                    Paper.book().write(Prevalent.UserPhoneNoKey, PhoneNo);
                    Paper.book().write(Prevalent.UserPasswordKey, Password);
                }
                if (PresentDbName.equals("Admin")) {
                    Paper.book().write(Prevalent.AdminPhoneNoKey, PhoneNo);
                    Paper.book().write(Prevalent.AdminPasswordKey, Password);
                }
            }

            if (PresentDbName.equals("Users")) {
                reference.child(PresentDbName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(PhoneNo).exists()) {
                            SignUpMember userData = dataSnapshot.child(PhoneNo).getValue(SignUpMember.class);
                            if (userData.getPhoneNo().equals(PhoneNo)) {
                                if (userData.getPassword().equals(Password)) {
                                    loadBar.dismiss();
                                    Toast.makeText(LogIn.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                                    finish();
                                } else {
                                    loadBar.dismiss();
                                    Toast.makeText(LogIn.this, "Password Incorrect..!!Please try Again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LogIn.this, "Account with " + PhoneNo + " number is not exist.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(LogIn.this, "Please create a new Account", Toast.LENGTH_SHORT).show();
                            loadBar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            if (PresentDbName.equals("Admin")) {

                reference.child(PresentDbName).orderByChild("PhoneNo").equalTo(PhoneNo).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if ((String.valueOf(data.child("PhoneNo").getValue())).equals(PhoneNo)) {
                                    if ((String.valueOf(data.child("Password").getValue())).equals(Password)) {
                                        loadBar.dismiss();
                                        Toast.makeText(LogIn.this, "Admin Login Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), AdminDashBoard.class));
                                        finish();
                                    } else {
                                        loadBar.dismiss();
                                        Toast.makeText(LogIn.this, "Password Incorrect..!!Please try Again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void ForgetPassword(View view) {

    }
}

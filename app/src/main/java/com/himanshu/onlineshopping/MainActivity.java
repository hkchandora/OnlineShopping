package com.himanshu.onlineshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.Admin.AdminDashBoard;
import com.himanshu.onlineshopping.Buyers.DashBoard;
import com.himanshu.onlineshopping.Model.SignUpMember;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.himanshu.onlineshopping.Sellers.SellerDashBoard;
import com.himanshu.onlineshopping.Sellers.SellerRegistration;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog loadBar;
    private String AdminPhoneNoKey, AdminPasswordKey, UserPhoneNoKey, UserPasswordKey;
    private DatabaseReference reference;
    private TextView SellerTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reference = FirebaseDatabase.getInstance().getReference();

        SellerTxt = findViewById(R.id.become_seller_txt);

        Paper.init(this);
        loadBar = new ProgressDialog(this);

        //For User Auto LogIn
        UserPhoneNoKey = Paper.book().read(Prevalent.UserPhoneNoKey);
        UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        //For Admin Auto LogIn
        AdminPhoneNoKey = Paper.book().read(Prevalent.AdminPhoneNoKey);
        AdminPasswordKey = Paper.book().read(Prevalent.AdminPasswordKey);

        if (UserPhoneNoKey != null && UserPasswordKey != null) {
            if (!TextUtils.isEmpty(UserPhoneNoKey) && !TextUtils.isEmpty(UserPasswordKey)) {

                final String PhoneNo = UserPhoneNoKey;
                final String Password = UserPasswordKey;

                loadBar.show();
                loadBar.setContentView(R.layout.progress_dialog);
                loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                loadBar.setCanceledOnTouchOutside(false);

                reference.child("SignUp Members").child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(PhoneNo).exists()) {
                            SignUpMember userData = dataSnapshot.child(PhoneNo).getValue(SignUpMember.class);
                            if (userData.getPhoneNo().equals(PhoneNo)) {
                                if (userData.getPassword().equals(Password)) {
                                    loadBar.dismiss();
                                   // Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                                    Prevalent.currentOnlineUser = userData;
                                    finish();
                                } else {
                                    loadBar.dismiss();
                                   // Toast.makeText(MainActivity.this, "Password Incorrect..!!Please try Again.", Toast.LENGTH_SHORT).show();
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
        if (AdminPhoneNoKey != null && AdminPasswordKey != null) {
            if (!TextUtils.isEmpty(AdminPhoneNoKey) && !TextUtils.isEmpty(AdminPasswordKey)) {
                final String AdminPhoneNo = AdminPhoneNoKey;
                final String AdminPassword = AdminPasswordKey;

                loadBar.show();
                loadBar.setContentView(R.layout.progress_dialog);
                loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                loadBar.setCanceledOnTouchOutside(false);


                reference.child("SignUp Members").child("Admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if ((String.valueOf(data.child("PhoneNo").getValue())).equals(AdminPhoneNo)) {
                                    if ((String.valueOf(data.child("Password").getValue())).equals(AdminPassword)) {
                                        loadBar.dismiss();
                                        Toast.makeText(MainActivity.this, "Admin Login Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), AdminDashBoard.class));
                                        finish();
                                    } else {
                                        loadBar.dismiss();
                                        //Toast.makeText(MainActivity.this, "Password Incorrect..!!Please try Again.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            startActivity(new Intent(getApplicationContext(), SellerDashBoard.class));
            finish();
        }
    }

    public void SignInPage(View view) {
        startActivity(new Intent(getApplicationContext(), LogIn.class));
    }

    public void LogInPage(View view) {
        startActivity(new Intent(getApplicationContext(), SignUp.class));
    }

    public void Seller(View view) {
        startActivity(new Intent(getApplicationContext(), SellerRegistration.class));
    }
}

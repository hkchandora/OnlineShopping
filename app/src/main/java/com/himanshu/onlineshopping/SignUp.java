package com.himanshu.onlineshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    EditText NameTxt, PhoneNoTxt, PasswordTxt;
    DatabaseReference reference;
    ProgressDialog loadBar;
    FirebaseAuth mAuth;
    String CodeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        NameTxt = findViewById(R.id.regName);
        PhoneNoTxt = findViewById(R.id.regPhoneNo);
        PasswordTxt = findViewById(R.id.regPassword);

        reference = FirebaseDatabase.getInstance().getReference().child("SignUp Members").child("Users");
        loadBar = new ProgressDialog(this);
    }

      public void Register(View view) {

        final String name = NameTxt.getText().toString();
        final String phoneNo = PhoneNoTxt.getText().toString();
        final String password = PasswordTxt.getText().toString();

        if (TextUtils.isEmpty(name)) {
            NameTxt.setError("Name required");
        } else if (TextUtils.isEmpty(phoneNo)) {
            PhoneNoTxt.setError("Phone NO required");
        } else if (TextUtils.isEmpty(password)) {
            PasswordTxt.setError("Password required");
        } else {

            loadBar.show();
            loadBar.setContentView(R.layout.progress_dialog);
            loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadBar.setCanceledOnTouchOutside(false);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child(phoneNo).exists()) {
                        HashMap<String, Object> userdata = new HashMap<>();
                        userdata.put("Name", name);
                        userdata.put("PhoneNo", phoneNo);
                        userdata.put("Password", password);

                        reference.child(phoneNo).updateChildren(userdata)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUp.this, "Success", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                            loadBar.dismiss();
                                        } else {
                                            Toast.makeText(SignUp.this, "Network Problem..!!Please Check your internet connection", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                            loadBar.dismiss();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(SignUp.this, "Already Exist..!!Please try with another number", Toast.LENGTH_SHORT).show();
                        loadBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}

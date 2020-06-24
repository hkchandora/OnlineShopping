package com.himanshu.onlineshopping.Sellers;


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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.himanshu.onlineshopping.R;

public class SellerLogIn extends AppCompatActivity {

    private EditText SellerInEmail, SellerInPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_log_in);

        SellerInEmail = findViewById(R.id.seller_email_login);
        SellerInPassword = findViewById(R.id.seller_password_login);

        loadBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void SellerLogInBtn(View view) {

        String Email = SellerInEmail.getText().toString();
        String Password = SellerInPassword.getText().toString();

        if (TextUtils.isEmpty(Email)) {
            SellerInEmail.setError("Email is required");
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            SellerInEmail.setError("Email is incorrect");
        } else if (TextUtils.isEmpty(Password)) {
            SellerInPassword.setError("Password is required");
        } else {

            loadBar.show();
            loadBar.setContentView(R.layout.progress_dialog);
            loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadBar.setCanceledOnTouchOutside(false);

            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadBar.dismiss();
                                Toast.makeText(SellerLogIn.this, "LogIn Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), SellerDashBoard.class));
                                finish();
                            } else {
                                loadBar.dismiss();
                                Toast.makeText(SellerLogIn.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.himanshu.onlineshopping.Model.Seller;
import com.himanshu.onlineshopping.R;

public class SellerRegistration extends AppCompatActivity {

    private EditText SellerName, SellerPhone, SellerEmail, SellerPassword, SellerShopAddress, SellerAddress;
    private FirebaseAuth mAuth;
    private ProgressDialog loadBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        SellerName = findViewById(R.id.seller_name);
        SellerPhone = findViewById(R.id.seller_phone);
        SellerEmail = findViewById(R.id.seller_email);
        SellerPassword = findViewById(R.id.seller_password);
        SellerShopAddress = findViewById(R.id.seller_shop_address);
        SellerAddress = findViewById(R.id.seller_address);

        loadBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void SellerRegister(View view) {

        final String Name = SellerName.getText().toString();
        final String Phone = SellerPhone.getText().toString();
        final String Email = SellerEmail.getText().toString();
        final String Password = SellerPassword.getText().toString();
        final String ShopAddress = SellerShopAddress.getText().toString();
        final String Address = SellerAddress.getText().toString();

        if (TextUtils.isEmpty(Name)) {
            SellerName.setError("Name is required");
        } else if (TextUtils.isEmpty(Phone)) {
            SellerPhone.setError("Phone number is required");
        } else if (Phone.length() < 10) {
            SellerPhone.setError("Phone Number Must be 10 digits");
        } else if (TextUtils.isEmpty(Email)) {
            SellerEmail.setError("Email is required");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            SellerEmail.setError("Email is incorrect");
        } else if (TextUtils.isEmpty(Password)) {
            SellerPassword.setError("Password is required");
        } else if (TextUtils.isEmpty(ShopAddress)) {
            SellerAddress.setError("Shop Address is required");
        } else if (TextUtils.isEmpty(Address)) {
            SellerAddress.setError("Address is required");
        } else {

            loadBar.show();
            loadBar.setContentView(R.layout.progress_dialog);
            loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadBar.setCanceledOnTouchOutside(false);


            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                DatabaseReference SellerRef;
                                SellerRef = FirebaseDatabase.getInstance().getReference().child("Seller Info");

                                Seller Member = new Seller();
                                Member.setSid(task.getResult().getUser().getUid());
                                Member.setName(Name);
                                Member.setPhone(Phone);
                                Member.setEmail(Email);
                                Member.setPassword(Password);
                                Member.setAddress(Address);
                                Member.setShopAddress(ShopAddress);
                                SellerRef.child(task.getResult().getUser().getUid()).setValue(Member);
                                Toast.makeText(SellerRegistration.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                loadBar.dismiss();
                                startActivity(new Intent(getApplicationContext(), SellerLogIn.class));
                                finish();
                            } else {
                                loadBar.dismiss();
                                Toast.makeText(SellerRegistration.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }
    }


    public void SellerLogin(View view) {
        startActivity(new Intent(getApplicationContext(), SellerLogIn.class));
    }
}

package com.himanshu.onlineshopping.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.himanshu.onlineshopping.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrder extends AppCompatActivity {

    private EditText ShipmentName, ShipmentPhoneNo, ShipmentAddress, ShipmentCityCode;
    private TextView ShipmentTotalAmount;
    private String TotalPriceAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        TotalPriceAmount = getIntent().getExtras().getString("Total Price");
        ShipmentName = findViewById(R.id.confirm_name);
        ShipmentPhoneNo = findViewById(R.id.confirm_phone_number);
        ShipmentAddress= findViewById(R.id.confirm_address);
        ShipmentCityCode = findViewById(R.id.confirm_city_code);
        ShipmentTotalAmount = findViewById(R.id.conform_total_amount);

        ShipmentTotalAmount.setText("Total Amount = "+TotalPriceAmount);


    }

    public void ConfirmOrder(View view) {
        if(TextUtils.isEmpty(ShipmentName.getText().toString())){
            ShipmentName.setError("Name is required");
        } else if(TextUtils.isEmpty(ShipmentPhoneNo.getText().toString())){
            ShipmentPhoneNo.setError("Phone Number is required");
        } else if(TextUtils.isEmpty(ShipmentAddress.getText().toString())){
            ShipmentAddress.setError("Address is required");
        } else if(TextUtils.isEmpty(ShipmentCityCode.getText().toString())){
            ShipmentCityCode.setError("City Code is required");
        } else{

            final String SaveCurrentTime, SaveCurrentDate;
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            SaveCurrentDate = currentDate.format(calForDate.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            SaveCurrentTime = currentTime.format(calForDate.getTime());

            final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                    .child(Prevalent.currentOnlineUser.getPhoneNo());

            HashMap<String, Object> orderMap = new HashMap<>();
            orderMap.put("totalAmount", TotalPriceAmount);
            orderMap.put("name", ShipmentName.getText().toString());
            orderMap.put("phoneNo", ShipmentPhoneNo.getText().toString());
            orderMap.put("address", ShipmentAddress.getText().toString());
            orderMap.put("cityCode", ShipmentCityCode.getText().toString());
            orderMap.put("date", SaveCurrentDate);
            orderMap.put("time", SaveCurrentTime);
            orderMap.put("status", "not shipped");

            orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                                .child(Prevalent.currentOnlineUser.getPhoneNo())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(ConfirmFinalOrder.this, "Your final order has been placed successful", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), DashBoard.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }
                                    }
                                });
                    }
                }
            });
        }
    }
}

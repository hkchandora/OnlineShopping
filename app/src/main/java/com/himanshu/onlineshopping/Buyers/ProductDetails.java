package com.himanshu.onlineshopping.Buyers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.himanshu.onlineshopping.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetails extends AppCompatActivity {

    private TextView ProductName, ProductDescription, ProductPrice;
    private ImageView ProductImage;
    private ElegantNumberButton numberButton;
    private Button addToCartBtn;

    private String ProductId = "", status = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ProductName = findViewById(R.id.product_name_details);
        ProductDescription = findViewById(R.id.product_description_details);
        ProductPrice = findViewById(R.id.product_price_details);

        ProductImage = findViewById(R.id.product_image_details);
        numberButton = findViewById(R.id.number_details_btn);
        addToCartBtn = findViewById(R.id.addToCart_btn_details);

        ProductId = getIntent().getStringExtra("pid");


        DatabaseReference ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        ProductRef.orderByChild("pid").equalTo(ProductId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ProductName.setText(String.valueOf(data.child("pname").getValue()));
                        ProductPrice.setText(String.valueOf(data.child("price").getValue()));
                        ProductDescription.setText(String.valueOf(data.child("description").getValue()));
                        Picasso.with(ProductDetails.this).load(String.valueOf(data.child("image").getValue())).into(ProductImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("Order Placed") || status.equals("Order Shipped")) {
                    Toast.makeText(ProductDetails.this, "You can purchase more product, once your order is shipped or confirmed.", Toast.LENGTH_LONG).show();
                } else {
                    AddToCart();
                }
            }
        });
    }


    public void AddToCart() {

        String SaveCurrentTime, SaveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        SaveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        SaveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference CartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", ProductId);
        cartMap.put("pname", ProductName.getText().toString());
        cartMap.put("price", ProductPrice.getText().toString());
        cartMap.put("date", SaveCurrentDate);
        cartMap.put("time", SaveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        CartRef.child("User View").child(Prevalent.currentOnlineUser.getPhoneNo()).child("Products").child(ProductId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            CartRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhoneNo()).child("Products").child(ProductId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if ((task.isSuccessful())) {
                                                Toast.makeText(ProductDetails.this, "Added to Cart List", Toast.LENGTH_SHORT).show();
//                                                startActivity(new Intent(getApplicationContext(), DashBoard.class));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderStatus();
    }

    private void CheckOrderStatus() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhoneNo());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ShipmentStatus = dataSnapshot.child("status").getValue().toString();

                    if (ShipmentStatus.equals("shipped")) {
                        status = "Order Shipped";
                    } else if (ShipmentStatus.equals("not shipped")) {
                        status = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


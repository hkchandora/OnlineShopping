package com.himanshu.onlineshopping.Admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.R;
import com.squareup.picasso.Picasso;

public class MaintainProducts extends AppCompatActivity {

    private Button ApplyChangeBtn;
    private EditText Name, Price, Description;
    private ImageView Image;
    private String PID;
    private DatabaseReference ProductRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_products);

        ApplyChangeBtn = findViewById(R.id.maintain_apply_change_btn);
        Name = findViewById(R.id.maintain_product_item_name);
        Price = findViewById(R.id.maintain_product_item_price);
        Description = findViewById(R.id.maintain_product_item_description);
        Image = findViewById(R.id.maintain_product_item_image);

        PID = getIntent().getExtras().getString("pid");

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        ProductRef.orderByChild("pid").equalTo(PID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String name = String.valueOf(data.child("pname").getValue());
                        String price = String.valueOf(data.child("price").getValue());
                        String description = String.valueOf(data.child("description").getValue());
                        String image = String.valueOf(data.child("image").getValue());

                        Name.setText(name);
                        Price.setText(price);
                        Description.setText(description);
                        Picasso.with(MaintainProducts.this).load(image).into(Image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ApplyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeApply();
            }
        });
    }

    public void ChangeApply() {
        final String name = Name.getText().toString();
        final String price = Price.getText().toString();
        final String description = Description.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Name.setError("Name is required");
        } else if (TextUtils.isEmpty(name)) {
            Price.setError("Price is required");
        } else if (TextUtils.isEmpty(name)) {
            Description.setError("Description is required");
        } else {

            ProductRef.orderByChild("pid").equalTo(PID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            data.getRef().child("pname").setValue(name);
                            data.getRef().child("price").setValue(price);
                            data.getRef().child("description").setValue(description);
                            Toast.makeText(MaintainProducts.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void DeleteProduct(View view){
        ProductRef.orderByChild("pid").equalTo(PID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        data.getRef().removeValue();
                        Toast.makeText(MaintainProducts.this, "Product removed success", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

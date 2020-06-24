package com.himanshu.onlineshopping.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.Model.CartList;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.himanshu.onlineshopping.R;
import com.himanshu.onlineshopping.ViewHolder.CartViewHolder;

public class Cart extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextBtn, BackBtn;
    private TextView CartPriceTxt, msgTxt1, msgTxt2;
    private int TotalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        CartPriceTxt = findViewById(R.id.cart_price);
        NextBtn = findViewById(R.id.cart_next_process);
        BackBtn = findViewById(R.id.back_btn);
        msgTxt1 = findViewById(R.id.msg1);
        msgTxt2 = findViewById(R.id.msg2);
        msgTxt2.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.cart_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        CheckForAnyOrder();
        CheckOrderStatus();


        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ConfirmFinalOrder.class);
                i.putExtra("Total Price", String.valueOf(TotalAmount));
                startActivity(i);
                finish();
            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        TotalAmount = 0;

        FirebaseRecyclerOptions<CartList> options =
                new FirebaseRecyclerOptions.Builder<CartList>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhoneNo()).child("Products"), CartList.class)
                        .build();

        FirebaseRecyclerAdapter<CartList, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartList, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final CartList model) {
                        holder.ProductNameTxt.setText(model.getPname());
                        holder.ProductPriceTxt.setText("Price = " + model.getPrice() + "/-");
                        holder.ProductQuantityTxt.setText("Quantity = " + model.getQuantity());


                        int oneTimeProductTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                        TotalAmount = TotalAmount + oneTimeProductTotalPrice;

                        CartPriceTxt.setText("Total Price = " + String.valueOf(TotalAmount) + "/-");

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Edit item",
                                                "Remove item"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
                                builder.setTitle("Cart Options:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(getApplicationContext(), ProductDetails.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (i == 1) {
                                            cartListRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhoneNo())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                cartListRef.child("Admin View")
                                                                        .child(Prevalent.currentOnlineUser.getPhoneNo())
                                                                        .child("Products")
                                                                        .child(model.getPid())
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(Cart.this, "Item removed", Toast.LENGTH_SHORT).show();
                                                                                onStart();
                                                                                CartPriceTxt.setText("Total Price = " + String.valueOf(TotalAmount) + "/-");
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderStatus() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhoneNo());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ShipmentStatus = dataSnapshot.child("status").getValue().toString();
                    String ShipmentName = dataSnapshot.child("name").getValue().toString();

                    if (ShipmentStatus.equals("shipped")) {
                        CartPriceTxt.setText("Dear " + ShipmentName + "\norder is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);
                        msgTxt1.setVisibility(View.VISIBLE);
                        msgTxt1.setText("Congrats, Your final order has been shipped successfully. Soon you will received your order at your door step.");
                        NextBtn.setVisibility(View.GONE);
//                        Toast.makeText(Cart.this, "You can purchase more products, once you received your final order.", Toast.LENGTH_SHORT).show();
                    } else if (ShipmentStatus.equals("not shipped")) {

                        CartPriceTxt.setText("Shipping Status = Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        msgTxt1.setVisibility(View.VISIBLE);
                        msgTxt1.setText("Congrats, Your final order has been placed successfully. Soon it will be verified.");
                        NextBtn.setVisibility(View.GONE);
                        Toast.makeText(Cart.this, "You can purchase more products, once you received your final order.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void CheckForAnyOrder() {

        DatabaseReference UserCartRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                .child(Prevalent.currentOnlineUser.getPhoneNo()).child("Products");

        final DatabaseReference OrderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhoneNo());

        UserCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    OrderRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                NextBtn.setVisibility(View.INVISIBLE);
                                msgTxt2.setVisibility(View.VISIBLE);
                                CartPriceTxt.setText("Total Price = 0/-");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

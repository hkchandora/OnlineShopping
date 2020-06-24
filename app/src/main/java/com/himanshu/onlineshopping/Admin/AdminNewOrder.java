package com.himanshu.onlineshopping.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.himanshu.onlineshopping.Model.AdminOrders;
import com.himanshu.onlineshopping.R;
import com.himanshu.onlineshopping.ViewHolder.AdminOrdersViewHolder;

public class AdminNewOrder extends AppCompatActivity {

    private RecyclerView OrderList;
    private DatabaseReference orderRef, AdminViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        AdminViewCart = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View");

        OrderList = findViewById(R.id.order_list);
        OrderList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef, AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {

                        holder.UserName.setText("Name: "+model.getName());
                        holder.UserPhoneNo.setText("Phone: "+model.getPhoneNo());
                        holder.UserTotalPrice.setText("Total Amount: "+model.getTotalAmount()+"/-");
                        holder.UserDateTime.setText("Order at: "+model.getDate()+"  "+model.getTime());
                        holder.UserShippingAddress.setText("Shipping Address: "+model.getAddress()+", "+model.getCityCode());

                        holder.ShowOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String UID = getRef(position).getKey();
                                Intent i = new Intent(getApplicationContext(), ShowUsersOrdersProducts.class);
                                i.putExtra("uid", UID);
                                startActivity(i);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                final AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrder.this);
                                builder.setTitle("Have you shipped this order products ?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if(i == 0) {
                                            String UID = getRef(position).getKey();

                                            orderRef.child(UID).removeValue();
                                            AdminViewCart.child(UID).removeValue();

                                        } else if( i ==1){
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        OrderList.setAdapter(adapter);
        adapter.startListening();
    }

    public void BackBtn(View view) {
        finish();
    }

}

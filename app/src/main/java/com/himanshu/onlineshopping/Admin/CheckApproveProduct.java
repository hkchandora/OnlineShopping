package com.himanshu.onlineshopping.Admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.Model.Product;
import com.himanshu.onlineshopping.R;
import com.himanshu.onlineshopping.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class CheckApproveProduct extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unVerifyProductRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_approve_product);

        recyclerView = findViewById(R.id.recyclerView_approve_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        unVerifyProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(unVerifyProductRef.orderByChild("productStatus").equalTo("Not Approved"), Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {

                        holder.ProductItemName.setText(model.getPname());
                        holder.ProductItemPrice.setText("Price = " + model.getPrice() + "/-");
                        holder.ProductItemDescription.setText(model.getDescription());
                        Picasso.with(CheckApproveProduct.this).load(model.getImage()).placeholder(R.drawable.product_image_placeholder).into(holder.ProductItemImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final  String productId = model.getPid();

                                CharSequence option[] = new CharSequence[]{
                                        "Approve",
                                        "Not"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CheckApproveProduct.this);
                                builder.setTitle("Do you want to Approve this product. Are you Sure?");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if(i == 0){
                                            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
                                            productRef.orderByChild("pid").equalTo(productId).limitToFirst(1)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getChildrenCount() > 0) {
                                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                            data.getRef().child("productStatus").setValue("Approved");
                                                            Toast.makeText(CheckApproveProduct.this, "Product is Approved. Now it is available for sale from the seller", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            dialog.dismiss();
                                        } else if(i == 1){
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
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void BackBtnApproveProduct(View view){
        finish();
    }
}

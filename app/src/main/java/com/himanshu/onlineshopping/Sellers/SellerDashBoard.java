package com.himanshu.onlineshopping.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.himanshu.onlineshopping.Admin.AdminNewOrder;
import com.himanshu.onlineshopping.MainActivity;
import com.himanshu.onlineshopping.Model.Product;
import com.himanshu.onlineshopping.R;
import com.himanshu.onlineshopping.ViewHolder.SellerProductViewHolder;
import com.squareup.picasso.Picasso;


public class SellerDashBoard extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unVerifyProductRef;
    private FloatingActionButton AddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dash_board);

        recyclerView = findViewById(R.id.recyclerView_un_approve_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        AddBtn = findViewById(R.id.fab2);

        unVerifyProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        BottomNavigationView navView = findViewById(R.id.nav_view_bottom);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SellerCategory.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(unVerifyProductRef.orderByChild("sellerID")
                                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Product.class)
                        .build();


        FirebaseRecyclerAdapter<Product, SellerProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, SellerProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellerProductViewHolder holder, int i, @NonNull final Product model) {
                        holder.ProductItemName.setText(model.getPname());
                        holder.ProductItemPrice.setText("Price = " + model.getPrice() + "/-");
                        holder.ProductItemDescription.setText(model.getDescription());
                        holder.ProductItemStatus.setText("Status : " + model.getProductStatus());
                        Picasso.with(SellerDashBoard.this).load(model.getImage()).placeholder(R.drawable.product_image_placeholder).into(holder.ProductItemImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String productId = model.getPid();

                                CharSequence option[] = new CharSequence[]{
                                        "Delete",
                                        "Not"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerDashBoard.this);
                                builder.setTitle("Do you want to Delete this product! Are you Sure?");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (i == 0) {
                                            String Category = model.getCategory();
                                            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
                                            productRef.child(Category + " " + productId).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SellerDashBoard.this, "Product is Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                            dialog.dismiss();
                                        } else if (i == 1) {
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
                    public SellerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_unapproved_product_item, parent, false);
                        SellerProductViewHolder holder = new SellerProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_order:
                    startActivity(new Intent(getApplicationContext(), AdminNewOrder.class));
                    return true;
                case R.id.navigation_logOut:
                    final FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
            }
            return false;
        }
    };
}

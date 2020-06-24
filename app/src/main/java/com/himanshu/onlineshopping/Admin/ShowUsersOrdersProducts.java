package com.himanshu.onlineshopping.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.himanshu.onlineshopping.Model.CartList;
import com.himanshu.onlineshopping.R;
import com.himanshu.onlineshopping.ViewHolder.CartViewHolder;

public class ShowUsersOrdersProducts extends AppCompatActivity {

    private RecyclerView ProductList;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;
    private String UserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users_orders_products);

        UserID = getIntent().getExtras().getString("uid");
        ProductList = findViewById(R.id.product_list_recyclerView);
        ProductList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ProductList.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View")
                .child(UserID).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CartList> options =
                new FirebaseRecyclerOptions.Builder<CartList>()
                .setQuery(cartListRef, CartList.class)
                .build();

        FirebaseRecyclerAdapter<CartList, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartList, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartList model) {
                        holder.ProductNameTxt.setText(model.getPname());
                        holder.ProductPriceTxt.setText("Price = "+model.getPrice()+"/-");
                        holder.ProductQuantityTxt.setText("Quantity = "+model.getQuantity());

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };

        ProductList.setAdapter(adapter);
        adapter.startListening();
    }

    public void BackButton(View view) {
        finish();
    }
}

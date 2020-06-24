package com.himanshu.onlineshopping.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.himanshu.onlineshopping.Model.Product;
import com.himanshu.onlineshopping.R;
import com.himanshu.onlineshopping.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class SearchProducts extends AppCompatActivity {

    private Button SearchBtn;
    private EditText inputTxt;
    private String SearchInputTxt="";
    private RecyclerView SearchList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        SearchBtn = findViewById(R.id.search_btn);
        inputTxt = findViewById(R.id.search_product_name);

        SearchList = findViewById(R.id.search_product_recyclerView);
        SearchList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        SearchList.setLayoutManager(layoutManager);

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchInputTxt = inputTxt.getText().toString();

                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(reference.orderByChild("pname").startAt(SearchInputTxt), Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull final Product model) {
                        holder.ProductItemName.setText(model.getPname());
                        holder.ProductItemPrice.setText("Price = "+model.getPrice()+"/-");
                        holder.ProductItemDescription.setText(model.getDescription());
                        Picasso.with(SearchProducts.this).load(model.getImage()).placeholder(R.drawable.product_image_placeholder).into(holder.ProductItemImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getApplicationContext(), ProductDetails.class);
                                i.putExtra("pid", model.getPid());
                                startActivity(i);
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
        SearchList.setAdapter(adapter);
        adapter.startListening();
    }
}

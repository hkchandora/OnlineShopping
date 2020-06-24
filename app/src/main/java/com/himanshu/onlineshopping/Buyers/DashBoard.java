package com.himanshu.onlineshopping.Buyers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.Admin.MaintainProducts;
import com.himanshu.onlineshopping.MainActivity;
import com.himanshu.onlineshopping.Model.Product;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.himanshu.onlineshopping.R;
import com.himanshu.onlineshopping.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private DatabaseReference reference, ProductRef;
    private String CurrentUserPhoneNo = "";
    private TextView userNameTxt;
    private RecyclerView ProductRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private String type = "";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = getIntent().getExtras().getString("Admin");
        }

        Paper.init(this);
        if (!type.equals("Admin")) {
            CurrentUserPhoneNo = Paper.book().read(Prevalent.UserPhoneNoKey);
            reference = FirebaseDatabase.getInstance().getReference().child("SignUp Members").child("Users").child(CurrentUserPhoneNo);
        }
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Cart.class));
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        if (type.equals("Admin")) {
//            navigationView.setVisibility(View.GONE);
//            drawer.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }

        userNameTxt = headerView.findViewById(R.id.userName_profile_txt);
        final CircleImageView profileImage = headerView.findViewById(R.id.user_profile_image);

        if (!type.equals("Admin")) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = String.valueOf(dataSnapshot.child("Name").getValue());
                    userNameTxt.setText(name);
                    if(dataSnapshot.child("Image").exists()) {
                        String image = String.valueOf(dataSnapshot.child("Image").getValue());
                        Picasso.with(DashBoard.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (type.equals("Admin")) {
            String AdminNo = Paper.book().read(Prevalent.AdminPhoneNoKey);
            DatabaseReference AdminRef = FirebaseDatabase.getInstance().getReference().child("SignUp Members").child("Admin")
                    .child(AdminNo);
            AdminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = String.valueOf(dataSnapshot.child("Name").getValue());
                    userNameTxt.setText(name);
                    if(dataSnapshot.child("Image").exists()) {
                        String image = String.valueOf(dataSnapshot.child("Image").getValue());
                        Picasso.with(DashBoard.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        ProductRecyclerView = findViewById(R.id.recyclerView_menu);
        ProductRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ProductRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_cart) {
            if(!type.equals("Admin")){
                startActivity(new Intent(getApplicationContext(), Cart.class));
            }
        } else if (id == R.id.nav_search) {
            if(!type.equals("Admin")){
                startActivity(new Intent(getApplicationContext(), SearchProducts.class));
            }
        } else if (id == R.id.nav_category) {

        } else if (id == R.id.nav_setting) {
            if(!type.equals("Admin")){
                startActivity(new Intent(getApplicationContext(), Settings.class));
            }
        } else if (id == R.id.nav_logout) {
            if(!type.equals("Admin")){
                Paper.book().destroy();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(ProductRef.orderByChild("productStatus").equalTo("Approved"), Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int prosition, @NonNull final Product model) {
                        holder.ProductItemName.setText(model.getPname());
                        holder.ProductItemPrice.setText("Price = " + model.getPrice() + "/-");
                        holder.ProductItemDescription.setText(model.getDescription());
                        Picasso.with(DashBoard.this).load(model.getImage()).placeholder(R.drawable.product_image_placeholder).into(holder.ProductItemImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (type.equals("Admin")) {
                                    Intent i = new Intent(getApplicationContext(), MaintainProducts.class);
                                    i.putExtra("pid", model.getPid());
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(getApplicationContext(), ProductDetails.class);
                                    i.putExtra("pid", model.getPid());
                                    startActivity(i);
                                }
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

        ProductRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

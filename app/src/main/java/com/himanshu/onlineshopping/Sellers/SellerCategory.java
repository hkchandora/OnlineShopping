package com.himanshu.onlineshopping.Sellers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.himanshu.onlineshopping.R;

public class SellerCategory extends AppCompatActivity {

    private ImageView TShirts, SportTShirt, FemaleDresses, Sweathers;
    private ImageView Glasses, Bags, Hats, Shoes;
    private ImageView HeadPhones, Laptops, Watches, Mobiles;
    private String Category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);

        TShirts = findViewById(R.id.t_shirt);
        SportTShirt = findViewById(R.id.sport_t_shirt);
        FemaleDresses = findViewById(R.id.female_dress);
        Sweathers = findViewById(R.id.sweather);

        Glasses = findViewById(R.id.glass);
        Bags = findViewById(R.id.bag);
        Hats = findViewById(R.id.hat);
        Shoes = findViewById(R.id.shoes);

        HeadPhones = findViewById(R.id.head_phone);
        Laptops = findViewById(R.id.laptop);
        Watches = findViewById(R.id.watch);
        Mobiles = findViewById(R.id.mobile);

        TShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "TShirt";
                AddProduct();
            }
        });

        SportTShirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "SportTShirt";
                AddProduct();
            }
        });

        FemaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "FemaleDress";
                AddProduct();
            }
        });

        Sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Sweather";
                AddProduct();
            }
        });

        Glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Glass";
                AddProduct();
            }
        });

        Bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Bag";
                AddProduct();
            }
        });

        Hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Hat";
                AddProduct();
            }
        });

        Shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Shoes";
                AddProduct();
            }
        });

        HeadPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "HeadPhone";
                AddProduct();
            }
        });

        Laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Laptop";
                AddProduct();
            }
        });

        Watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Watch";
                AddProduct();
            }
        });

        Mobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Mobile";
                AddProduct();
            }
        });

    }

    protected void AddProduct() {
        Intent i = new Intent(getApplicationContext(), SellerAddNewProduct.class);
        i.putExtra("Category", Category);
        startActivity(i);
    }
}

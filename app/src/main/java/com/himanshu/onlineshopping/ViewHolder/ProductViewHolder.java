package com.himanshu.onlineshopping.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.himanshu.onlineshopping.Interface.ItemClickListner;
import com.himanshu.onlineshopping.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView ProductItemName, ProductItemDescription, ProductItemPrice;
    public ImageView ProductItemImage;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        ProductItemImage = itemView.findViewById(R.id.product_item_image);
        ProductItemName = itemView.findViewById(R.id.product_item_name);
        ProductItemDescription = itemView.findViewById(R.id.product_item_description);
        ProductItemPrice = itemView.findViewById(R.id.product_item_price);
    }

    public void setItemClickListner(ItemClickListner listner) {
        this.listner = listner;
    }



    @Override
    public void onClick(View view) {
//        listner.onClick(view, getAdapterPosition(), false);
    }
}

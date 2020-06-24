package com.himanshu.onlineshopping.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.himanshu.onlineshopping.Interface.ItemClickListner;
import com.himanshu.onlineshopping.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView ProductNameTxt, ProductPriceTxt, ProductQuantityTxt;
    private ItemClickListner itemClickListner;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        ProductNameTxt = itemView.findViewById(R.id.cart_item_name);
        ProductPriceTxt = itemView.findViewById(R.id.cart_item_price);
        ProductQuantityTxt = itemView.findViewById(R.id.cart_item_quantity);

    }

    @Override
    public void onClick(View v) {
        //itemClickListner.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}

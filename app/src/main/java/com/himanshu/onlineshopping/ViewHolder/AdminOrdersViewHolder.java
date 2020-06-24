package com.himanshu.onlineshopping.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.himanshu.onlineshopping.R;

import java.nio.file.attribute.UserPrincipal;

public class AdminOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView UserName, UserPhoneNo, UserTotalPrice, UserDateTime, UserShippingAddress;
    public Button ShowOrderBtn;

    public AdminOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        UserName = itemView.findViewById(R.id.order_item_userName);
        UserPhoneNo = itemView.findViewById(R.id.order_item_phoneNo);
        UserTotalPrice = itemView.findViewById(R.id.order_item_price);
        UserDateTime = itemView.findViewById(R.id.order_item_date_time);
        UserShippingAddress = itemView.findViewById(R.id.order_item_address);
        ShowOrderBtn = itemView.findViewById(R.id.order_show_all_product_btn);

    }

    @Override
    public void onClick(View v) {

    }
}
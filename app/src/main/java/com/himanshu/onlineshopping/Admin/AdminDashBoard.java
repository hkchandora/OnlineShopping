package com.himanshu.onlineshopping.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.himanshu.onlineshopping.Buyers.DashBoard;
import com.himanshu.onlineshopping.MainActivity;
import com.himanshu.onlineshopping.R;

import io.paperdb.Paper;

public class AdminDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

    }

    public void MaintainBtn(View view) {
        Intent i = new Intent(getApplicationContext(), DashBoard.class);
        i.putExtra("Admin", "Admin");
        startActivity(i);
    }

    public void CheckNewOrder(View view) {
        startActivity(new Intent(getApplicationContext(), AdminNewOrder.class));
    }

    public void ApprovedProduct(View view) {
        startActivity(new Intent(getApplicationContext(), CheckApproveProduct.class));
    }

    public void LogOut(View view){
        Paper.book().destroy();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

package com.himanshu.onlineshopping.Sellers;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.himanshu.onlineshopping.Model.Product;
import com.himanshu.onlineshopping.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SellerAddNewProduct extends AppCompatActivity {

    private ImageView ProductImage;
    private Button AddProductBtn;
    private EditText ProductName, ProductDescription, ProductPrice;
    private String Category, PName, Price, Description, SaveCurrentDate, SaveCurrentTime;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String ProductRandomKey, DownloadImageUrl;
    private StorageReference storageReference;
    private DatabaseReference reference, sellerRef;
    private ProgressDialog loadBar;
    private String sName, sAddress, sPhone, sEmail, sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_product);

        ProductName = findViewById(R.id.product_name);
        ProductDescription = findViewById(R.id.product_description);
        ProductPrice = findViewById(R.id.product_price);
        ProductImage = findViewById(R.id.select_product_image);
        AddProductBtn = findViewById(R.id.add_product_btn);

        Category = getIntent().getExtras().getString("Category");
        reference = FirebaseDatabase.getInstance().getReference().child("Products");
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Seller Info");
        storageReference = FirebaseStorage.getInstance().getReference().child("Product Images/");
        loadBar = new ProgressDialog(this);


        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            sName = dataSnapshot.child("name").getValue().toString();
                            sAddress = dataSnapshot.child("address").getValue().toString();
                            sPhone = dataSnapshot.child("phone").getValue().toString();
                            sEmail = dataSnapshot.child("email").getValue().toString();
                            sID = dataSnapshot.child("sid").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImageUri = data.getData();
            Picasso.with(SellerAddNewProduct.this).load(ImageUri).into(ProductImage);

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            SaveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:SS a");
            SaveCurrentTime = currentTime.format(calendar.getTime());

            ProductRandomKey = SaveCurrentDate +" "+ SaveCurrentTime;

        }
    }


    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void AddProductData(View view) {

        PName = ProductName.getText().toString();
        Price = ProductPrice.getText().toString();
        Description = ProductDescription.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(PName)) {
            ProductName.setError("Product name is required");
        } else if (TextUtils.isEmpty(Price)) {
            ProductPrice.setError("Product Price is required");
        } else if (TextUtils.isEmpty(Description)) {
            ProductDescription.setError("Product Description is required");
        } else {

            loadBar.show();
            loadBar.setContentView(R.layout.progress_dialog);
            loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadBar.setCanceledOnTouchOutside(false);


            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            SaveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:SS a");
            SaveCurrentTime = currentTime.format(calendar.getTime());

            ProductRandomKey = SaveCurrentDate +" "+ SaveCurrentTime;


            final StorageReference s = storageReference.child(Category+" " +ProductRandomKey+ "."+getFileExtension(ImageUri));;

            UploadTask uploadTask = s.putFile(ImageUri);
            uploadTask
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            DownloadImageUrl = s.getDownloadUrl().toString();
                            return s.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                loadBar.dismiss();

                                DownloadImageUrl = task.getResult().toString();

                                Product item = new Product();
                                item.setPid(ProductRandomKey);
                                item.setDate(SaveCurrentDate);
                                item.setTime(SaveCurrentTime);
                                item.setDescription(Description);
                                item.setImage(DownloadImageUrl);
                                item.setCategory(Category);
                                item.setPrice(Price);
                                item.setPname(PName);

                                item.setProductStatus("Not Approved");
                                item.setSellerName(sName);
                                item.setSellerAddress(sAddress);
                                item.setSellerEmail(sEmail);
                                item.setSellerPhone(sPhone);
                                item.setSellerID(sID);
                                reference.child(Category+" "+ProductRandomKey).setValue(item);

                                Toast.makeText(SellerAddNewProduct.this, "Product Added successfully...", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }
    }
}

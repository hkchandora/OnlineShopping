package com.himanshu.onlineshopping.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.himanshu.onlineshopping.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private EditText PhoneNoTxt, FullNameTxt, AddressTxt;
    private TextView ProfileChange, CloseSetting, UpdateSetting;
    private CircleImageView ProfileImage;
    private DatabaseReference userRef;
    private Uri ImageUri;
    private String myUri = "";
    private StorageReference storageProfileReference;
    private String Checker = "";
    private StorageTask uploadTask;
    private Button VerifyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PhoneNoTxt = findViewById(R.id.setting_phoneNo);
        FullNameTxt = findViewById(R.id.setting_full_name);
        AddressTxt = findViewById(R.id.setting_address);

        ProfileChange = findViewById(R.id.profile_change_setting);
        CloseSetting = findViewById(R.id.close_setting);
        UpdateSetting = findViewById(R.id.update_setting);

        ProfileImage = findViewById(R.id.setting_profile_image);
        VerifyBtn = findViewById(R.id.setting_verify_btn);

        storageProfileReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures/");

        userRef = FirebaseDatabase.getInstance().getReference().child("SignUp Members").child("Users")
                .child(Prevalent.currentOnlineUser.getPhoneNo());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("Name").getValue().toString();
                    FullNameTxt.setText(name);
                    if(dataSnapshot.child("Image").exists()) {
                    String image = dataSnapshot.child("Image").getValue().toString();
                    Picasso.with(Settings.this).load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    }
                    if(dataSnapshot.child("Address").exists()){
                        String phoneNo = dataSnapshot.child("PhoneOrder").getValue().toString();
                        String address = dataSnapshot.child("Address").getValue().toString();
                        AddressTxt.setText(address);
                        PhoneNoTxt.setText(phoneNo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResetPassword.class);
                i.putExtra("check", "setting");
                startActivity(i);
            }
        });

        CloseSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UpdateSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Checker.equals("clicked")) {
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

        ProfileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checker = "clicked";

                CropImage.activity(ImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(Settings.this);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ImageUri = result.getUri();
            ProfileImage.setImageURI(ImageUri);
        } else {
            Exception error = result.getError();
            Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Settings.class));
            finish();
        }
    }

    private void updateOnlyUserInfo() {

        if (TextUtils.isEmpty(FullNameTxt.getText().toString())) {
            FullNameTxt.setError("Name is Required");
        } else if (TextUtils.isEmpty(PhoneNoTxt.getText().toString())) {
            PhoneNoTxt.setError("Phone number is Required");
        } else if (TextUtils.isEmpty(AddressTxt.getText().toString())) {
            AddressTxt.setError("Address is Required");
        } else {

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("Name", FullNameTxt.getText().toString());
            userMap.put("PhoneOrder", PhoneNoTxt.getText().toString());
            userMap.put("Address", AddressTxt.getText().toString());
            userRef.updateChildren(userMap);

            Toast.makeText(Settings.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(FullNameTxt.getText().toString())) {
            FullNameTxt.setError("Name is Required");
        } else if (TextUtils.isEmpty(PhoneNoTxt.getText().toString())) {
            PhoneNoTxt.setError("Phone number is Required");
        } else if (TextUtils.isEmpty(AddressTxt.getText().toString())) {
            AddressTxt.setError("Address is Required");
        } else if (Checker.equals("clicked")) {

            final ProgressDialog loadBar = new ProgressDialog(this);
            loadBar.show();
            loadBar.setContentView(R.layout.progress_dialog);
            loadBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadBar.setCanceledOnTouchOutside(false);

            if (ImageUri != null) {
                final StorageReference fileRef = storageProfileReference.child(Prevalent.currentOnlineUser.getName() + " ("
                        + Prevalent.currentOnlineUser.getPhoneNo() + ").jpg");

                uploadTask = fileRef.putFile(ImageUri);

                uploadTask
                        .continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return fileRef.getDownloadUrl();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    myUri = downloadUri.toString();

                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("Name", FullNameTxt.getText().toString());
                                    userMap.put("PhoneOrder", PhoneNoTxt.getText().toString());
                                    userMap.put("Address", AddressTxt.getText().toString());
                                    userMap.put("Image", myUri);

                                    userRef.updateChildren(userMap);

                                    loadBar.dismiss();
//                                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                                    Toast.makeText(Settings.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    loadBar.dismiss();
                                    Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Image is not Selected", Toast.LENGTH_SHORT).show();
        }
    }
}

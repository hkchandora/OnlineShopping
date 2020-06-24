package com.himanshu.onlineshopping.Buyers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himanshu.onlineshopping.Prevalent.Prevalent;
import com.himanshu.onlineshopping.R;

import java.util.HashMap;

public class ResetPassword extends AppCompatActivity {

    private String Check = "";
    private TextView PageTitle, TitleQuestion;
    private EditText PhoneNo, Question1, Question2;
    private Button VerifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Check = getIntent().getExtras().getString("check");

        PageTitle = findViewById(R.id.reset_title);
        PhoneNo = findViewById(R.id.reset_password_phone);
        TitleQuestion = findViewById(R.id.reset_password_question_txt);
        Question1 = findViewById(R.id.reset_question_1);
        Question2 = findViewById(R.id.reset_question_2);
        VerifyBtn = findViewById(R.id.reset_verify_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();

        PhoneNo.setVisibility(View.GONE);

        if (Check.equals("setting")) {
            PageTitle.setText("Set Questions");
            TitleQuestion.setText("Please Set Answers For The Following Security Questions");
            VerifyBtn.setText("Set");

            DisplayPreviousAnswers();

            VerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer1 = Question1.getText().toString().toLowerCase();
                    String answer2 = Question2.getText().toString().toLowerCase();

                    if (TextUtils.isEmpty(answer1)) {
                        Question1.setError("Required");
                    } else if (TextUtils.isEmpty(answer2)) {
                        Question2.setError("Required");
                    } else {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SignUp Members").child("Users")
                                .child(Prevalent.currentOnlineUser.getPhoneNo());

                        HashMap<String, Object> SecurityMap = new HashMap<>();
                        SecurityMap.put("Answer1", answer1);
                        SecurityMap.put("Answer2", answer2);

                        reference.child("Security Questions").updateChildren(SecurityMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ResetPassword.this, "Answers set Successfully.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });

                    }
                }
            });

        } else if (Check.equals("login")) {

            PhoneNo.setVisibility(View.VISIBLE);

            VerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String phone = PhoneNo.getText().toString().toLowerCase();
                    final String answer1 = Question1.getText().toString().toLowerCase();
                    final String answer2 = Question2.getText().toString().toLowerCase();

                    if (phone.equals("") && answer1.equals("") && answer2.equals("")) {
                        Toast.makeText(ResetPassword.this, "Please fill all info", Toast.LENGTH_SHORT).show();
                    } else {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SignUp Members").child("Users")
                                .child(phone);

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String mPhone = dataSnapshot.child("PhoneOrder").getValue().toString();

                                    if (dataSnapshot.hasChild("Security Questions")) {
                                        String ans1 = String.valueOf(dataSnapshot.child("Security Questions").child("Answer1").getValue());
                                        String ans2 = String.valueOf(dataSnapshot.child("Security Questions").child("Answer2").getValue());

                                        if (!ans1.equals(answer1)) {
                                            Toast.makeText(ResetPassword.this, "Your 1st answer is wrong", Toast.LENGTH_SHORT).show();
                                        } else if (!ans2.equals(answer2)) {
                                            Toast.makeText(ResetPassword.this, "Your 2nd answer is wrong", Toast.LENGTH_SHORT).show();
                                        } else {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                                            builder.setTitle("New Password");

                                            final EditText newPassword = new EditText(ResetPassword.this);
                                            newPassword.setHint("Write  New Password here...");
                                            builder.setView(newPassword);

                                            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, int which) {
                                                    if (!newPassword.getText().toString().equals("")) {
                                                        reference.child("Password").setValue(newPassword.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(ResetPassword.this, "Password Changed Successfully.", Toast.LENGTH_SHORT).show();
                                                                            dialog.cancel();
                                                                            finish();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });

                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                            builder.show();

                                        }

                                    } else {
                                        Toast.makeText(ResetPassword.this, "You have not set the security questions.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                }
            });
        }
    }


    private void DisplayPreviousAnswers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SignUp Members").child("Users")
                .child(Prevalent.currentOnlineUser.getPhoneNo()).child("Security Questions");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ans1 = String.valueOf(dataSnapshot.child("Answer1").getValue());
                    String ans2 = String.valueOf(dataSnapshot.child("Answer2").getValue());

                    Question1.setText(ans1);
                    Question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void OnBackBtn(View view) {
        finish();
    }
}

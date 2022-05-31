package com.mac.ekchitthi.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.MainActivity;
import com.mac.ekchitthi.R;


import java.util.concurrent.TimeUnit;

public class OTP_Verification_Activity extends AppCompatActivity {
    private EditText input1, input2, input3, input4, input5, input6;
    private TextView intentPhoneNumber, resendOtpBtn;
    private String phoneNumber, backEndOtp, finalOtp, countryCode, username, status;
    private Button proceedBtn;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private int userAvailable2 = 0;
    private int userAvailable = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        initialiseWidgets();
        checkValidation();
        autoNext();
        resendOTP();
    }


    private void initialiseWidgets() {
        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        input3 = findViewById(R.id.input3);
        input4 = findViewById(R.id.input4);
        input5 = findViewById(R.id.input5);
        input6 = findViewById(R.id.input6);
        progressBar = findViewById(R.id.progressBar);
        resendOtpBtn = findViewById(R.id.resendOtpBtn);
        intentPhoneNumber = findViewById(R.id.intentPhoneNumber);
        proceedBtn = findViewById(R.id.proceedBtn);
        resendOtpBtn = findViewById(R.id.resendOtpBtn);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        backEndOtp = getIntent().getStringExtra("backEndOtp");
        countryCode = getIntent().getStringExtra("countryCode");
        username = getIntent().getStringExtra("username");
        status = getIntent().getStringExtra("status");
        intentPhoneNumber.setText(countryCode + "- " + phoneNumber);
        reference = FirebaseDatabase.getInstance().getReference().child("User");

    }

    private void checkValidation() {

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input1.getText().toString().isEmpty() && !input2.getText().toString().isEmpty() && !input3.getText().toString().isEmpty() && !input4.getText().toString().isEmpty() && !input5.getText().toString().isEmpty() && !input6.getText().toString().isEmpty()) {
                    finalOtp = input1.getText().toString() + input2.getText().toString() + input3.getText().toString() + input4.getText().toString() + input5.getText().toString() + input6.getText().toString();

                    VerifyOTP();


                } else {
                    Toast.makeText(OTP_Verification_Activity.this, "Enter all numbers", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }


    private void autoNext() {
        input1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input5.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void VerifyOTP() {

        progressBar.setVisibility(View.VISIBLE);
        proceedBtn.setVisibility(View.GONE);

        if (backEndOtp != null) {

//                        if(finalOtp.equals(backEndOtp)){
//
//                            Toast.makeText(OTP_Verification_Activity.this, "Verified", Toast.LENGTH_SHORT).show();
//
//
//                        }else{
//                            progressBar.setVisibility(View.VISIBLE);
//                            proceedBtn.setVisibility(View.GONE);
//                            Toast.makeText(OTP_Verification_Activity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
//                        }

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backEndOtp, finalOtp);

            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    proceedBtn.setVisibility(View.VISIBLE);

                    if (task.isSuccessful()) {
                        final FirebaseAuth auth;
                        final FirebaseUser user;
                        auth = FirebaseAuth.getInstance();
                        user = auth.getCurrentUser();


                        int finalStatus = Integer.parseInt(status);
                        if (finalStatus == 0) {

                            reference.orderByKey().equalTo("+91"+phoneNumber).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists()) {
                                        userAvailable2 = 1;
                                        Toast.makeText(OTP_Verification_Activity.this, "userAvailable2", Toast.LENGTH_SHORT).show();
                                    } else {
                                        userAvailable2 = 0;


                                    }

                                    if (userAvailable2 == 0) {

                                        if (user != null) {
                                            User_Details_Model user_details_model = new User_Details_Model(username, phoneNumber, "", user.getUid(),"");
                                            reference.child(user.getPhoneNumber()).child("user info").setValue(user_details_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        Intent intent = new Intent(OTP_Verification_Activity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);

                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(OTP_Verification_Activity.this, "User not inserted", Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                        }

                                    } else {

                                        Intent intent = new Intent(OTP_Verification_Activity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            reference.orderByKey().equalTo(user.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        userAvailable = 1;
                                    } else {
                                        userAvailable = 0;


                                    }


                                    if (userAvailable == 1) {

                                        Intent intent = new Intent(OTP_Verification_Activity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);


                                    } else{

                                        User_Details_Model user_details_model = new User_Details_Model(username, phoneNumber, "", user.getUid(),"");
                                        reference.child(user.getPhoneNumber()).child("user info").setValue(user_details_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Intent intent = new Intent(OTP_Verification_Activity.this, Create_New_Account_Activity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("status","phone");
                                                    startActivity(intent);

                                                }

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(OTP_Verification_Activity.this, "User not inserted", Toast.LENGTH_SHORT).show();

                                            }
                                        });



                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }


                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressBar.setVisibility(View.GONE);
                    proceedBtn.setVisibility(View.VISIBLE);

                }
            });

        } else {
            progressBar.setVisibility(View.GONE);
            proceedBtn.setVisibility(View.VISIBLE);
            Toast.makeText(OTP_Verification_Activity.this, "OTP not received", Toast.LENGTH_SHORT).show();

        }


    }


    private void resendOTP() {
        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCode + phoneNumber, 60, TimeUnit.SECONDS, OTP_Verification_Activity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String newOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(newOTP, forceResendingToken);
                        backEndOtp = newOTP;
                        Toast.makeText(OTP_Verification_Activity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
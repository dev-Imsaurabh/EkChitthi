package com.mac.ekchitthi.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

public class PhoneNumber_Activity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private String TAG = "PhoneNumberActivity";
    private EditText countryCodeBox, phoneNumberBox;
    private Button signUpBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText name;
    private RelativeLayout signUp_main;
    private int status = 0;
    private TextView aha, ask_login_btn, ask_signUpWithGoogle, username_title, signUpTagline, signUpHeading;
    private CardView google_btn;
    private GoogleSignInOptions gso;
    private DatabaseReference reference;
    private ProgressDialog pd;
    private int userAvailable = 0;
    private int userAvailable2 = 0;
    private String logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        initialisingWidgets();
        checkValidation();
        settingHintSizeManually();
        AddingTextWatcher();
        ClickOnAskLoginBtn();
        ClickOnGoogleBtn();
    }


    private void settingHintSizeManually() {
        name.setHint(Html.fromHtml("<small><small>" + "enter your name" + "</small></small>"));
        phoneNumberBox.setHint(Html.fromHtml("<small><small>" + "enter your phone number" + "</small></small>"));
    }


    private void initialisingWidgets() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        countryCodeBox = findViewById(R.id.countryCodeBox);
        phoneNumberBox = findViewById(R.id.phoneNumberBox);
        signUpBtn = findViewById(R.id.signUpBtn);
        progressBar = findViewById(R.id.progressBar);
        name = findViewById(R.id.name);

        //Play with them
        aha = findViewById(R.id.aha);
        ask_login_btn = findViewById(R.id.ask_login_btn);
        ask_signUpWithGoogle = findViewById(R.id.ask_signUpWithGoogle);
        username_title = findViewById(R.id.username_title);
        signUpTagline = findViewById(R.id.signUpTagline);
        signUpHeading = findViewById(R.id.signUp_heading);
        signUp_main = findViewById(R.id.signUp_main);
        google_btn = findViewById(R.id.google_btn);

        try {
            logout=getIntent().getStringExtra("main");
            if(logout!=null){

                if(!logout.equals("")){
                    ChangeToLogin();
                    status=1;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        pd = new ProgressDialog(this);

        //Google Signin
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .requestProfile()
                .build();
        reference = FirebaseDatabase.getInstance().getReference().child("User");

    }

    private void checkValidation() {

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status == 0) {
                    if (!phoneNumberBox.getText().toString().isEmpty() && !countryCodeBox.getText().toString().isEmpty() && !name.getText().toString().isEmpty()) {
                        if (phoneNumberBox.getText().toString().trim().length() == 10 && countryCodeBox.getText().toString().trim().length() == 3) {

                            progressBar.setVisibility(View.VISIBLE);
                            signUpBtn.setVisibility(View.GONE);
                            OtpOProcessStart();

                        } else {
                            Toast.makeText(PhoneNumber_Activity.this, "Please check your phone number and country code !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PhoneNumber_Activity.this, "Please enter phone number , country code and username !", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (!phoneNumberBox.getText().toString().isEmpty() && !countryCodeBox.getText().toString().isEmpty()) {
                        if (phoneNumberBox.getText().toString().trim().length() == 10 && countryCodeBox.getText().toString().trim().length() == 3) {

                            progressBar.setVisibility(View.VISIBLE);
                            signUpBtn.setVisibility(View.GONE);
                            OtpOProcessStart();

                        } else {
                            Toast.makeText(PhoneNumber_Activity.this, "Please check your phone number and country code !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PhoneNumber_Activity.this, "Please enter phone number and country code !", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void OtpOProcessStart() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCodeBox.getText().toString().trim() + phoneNumberBox.getText().toString().trim(), 60, TimeUnit.SECONDS, PhoneNumber_Activity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressBar.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                progressBar.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.VISIBLE);

                Toast.makeText(PhoneNumber_Activity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCodeSent(@NonNull String backEndOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(backEndOtp, forceResendingToken);

                progressBar.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.VISIBLE);
                Intent intent = new Intent(PhoneNumber_Activity.this, OTP_Verification_Activity.class);
                intent.putExtra("phoneNumber", phoneNumberBox.getText().toString());
                intent.putExtra("backEndOtp", backEndOtp);
                intent.putExtra("countryCode", countryCodeBox.getText().toString());
                intent.putExtra("username", name.getText().toString());
                intent.putExtra("status", String.valueOf(status));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onStart() {

        if (user != null) {

            Intent intent = new Intent(PhoneNumber_Activity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        super.onStart();
    }

    private void AddingTextWatcher() {
        phoneNumberBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (phoneNumberBox.getText().length() < 10) {
                    phoneNumberBox.setTextColor(Color.RED);
                } else {
                    phoneNumberBox.setTextColor(Color.GREEN);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void ClickOnAskLoginBtn() {

        ask_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PerformAnimation();

            }
        });

    }

    private void PerformAnimation() {

        signUp_main.animate().alpha(0.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                signUp_main.setVisibility(View.GONE);

                signUp_main.animate().alpha(1.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (status == 0) {
                            ChangeToLogin();

                            status = 1;
                        } else {
                            ChangeToSignUp();

                            status = 0;
                        }
                        signUp_main.setVisibility(View.VISIBLE);


                    }
                });


            }
        });
    }

    private void ChangeToSignUp() {
        aha.setText("Already Have An Account? ");
        ask_login_btn.setText("Login");
        ask_signUpWithGoogle.setText("Sign up with Google");
        signUpHeading.setText("Sign Up!");
        signUpTagline.setText("Create an account so you can order your\nfavourite products easily and quickly");
        signUpBtn.setText("Register Now");
        SignUpSetVisibilityToVisible();
    }

    private void SignUpSetVisibilityToVisible() {
        name.setVisibility(View.VISIBLE);
        username_title.setVisibility(View.VISIBLE);
    }

    private void ChangeToLogin() {
        aha.setText("Not A Member? ");
        ask_login_btn.setText("Join Now");
        ask_signUpWithGoogle.setText("Sign in with Google");
        signUpHeading.setText("Welcome Back!");
        signUpTagline.setText("Sign in to your account.");
        signUpBtn.setText("Login");
        SignUpSetVisibilityToGone();
    }

    private void SignUpSetVisibilityToGone() {
        name.setVisibility(View.GONE);
        username_title.setVisibility(View.GONE);
    }

    private void ClickOnGoogleBtn() {
        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();


            }
        });
    }

    private void signIn() {
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                pd.setMessage("Just a moment...");
                pd.setCancelable(false);
                pd.show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {


        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseAuth auth2;
                            FirebaseUser user2;
                            auth2 = FirebaseAuth.getInstance();
                            user2 = auth2.getCurrentUser();
                            auth = auth2;
                            user = user2;


                            if (status == 0) {

                                reference.orderByKey().equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            userAvailable2 = 1;
                                            Toast.makeText(PhoneNumber_Activity.this, "userAvailable2", Toast.LENGTH_SHORT).show();
                                        } else {
                                            userAvailable2 = 0;


                                        }

                                        if (userAvailable2 == 0) {
                                            if (user != null) {
                                                User_Details_Model user_details_model = new User_Details_Model(user.getDisplayName(), "", user.getEmail(), user.getUid(),user.getPhotoUrl().toString());
                                                reference.child(user.getUid()).child("user info").setValue(user_details_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            pd.dismiss();

                                                            Intent intent = new Intent(PhoneNumber_Activity.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);

                                                            Toast.makeText(PhoneNumber_Activity.this, "Sign in successful", Toast.LENGTH_SHORT).show();

                                                        }

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pd.dismiss();

                                                        Toast.makeText(PhoneNumber_Activity.this, "Sign in unsuccessful", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }

                                        } else {

                                            Intent intent = new Intent(PhoneNumber_Activity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                            Toast.makeText(PhoneNumber_Activity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();

                                        }


                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            } else {

                                reference.orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                                        if (snapshot.exists()) {
                                            userAvailable = 1;
                                        } else {
                                            userAvailable = 0;


                                        }


                                        if (userAvailable == 1) {
                                            Intent intent = new Intent(PhoneNumber_Activity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        } else {


                                            Intent intent = new Intent(PhoneNumber_Activity.this, Create_New_Account_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("status","google");

                                            startActivity(intent);

                                        }


                                        pd.dismiss();
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//
//        if(user!=null){
//            auth.signOut();
//        }
    }
}
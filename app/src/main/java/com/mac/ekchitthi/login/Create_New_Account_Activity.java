package com.mac.ekchitthi.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.MainActivity;
import com.mac.ekchitthi.R;

import java.util.HashMap;


public class Create_New_Account_Activity extends AppCompatActivity {

    private Button CNA;
    private FirebaseAuth auth;
    private FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private String status;
    private TextView info,txt_userName;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        CNA=findViewById(R.id.CNA);
        info=findViewById(R.id.info);
        txt_userName=findViewById(R.id.txt_userName);
        status=getIntent().getStringExtra("status");
        reference= FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("user info");

        if(status.equals("phone")){

            info.setText("Congratulations!\nWe have created new account\nfor you, cuz we didn't find any\nexisting account related to this\nphone number :)");
            CNA.setText("I am excited to see my account!");

        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        CNA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status.equals("google")){

                    auth.signOut();

                    // Google sign out
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(Create_New_Account_Activity.this, PhoneNumber_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                }else{
                    if(!txt_userName.getText().toString().isEmpty()){
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    HashMap hp = new HashMap();
                                    hp.put("username",txt_userName.getText().toString());
                                    reference.updateChildren(hp).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {

                                            Intent intent = new Intent(Create_New_Account_Activity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





                    }else{
                        Toast.makeText(Create_New_Account_Activity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    }



                }



            }
        });


    }
}
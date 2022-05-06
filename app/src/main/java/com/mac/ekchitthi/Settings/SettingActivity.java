package com.mac.ekchitthi.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mac.ekchitthi.R;
import com.mac.ekchitthi.login.PhoneNumber_Activity;
import com.squareup.picasso.Picasso;

public class SettingActivity extends AppCompatActivity {
    private TextView user_name;
    private FirebaseAuth auth;
    private Button btn_logout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private ImageView ad_app_icon1,add_app_icon2,ad_app_icon3;
    private Button install_btn1,install_btn2,install_btn3;
    private FirebaseMessaging firebaseMessaging;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        FindViewByID();
        SetupUser();

    }

    private void SetupUser() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("username").getValue(String.class);
                user_name.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void FindViewByID() {
        user_name=findViewById(R.id.user_name);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        btn_logout=findViewById(R.id.btn_logout);
        ad_app_icon1=findViewById(R.id.ad_app_icon1);
        add_app_icon2=findViewById(R.id.ad_app_icon2);
        ad_app_icon3=findViewById(R.id.ad_app_icon3);
        install_btn1=findViewById(R.id.install_btn1);
        install_btn2=findViewById(R.id.install_btn2);
        install_btn3=findViewById(R.id.install_btn13);
        firebaseMessaging=FirebaseMessaging.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("user info");

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(SettingActivity.this, PhoneNumber_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("main","logout");
                firebaseMessaging.unsubscribeFromTopic(user.getUid());
                startActivity(intent);
            }
        });
        Picasso.get().load("https://play-lh.googleusercontent.com/w7Rehewr8Wg5uCstbnn5kFZ-rS3dfQDX5ZFxFYdH6p97ARIpFgAJzac4rXvXM-VpxQ=s180-rw").into(ad_app_icon1);
        Picasso.get().load("https://play-lh.googleusercontent.com/p6qdQBImjECkyY4HMowK3RsGG_tnkD0E92RgkY4TLik2fpFAHZTC3qUoasFIdhXuGA8=s180-rw").into(add_app_icon2);
        Picasso.get().load("https://play-lh.googleusercontent.com/i81CCkpNfExlQVZer00lWsDas1b_0nSNV4UZht10pitn3-oyoV7Mx08CSIrlkWRPYA=s180-rw").into(ad_app_icon3);

        install_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPlayStore("com.mac.picoholic");

            }
        });

        install_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPlayStore("com.mac.bmicalculator");


            }
        });

        install_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPlayStore("com.mac.whatkilometers");


            }
        });


    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        startService(new Intent(SettingActivity.this, EkChitthiBackgroundServices.class));
//
//
//    }

    private void openPlayStore(String packageName){
        try {
            Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            appStoreIntent.setPackage(packageName);

            startActivity(appStoreIntent);
        } catch (android.content.ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
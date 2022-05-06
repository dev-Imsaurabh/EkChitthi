package com.mac.ekchitthi.Letter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.R;

import java.sql.Timestamp;
import java.util.ArrayList;

public class OldLettersActivity extends AppCompatActivity {
    private DatabaseReference reference, reference1,reference2;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<LetterModel> list;
    private LetterAdapter adapter;
    private RecyclerView rv_old;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_letters);
        rv_old = findViewById(R.id.rv_old);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("letters");
        reference2= FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("mCount");
        reference1 = FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0, 4) + "CurrentTimeStamp");
        rv_old.setLayoutManager(new LinearLayoutManager(this));
        rv_old.setHasFixedSize(true);
        GetTimeStamp();

    }

    public void GetTimeStamp() {

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timeStamp = Long.parseLong(snapshot.getValue().toString())/1000;
                try {
//                    Toast.makeText(getContext(), String.valueOf(timeStamp), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(timeStamp);


                    }
                },1000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void getData(long serverTimestamp) {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        LetterModel data = snapshot1.getValue(LetterModel.class);


                        Timestamp timestamp = new Timestamp(serverTimestamp);
                        Timestamp timestamp2 = new Timestamp(Long.parseLong(data.getFuture_timestamp()));
                        if (timestamp.compareTo(timestamp2) > 0&&!data.getRead_status().equals("")) {
                            list.add(0, data);

                        }



                    }

                    adapter=new LetterAdapter(OldLettersActivity.this,list);
                    adapter.notifyDataSetChanged();
                    rv_old.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
//                    reference2.setValue(list.size());



                } else {
                    progressBar.setVisibility(View.GONE);
                }

//                SharedPreferences pref = context.getSharedPreferences("post", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("count", String.valueOf(list.size()));
//                editor.apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        GetTimeStamp();
    }
}
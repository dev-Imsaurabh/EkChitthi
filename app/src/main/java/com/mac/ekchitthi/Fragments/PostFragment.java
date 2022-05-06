package com.mac.ekchitthi.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.Letter.LetterAdapter;
import com.mac.ekchitthi.Letter.LetterModel;
import com.mac.ekchitthi.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class PostFragment extends Fragment {

    private DatabaseReference reference, reference1,reference2;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<LetterModel> list;
    private LetterAdapter adapter;
    private RecyclerView rv_post;
    private ProgressBar progressBar;
    private View no_post;
    Context context ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        context=getContext();
        FindViewByID(view);

        return view;
    }

    private void FindViewByID(View view) {

        rv_post = view.findViewById(R.id.rv_post);
        progressBar = view.findViewById(R.id.progressBar);
        no_post = view.findViewById(R.id.no_post);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("letters");
        reference2= FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("mCount");
        reference1 = FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0, 4) + "CurrentTimeStamp");
        rv_post.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_post.setHasFixedSize(true);
        GetTimeStamp();


    }

    public void GetTimeStamp() {

        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
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

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        LetterModel data = snapshot1.getValue(LetterModel.class);


                        Timestamp timestamp = new Timestamp(serverTimestamp);
                        Timestamp timestamp2 = new Timestamp(Long.parseLong(data.getFuture_timestamp()));
                        if (timestamp.compareTo(timestamp2) > 0&&data.getRead_status().equals("")) {
                            list.add(0, data);

                        }



                    }
                    if(list.isEmpty()){
                        no_post.setVisibility(View.VISIBLE);
                    }
                    if(!list.isEmpty()){
                        Collections.reverse(list);
                    }

                    adapter=new LetterAdapter(getContext(),list);
                    adapter.notifyDataSetChanged();
                    rv_post.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
//                    reference2.setValue(list.size());



                } else {
                    no_post.setVisibility(View.VISIBLE);
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
    public void onResume() {
        super.onResume();
        GetTimeStamp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                reference2.setValue(list.size());
//
//
//            }
//        },100);



    }
}
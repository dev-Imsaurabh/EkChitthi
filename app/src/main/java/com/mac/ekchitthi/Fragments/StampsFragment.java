package com.mac.ekchitthi.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.R;
import com.mac.ekchitthi.Stamp.StampModel;
import com.mac.ekchitthi.Stamp.Stamps_Adapter;

import java.util.ArrayList;

public class StampsFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private RecyclerView rv_stamps;
    private ArrayList<StampModel> list;
    private Stamps_Adapter stamps_adapter;
    private ProgressBar progressBar;
    private View no_stamps;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_stamps, container, false);

        FindViewByID(view);


        return  view;
    }

    private void FindViewByID(View view) {

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("stamps");
        rv_stamps=view.findViewById(R.id.rv_stamps);
        progressBar=view.findViewById(R.id.progressBar);
        no_stamps=view.findViewById(R.id.no_stamp);

        GridLayoutManager gridLayoutManager= new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        rv_stamps.setLayoutManager(gridLayoutManager);
        rv_stamps.setHasFixedSize(true);
        list=new ArrayList<>();
        getData();


    }

    private void getData() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        StampModel data = snapshot1.getValue(StampModel.class);
                        list.add(0,data);
                    }
                    stamps_adapter = new Stamps_Adapter(getContext(),list);
                    stamps_adapter.notifyDataSetChanged();
                    rv_stamps.setAdapter(stamps_adapter);
                    no_stamps.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.GONE);
                    no_stamps.setVisibility(View.VISIBLE);


                }

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
        getData();
    }
}
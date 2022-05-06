package com.mac.ekchitthi.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.Letter.LetterModel;
import com.mac.ekchitthi.R;
import com.mac.ekchitthi.Tracking.TrackAdapter;

import java.util.ArrayList;

public class StatusFragment extends Fragment {
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<LetterModel>list;
    private TrackAdapter adapter;
    private RecyclerView rv_track;
    private ProgressBar progressBar;
    private View no_status;
    private Button delete_all_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_status, container, false);

        FindViewByID(view);
        return  view;
    }

    private void FindViewByID(View view) {
        rv_track=view.findViewById(R.id.rv_tracking);
        progressBar=view.findViewById(R.id.progressBar);
        no_status=view.findViewById(R.id.no_status);
        delete_all_btn=view.findViewById(R.id.delete_all_btn);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        list=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("tracks");

        rv_track.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_track.setHasFixedSize(true);
        delete_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete all tracks ?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "All Deleted", Toast.LENGTH_SHORT).show();
                              getData();
                            }
                        });

                    }

                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });

                builder.show();

            }
        });
        getData();

    }

    private void getData() {
        list.clear();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    delete_all_btn.setVisibility(View.VISIBLE);

                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        LetterModel data = snapshot1.getValue(LetterModel.class);
                        list.add(0,data);
                    }
                    adapter = new TrackAdapter(getContext(),list);
                    adapter.notifyDataSetChanged();
                    rv_track.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                }else{
                    no_status.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }
}
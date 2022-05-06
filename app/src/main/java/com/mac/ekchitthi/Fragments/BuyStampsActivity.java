package com.mac.ekchitthi.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.MainActivity;
import com.mac.ekchitthi.R;
import com.mac.ekchitthi.Stamp.StampModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BuyStampsActivity extends AppCompatActivity {
    private TextView txt_dist, txt_date, txt_stampDays, txt_stampWord, txt_from, txt_to, stamp_id;
    private Elements elements;
    private Elements elements1;
    private Document doc;
    private EditText et_from, et_to, et_max;
    private Button btn_calculate, btn_addStamp;
    private LinearLayout mainLayout;
    private String[] finalDist;
    private ProgressDialog pd;
    private int averageTravelInKm = 325;
    private int numberOfDays;
    private CardView stampCard;
    private FirebaseAuth auth;
    private ImageView iv_stamp;
    private String stampID;
    private String globalDistance,maxWord,from,to,stamp_image;
    String todaysDate;
    private FirebaseUser user;
    private String time, date;
    private DatabaseReference reference;
    private long timeStamp;
    private String auto_gen;
    private String reply_from;
    private String reply_to;



    private String finalLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_stamps);
        auto_gen=getIntent().getStringExtra("auto_gen");
        reply_from=getIntent().getStringExtra("from");
        reply_to=getIntent().getStringExtra("to");



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        txt_dist = findViewById(R.id.txt_dist);
        et_to = findViewById(R.id.et_to);
        et_from = findViewById(R.id.et_from);
        btn_calculate = findViewById(R.id.btn_calculate);
        mainLayout = findViewById(R.id.mainLayout);
        stampCard = findViewById(R.id.stampCard);
        btn_addStamp=findViewById(R.id.btn_addStamp);

        stamp_id = findViewById(R.id.stamp_id);
        et_max = findViewById(R.id.et_max);
        txt_stampWord = findViewById(R.id.txt_stampWord);
        txt_from = findViewById(R.id.txt_from);
        txt_to = findViewById(R.id.txt_to);
        txt_stampDays = findViewById(R.id.txt_stampDays);
        txt_date = findViewById(R.id.txt_date);
        iv_stamp = findViewById(R.id.iv_stamp);

        pd = new ProgressDialog(this);
        reference=FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("stamps");

        btn_addStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddStamp();
            }
        });


        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stampCard.setVisibility(View.GONE);
                finalLast = "";
                pd.setMessage("Calculating...");
                pd.setCancelable(false);
                if (!et_from.getText().toString().isEmpty() && !et_to.getText().toString().isEmpty() && !et_max.getText().toString().isEmpty()) {
                    pd.show();
                    contents contents = new contents();

                    contents.execute();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                }else{
                    Snackbar.make(btn_calculate, "Please enter all the fields", Snackbar.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                }


            }
        });

        try {
            if(auto_gen!=null){
                AutoGenerate();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void AutoGenerate() {
        pd.setMessage("Calculating...");
        pd.setCancelable(false);
        pd.show();
        et_from.setText(reply_from);
        et_to.setText(reply_to);
        et_max.setText("500");
        contents contents = new contents();

        contents.execute();






    }


    private class contents extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try {


                String url = "https://www.google.com/search?q=distance+between" + et_from.getText().toString().trim() + "to" + et_to.getText().toString().trim();
                String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
//                String userAgent = "Mozilla/5.0 (Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36";

                doc = Jsoup.connect(url).userAgent(userAgent).get();
                elements = doc.getElementsByClass("BbbuR uc9Qxb uE1RRc");
                if (elements.text().isEmpty()) {
                    elements1 = doc.getElementsByClass("dDoNo FzvWSb vk_bk");


                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            try {


                if (elements.text().isEmpty()) {

//                    Snackbar.make(btn_calculate, elements1.text(), Snackbar.LENGTH_SHORT).show();


                    try {
                        if (elements1.text().contains("km")) {

                            finalDist = elements1.text().split(",");
                            finalLast = finalDist[0] + finalDist[1].replaceAll("km", "");
//                            txt_dist.setText(finalLast+ "km");

                        } else if (elements1.text().contains("mi")) {
                            finalDist = elements1.text().split(",");
                            finalLast = finalDist[0] + finalDist[1].replaceAll("mi", "");
                            double milesinkm = Double.parseDouble(finalLast) * 1.609;
                            finalLast = String.valueOf(milesinkm);
//                            txt_dist.setText(String.valueOf(finalLast)+" km");


                        }


                    } catch (Exception e) {
                        e.printStackTrace();


                    }

                } else {
//                    Snackbar.make(btn_calculate, elements.text(), Snackbar.LENGTH_SHORT).show();

                    String finalDistance = elements.text().substring(elements.text().indexOf("(") + 1, elements.text().indexOf(")"));


                    try {
                        finalDist = finalDistance.split(",");
                        finalLast = finalDist[0] + finalDist[1].replaceAll("km", "");
//                        txt_dist.setText(finalLast+" km");


                    } catch (Exception e) {
                        e.printStackTrace();
//                        txt_dist.setText(finalDistance.replaceAll("km","")+"km");
                        GenerateStamp(finalDistance.replaceAll("km", ""));


                    }

                }

                pd.dismiss();
                if (!finalLast.isEmpty()) {
                    GenerateStamp(finalLast);

                } else {
                    pd.dismiss();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (stampCard.getVisibility() == View.GONE) {
                Toast.makeText(BuyStampsActivity.this, "Not Available", Toast.LENGTH_SHORT).show();
            }


        }


    }

    private void GenerateStamp(String distance) {
        pd.dismiss();
        if (!distance.isEmpty()) {
            stampCard.setVisibility(View.VISIBLE);
            txt_dist.setText("Distance: " + distance + " km");
            stampID = user.getUid().substring(0, 4) + System.currentTimeMillis();

            stamp_id.setText("ID: " + stampID);
            maxWord=et_max.getText().toString();
            txt_stampWord.setText("Max word weight: " + maxWord + " words");
            from=et_from.getText().toString().trim();
            txt_from.setText("From: " + from);
            to=et_to.getText().toString().trim();
            txt_to.setText("To: " + to);

            String[] breakKm = distance.trim().split("\\.");
            globalDistance=breakKm[0];
            numberOfDays = Integer.parseInt(breakKm[0]) / averageTravelInKm;

            if (numberOfDays <= 1) {
                numberOfDays = 1;
                stamp_image="1";
                txt_stampDays.setText("Total delivery days: " + String.valueOf(numberOfDays) + " day");
                iv_stamp.setImageResource(R.drawable.basic_stamp);


            } else if (numberOfDays == 2) {
                stamp_image="2";
                iv_stamp.setImageResource(R.drawable.stand_stamp);
                txt_stampDays.setText("Total delivery days: " + String.valueOf(numberOfDays) + " days");

            } else if (numberOfDays > 2) {
                stamp_image="3";
                iv_stamp.setImageResource(R.drawable.premium_stamp);
                txt_stampDays.setText("Total delivery days: " + String.valueOf(numberOfDays) + " days");
            }

            GeneratedOn();


        }


    }


    private void GeneratedOn() {


        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0, 4) + "CurrentTimeStamp");
        reference.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (snapshot.exists()) {
                                    long CurrentTimeStamp = Long.parseLong(snapshot.getValue().toString());
//                                    time = DateUtils.formatDateTime(BuyStampsActivity.this, Long.parseLong(String.valueOf(CurrentTimeStamp)), DateUtils.FORMAT_SHOW_TIME);
//                                    date = DateUtils.formatDateTime(BuyStampsActivity.this, Long.parseLong(String.valueOf(CurrentTimeStamp)), DateUtils.FORMAT_SHOW_DATE);
                                    Date df = new java.util.Date(CurrentTimeStamp);
                                    String vv = new SimpleDateFormat("dd-MM-yy,h:mm a").format(df);
                                    todaysDate = vv;
                                    txt_date.setText("Generated on: " + todaysDate);
                                    timeStamp=CurrentTimeStamp;

                                    if(auto_gen!=null){
                                        AddStamp();
                                    }


                                }


                            }
                        }, 10);




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    private void AddStamp() {

        StampModel stampModel= new StampModel(stampID,globalDistance,String.valueOf(numberOfDays),maxWord.trim(),from,to,todaysDate,stamp_image,timeStamp);
        reference.child(stampID).setValue(stampModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(BuyStampsActivity.this, "Successfully added to your Stamps box", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(BuyStampsActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("stamp_call","1");
//                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });






    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        startService(new Intent(BuyStampsActivity.this, EkChitthiBackgroundServices.class));
//
//
//    }


}
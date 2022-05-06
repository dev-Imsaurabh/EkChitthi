package com.mac.ekchitthi.LetterBox;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.Fragments.BuyStampsActivity;
import com.mac.ekchitthi.Letter.LetterModel;
import com.mac.ekchitthi.MainActivity;
import com.mac.ekchitthi.R;
import com.mac.ekchitthi.Stamp.StampModel;
import com.mac.ekchitthi.Stamp.Stamps_Adapter_dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NewLetterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Dialog dialog, buy_more_schedule_dialog,reply_dialog;
    private TextView select_stamp;
    private EditText s_from, s_to, r_letter;
    private TextView r_distance, r_days, r_date, maxWords, changing_words, dummyTxt, r_Number, s_name, invite_btn;
    private ImageView stamp_image;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference, reference1, reference2, reference3,reference4;
    private String[] count;
    private ImageView choose_contact;
    private ScrollView mainLayout;
    private static final int RESULT_PICK_CONTACT1 = 1;
    private ArrayList<String> keyList;
    boolean available = false;
    private Button send_letter;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    private Dialog invite_dialog, sent_dialog;
    private boolean clickOnStamp = false, customdate = false;
    private String currentTimestamp;
    private String newTimeStamp;
    private Button sch_btn;
    private TextView schedule_count;
    private String sent_time;
    private String uid;
    private int leftSchedule;
    private BillingClient billingClient;
    private List<String> skuList;
    private String intentSenderNumber,from,to;
    private int clicked=0;
    private boolean blocked=false;
    private boolean SpecialAccess=false;
    private ArrayList<String>blockedList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_letter);
        intentSenderNumber=getIntent().getStringExtra("sender_number");
        from=getIntent().getStringExtra("from");
        to=getIntent().getStringExtra("to");
        FindViewByID();
        getBlockList();
        CheckSpecialAccess();
        FireStamp();

        try {
            if(intentSenderNumber!=null){
                Reply();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBlockList() {
        reference1.child(r_Number.getText().toString()).child("blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blockedList = new ArrayList<>();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    blockedList.add(snapshot1.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckSpecialAccess() {
        reference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    String getNumbers = snapshot1.getValue(String.class);
                    if(getNumbers.equals(user.getPhoneNumber())){
                        SpecialAccess=true;
                        Toast.makeText(NewLetterActivity.this, "specialAccess", Toast.LENGTH_SHORT).show();

                    }

                }
                getSchedule();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void Reply() {
        r_Number.setEnabled(false);
        choose_contact.setEnabled(false);
        r_Number.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                r_Number.setText(intentSenderNumber);
                reply_dialog = new Dialog(NewLetterActivity.this);
                reply_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reply_dialog.setContentView(R.layout.generate_reply_stamp_dialog);
                reply_dialog.setCancelable(false);

                Button gen_btn = reply_dialog.findViewById(R.id.gen_btn);
                TextView replyFrom=reply_dialog.findViewById(R.id.replyFrom);
                TextView replyTo=reply_dialog.findViewById(R.id.replyTo);
                replyFrom.setText("From :"+from);
                replyTo.setText("To :"+to);




                gen_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clicked+=1;
                        reply_dialog.dismiss();
                        Intent intent = new Intent(NewLetterActivity.this, BuyStampsActivity.class);
                        intent.putExtra("auto_gen","1");
                        intent.putExtra("from",from);
                        intent.putExtra("to",to);
                        startActivity(intent);



                    }
                });


                reply_dialog.show();


            }
        },500);

    }

    private void getSchedule() {
        try {
            reference1.child(user.getPhoneNumber()).child("schedule").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        leftSchedule = Integer.parseInt(snapshot.child("left").getValue(String.class));
                        if(SpecialAccess){
                            schedule_count.setText("You have unlimited Schedule");

                        }else{
                            schedule_count.setText("You have " + String.valueOf(leftSchedule) + " Schedule left");


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FindViewByID() {

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        skuList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0, 4) + "CurrentTimeStamp");
        reference1 = FirebaseDatabase.getInstance().getReference().child("User");
        reference2 = FirebaseDatabase.getInstance().getReference().child("User");
        reference4 = FirebaseDatabase.getInstance().getReference().child("specialAccess");

        select_stamp = findViewById(R.id.selectStamp);
        s_from = findViewById(R.id.s_from);
        schedule_count = findViewById(R.id.schedule_count);
        invite_btn = findViewById(R.id.invite_btn);
        s_name = findViewById(R.id.r_Name);
        s_to = findViewById(R.id.s_to);
        r_distance = findViewById(R.id.r_distance);
        r_days = findViewById(R.id.r_days);
        r_date = findViewById(R.id.r_date);
        maxWords = findViewById(R.id.max_words);
        stamp_image = findViewById(R.id.stamp_image);
        dummyTxt = findViewById(R.id.dummyTxt);
        r_letter = findViewById(R.id.r_letter);
        changing_words = findViewById(R.id.changing_words);
        mainLayout = findViewById(R.id.mainLayout);
        r_Number = findViewById(R.id.r_Number);
        choose_contact = findViewById(R.id.choose_contact);
        send_letter = findViewById(R.id.send_letter);
        sch_btn = findViewById(R.id.sch_btn);
        keyList = new ArrayList<>();

        SelectStamp();
        WordCount();
        getKeys();
        CheckNumber();
        ChooseContactButton();
        SendButtonAction();
        CustomDatePicker();

        invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onShareClicked();


            }
        });

    }


    private void SendButtonAction() {
        send_letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidation();
            }
        });
    }

    private void CheckValidation() {
        for(String list : blockedList){
            if(list.contains(user.getPhoneNumber())){
                blocked=true;

            }

        }

        if (!available) {
            r_Number.setError("Invalid Recipient");
            r_Number.requestFocus();
//            Toast.makeText(NewLetterActivity.this, "Please Enter The Correct Recipient Number", Toast.LENGTH_SHORT).show();
        } else if (s_from.getText().toString().equals("")) {
            Toast.makeText(NewLetterActivity.this, "Please Choose The Stamp", Toast.LENGTH_SHORT).show();
        }
        else if (r_letter.getText().toString().equals("")) {
            Toast.makeText(NewLetterActivity.this, "Please Write The Letter", Toast.LENGTH_SHORT).show();
        }else if(blocked){
            OpenBlockDialog();

        } else {
            SendLetter();
        }

    }

    private void OpenBlockDialog() {
        android.app.AlertDialog.Builder builder= new android.app.AlertDialog.Builder(this);
        builder.setMessage("Sorry,You are blocked by this user :(");
        builder.setCancelable(true);
       builder.show();


    }

    private void ChooseContactButton() {
        choose_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestContactPermission();
            }
        });
    }

    private void openContactList() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT1);
    }

    private void getKeys() {

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String numbers = snapshot1.getKey();
                        keyList.add(numbers);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void CheckNumber() {

        r_Number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                available = false;
                blocked=false;
                if (r_Number.getText().length() == 13) {
                    for (int j = 0; j < keyList.size(); j++) {
                        if (keyList.get(j).equals(r_Number.getText().toString())) {
                            available = true;
                        }
                    }
                    if (available) {
                        Toast.makeText(NewLetterActivity.this, "Available", Toast.LENGTH_SHORT).show();
                        getBlockList();
                        r_Number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                        reference1.child(r_Number.getText().toString()).child("user info").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String getUid = snapshot.child("uid").getValue(String.class);
                                    uid = getUid;
//                                    Toast.makeText(NewLetterActivity.this, uid, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        r_Number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invalid, 0);
                        Toast.makeText(NewLetterActivity.this, "Not Available", Toast.LENGTH_SHORT).show();
                        uid = "";
                        invite_dialog = new Dialog(NewLetterActivity.this);
                        invite_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        invite_dialog.setContentView(R.layout.invite_dialog);
                        invite_dialog.setCancelable(true);
                        Button inviteBtn = invite_dialog.findViewById(R.id.invite_btn);
                        inviteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onShareClicked();
                            }
                        });
                        invite_dialog.show();


                    }
                } else {
                    uid = "";
                    r_Number.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!r_Number.getText().toString().contains("+")) {


                    if (r_Number.getText().length() == 10) {


                        Toast.makeText(NewLetterActivity.this, "Please enter number with your\ncountry code", Toast.LENGTH_SHORT).show();
                        r_Number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invalid, 0);


                    }

                }


            }
        });


    }

    private void WordCount() {

        r_letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r_letter.setEnabled(true);
            }
        });
        r_letter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Integer.parseInt(changing_words.getText().toString().trim()) < Integer.parseInt(maxWords.getText().toString())) {


                }


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (maxWords.getText().toString().equals("0")) {

                    Toast.makeText(NewLetterActivity.this, "Please select stamp", Toast.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);


                } else if (Integer.parseInt(changing_words.getText().toString()) > Integer.parseInt(maxWords.getText().toString())) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    Toast.makeText(NewLetterActivity.this, "Word limit exceeded", Toast.LENGTH_SHORT).show();


                }

//                else {
//                    done = false;
//                }

                if (Integer.parseInt(maxWords.getText().toString()) != 0) {

                    String currentText = editable.toString();
                    count = currentText.trim().split(" ");
                    changing_words.setText(String.valueOf(count.length));

                }


            }
        });


    }

    private void SelectStamp() {


        select_stamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (customdate) {
                    customdate = false;
                }
                clickOnStamp = true;
                dialog = new Dialog(NewLetterActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.stamp_dialog);
                dialog.setCancelable(true);
                DatabaseReference reference;
                FirebaseAuth auth;
                FirebaseUser user;
                final Stamps_Adapter_dialog[] adapter = new Stamps_Adapter_dialog[1];
                ArrayList<StampModel> list;
                list = new ArrayList<>();
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();

                reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("stamps");
                RecyclerView rv = dialog.findViewById(R.id.rv_dialog_stamp);
                TextView no_stamp_info = dialog.findViewById(R.id.no_stamp_info);
                Button gen_btn = dialog.findViewById(R.id.gen_btn);
                gen_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent intent = new Intent(NewLetterActivity.this, BuyStampsActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                rv.setLayoutManager(new LinearLayoutManager(NewLetterActivity.this));
                rv.setHasFixedSize(true);
                ProgressBar pb = dialog.findViewById(R.id.dialog_pb);


                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                StampModel data = snapshot1.getValue(StampModel.class);
                                list.add(0, data);

                            }

                            adapter[0] = new Stamps_Adapter_dialog(NewLetterActivity.this, list, dialog, s_from, s_to, r_distance, r_days, r_date, maxWords, stamp_image, dummyTxt);
                            adapter[0].notifyDataSetChanged();
                            rv.setAdapter(adapter[0]);
                            pb.setVisibility(View.GONE);
                        } else {
                            pb.setVisibility(View.GONE);
                            no_stamp_info.setVisibility(View.VISIBLE);
                            gen_btn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pb.setVisibility(View.GONE);

                    }
                });


                dialog.show();
            }
        });
    }

    private void FireStamp() {
        reference.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                long l = Long.parseLong(snapshot.getValue().toString());
                                sent_time = String.valueOf(l);
//                                Toast.makeText(NewLetterActivity.this, sent_time, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }, 100);

            }
        });

    }

    private void onShareClicked() {

        String link = "https://play.google.com/store/apps/details?id=" + getPackageName();

        Uri uri = Uri.parse(link);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "hey, I am inviting you to download" +
                " this amazing Letter posting App !!\n*******" +
                "Ek Chitthi*******\n" + uri.toString());
        intent.putExtra(Intent.EXTRA_TITLE, "Download Ek Chitthi App !");

        startActivity(Intent.createChooser(intent, "Invite People"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT1:
                    contactPicked1(data);
                    break;
            }
        } else {
        }
    }

    private void contactPicked1(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            name = cursor.getString(nameIndex);
            phoneNo = cursor.getString(phoneIndex);
            if (phoneNo.contains("+91")) {
                String a = phoneNo.replaceAll("-", "");
                String b = a.replaceAll("\\s", "");
                r_Number.setText(b);
            } else {
                String a = phoneNo.replaceAll("-", "");
                String b = a.replaceAll(" ", "");
                r_Number.setText("+91" + b);

            }
//            s_name.setText(name);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , 100);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            100);
                }
            } else {
                openContactList();
            }
        } else {
            openContactList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openContactList();
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    private void SendLetter() {
        sent_dialog = new Dialog(NewLetterActivity.this);
        sent_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sent_dialog.setContentView(R.layout.sent_dialog);
        sent_dialog.setCancelable(false);
        sent_dialog.show();

        Button track_btn = sent_dialog.findViewById(R.id.buy_more_btn);
        TextView sent_text = sent_dialog.findViewById(R.id.sent_text);

        LottieAnimationView animationView = sent_dialog.findViewById(R.id.animationView);

        track_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sent_dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(NewLetterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("stamp_call", "2");
                        startActivity(intent);

                    }
                }, 500);

            }
        });

        String[] futureTimeStamp = dummyTxt.getText().toString().split(",");
        String name;

        if (s_name.getText().toString().trim().equals("")) {
            name = "Anonymous" + "," + sent_time;
        } else {
            name = s_name.getText().toString().trim() + "," + sent_time;

        }

        if (!customdate) {
            currentTimestamp = futureTimeStamp[0];

        } else {
            if (newTimeStamp != null) {
                currentTimestamp = newTimeStamp;
                if(!SpecialAccess){
                    DeductSchedule();
                }


            } else {

                currentTimestamp = futureTimeStamp[0];


            }
        }


        LetterModel letterModel = new LetterModel(user.getPhoneNumber(), r_letter.getText().toString().trim(), "", currentTimestamp, r_date.getText().toString().trim(), name, s_from.getText().toString(), futureTimeStamp[1], futureTimeStamp[2], r_Number.getText().toString(), s_to.getText().toString());

        reference2.child(r_Number.getText().toString().trim()).child("letters").child(futureTimeStamp[1]).setValue(letterModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                reference3 = FirebaseDatabase.getInstance().getReference().child("notifications");


                String key = reference3.push().getKey();
                reference3.child(key).setValue(currentTimestamp + "," + uid + "," + key + "," + name);

                reference1.child(user.getPhoneNumber()).child("stamps").child(futureTimeStamp[1]).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                        reference1.child(user.getPhoneNumber()).child("tracks").child(futureTimeStamp[1]).setValue(letterModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                animationView.setAnimation(R.raw.sent_anim);
                                animationView.playAnimation();
                                track_btn.setVisibility(View.VISIBLE);
                                sent_text.setVisibility(View.VISIBLE);

                            }
                        });

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void DeductSchedule() {
        reference1.child(user.getPhoneNumber()).child("schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap hp = new HashMap();
                    int finalLeft = leftSchedule - 1;
                    hp.put("left", String.valueOf(finalLeft));
                    reference1.child(user.getPhoneNumber()).child("schedule").updateChildren(hp);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        startService(new Intent(NewLetterActivity.this, EkChitthiBackgroundServices.class));
//
//
//    }

    private void CustomDatePicker() {
        sch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickOnStamp) {
                    if(!SpecialAccess){

                        if (leftSchedule > 0) {
                            if(stamp_image.getDrawable()!=null){
                                CallCal();

                            }else{
                                Toast.makeText(NewLetterActivity.this, "Select Stamp", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            OpenBuyMoreScheduleDialog();
                        }

                    }else{
                        if(stamp_image.getDrawable()!=null){
                            CallCal();

                        }else{
                            Toast.makeText(NewLetterActivity.this, "Select Stamp", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(NewLetterActivity.this, "Please select stamp", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void OpenBuyMoreScheduleDialog() {

        buy_more_schedule_dialog = new Dialog(NewLetterActivity.this);
        buy_more_schedule_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buy_more_schedule_dialog.setContentView(R.layout.buy_more_schedule_dialog);
        buy_more_schedule_dialog.setCancelable(true);
        buy_more_schedule_dialog.show();

        Button buy_more_btn = buy_more_schedule_dialog.findViewById(R.id.buy_more_btn);
        Button buy_more_btn1 = buy_more_schedule_dialog.findViewById(R.id.buy_more_btn1);
        Button buy_more_btn2 = buy_more_schedule_dialog.findViewById(R.id.buy_more_btn2);
        buy_more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skuList.clear();
                skuList.add("schedule_8");
                StartBilling();

            }
        });
        buy_more_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skuList.clear();
                skuList.add("schedule_20");
                StartBilling();
            }
        });

        buy_more_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skuList.clear();
                skuList.add("schedule_45");
                StartBilling();
            }
        });

    }

    private void StartBilling() {
        buy_more_schedule_dialog.dismiss();


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {

                                    for (SkuDetails skuDetails : skuDetailsList) {

                                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuDetails)
                                                .build();
                                        int responseCode = billingClient.launchBillingFlow(NewLetterActivity.this, billingFlowParams).getResponseCode();


                                    }


                                }
                            });


                }
            }

            @Override
            public void onBillingServiceDisconnected() {


            }
        });
    }

    private void CallCal() {
        customdate = true;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(NewLetterActivity.this, NewLetterActivity.this, year, month, day);
        datePickerDialog.show();

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewLetterActivity.this, NewLetterActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, myYear);
        calendar.set(Calendar.MONTH, myMonth);
        calendar.set(Calendar.DAY_OF_MONTH, myday);
        calendar.set(Calendar.HOUR_OF_DAY, myHour);
        calendar.set(Calendar.MINUTE, myMinute);
        calendar.set(Calendar.SECOND, 0);
        Long timestamp = calendar.getTimeInMillis() / 1000;
        newTimeStamp = String.valueOf(timestamp);
        Long timestamp2 = calendar.getTimeInMillis();

        String time = DateUtils.formatDateTime(NewLetterActivity.this, Long.parseLong(String.valueOf(timestamp2)), DateUtils.FORMAT_SHOW_TIME);
        String date = DateUtils.formatDateTime(NewLetterActivity.this, Long.parseLong(String.valueOf(timestamp2)), DateUtils.FORMAT_SHOW_DATE);


        r_date.setText(time + "," + date);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sent_dialog != null && sent_dialog.isShowing()) {
            sent_dialog.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(clicked==1){
            Target target = new ViewTarget(R.id.selectStamp, NewLetterActivity.this);
            ShowcaseView.Builder res = new ShowcaseView.Builder(NewLetterActivity.this, true)
                    .setTarget(target)
                    .setContentTitle("Select Stamp")
                    .hideOnTouchOutside()
                    .setContentText("You have to select stamp to set destination");
            res.build();
        }
        SelectStamp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sent_dialog != null && sent_dialog.isShowing()) {
            sent_dialog.cancel();
        }
    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK & list != null) {

                if(skuList.get(0).equals("schedule_8")){
                    updateSchedule(8);
                }else if(skuList.get(0).equals("schedule_20")){
                    updateSchedule(20);


                }else if(skuList.get(0).equals("schedule_45")){
                    updateSchedule(45);


                }
            }
        }


    };

    private void updateSchedule(int i) {
        switch (i){
            case 8:
                int newSchedule = leftSchedule+8;
                updateSchduleDB(newSchedule,8);
                break;

            case 20:
                int newSchedule1 = leftSchedule+20;
                updateSchduleDB(newSchedule1, 20);
                break;

            case 45:
                int newSchedule2 = leftSchedule+45;
                updateSchduleDB(newSchedule2, 45);
                break;

        }
    }

    private void updateSchduleDB( int newSchedule,int purchasedSchedule) {
        reference1.child(user.getPhoneNumber()).child("schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap hp = new HashMap();
                hp.put("left",String.valueOf(newSchedule));
                reference1.child(user.getPhoneNumber()).child("schedule").updateChildren(hp).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        Toast.makeText(NewLetterActivity.this, "you got "+String.valueOf(purchasedSchedule)+" new Schedules", Toast.LENGTH_SHORT).show();
                        leftSchedule=newSchedule;
                        schedule_count.setText("You have "+String.valueOf(leftSchedule)+" Schedule left");

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
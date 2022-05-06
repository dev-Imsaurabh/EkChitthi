package com.mac.ekchitthi;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mac.ekchitthi.Fragments.BuyStampsActivity;
import com.mac.ekchitthi.Letter.OldLettersActivity;
import com.mac.ekchitthi.LetterBox.NewLetterActivity;
import com.mac.ekchitthi.Service2.ForegroundService;
import com.mac.ekchitthi.Settings.SettingActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button fab;
    private int btnStatus = 0;
    private Toolbar toolbar;
    private EditText test;
    private String stamp_call;
    private DatabaseReference reference,reference1,reference2;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseMessaging firebaseMessaging;
    private TextView location;
    private Document doc;
    private Elements elements, elements1;
    private AlarmManager alarmManager;
    private Dialog dialog;
    private LinearLayout bmac;
    private TextView older_letters_btn;
    private static final String workTag ="ECservice";
    private int versionCode ;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        checkWorker();


//        Toast.makeText(this, getDeviceName(), Toast.LENGTH_SHORT).show();




//
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
//                dialog = new Dialog(MainActivity.this);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.setContentView(R.layout.remove_optimization_dialog);
//                dialog.setCancelable(false);
//                Button button_settings = dialog.findViewById(R.id.btn_settings);
//                button_settings.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                        askIgnoreOptimization();
//                    }
//                });
//               android.os.Handler handler  = new android.os.Handler();
//               handler.postDelayed(new Runnable() {
//                   @Override
//                   public void run() {
//
//                       dialog.show();
//
//
//                   }
//               },1000);
//
//            } else {
////                Toast.makeText(this, "all ready optimized", Toast.LENGTH_SHORT).show();
//                // already ignoring battery optimization code here next you want to do
//            }
//        } else {
//            // already ignoring battery optimization code here next you want to do
//        }


        NotificationManagerCompat.from(this).cancelAll();

//
//       if(!checkServiceRunning(ForegroundService.class)){
////           Toast.makeText(this, "not running", Toast.LENGTH_SHORT).show();
//           startForegroundService(new Intent(this,ForegroundService.class));
//       }else {
////           Toast.makeText(this, "running", Toast.LENGTH_SHORT).show();
//       }


        stamp_call = getIntent().getStringExtra("stamp_call");


        FindViewByID();


        checkNetwork();
        getVersion();
        FabClick();
        FireStamp();
        CheckAndSetScheduling();

        location location = new location();
        location.execute();


    }

    private void getVersion() {

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            String version = pInfo.versionName;
             versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String code = snapshot.getValue(String.class);

                if(versionCode!=Integer.parseInt(code)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("⚠️New version of this app is available on PlayStore.\nUpdate the app to enjoy uninterrupted services.\nEk Chitthi Developer.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            openPlayStore(getPackageName());
                            dialogInterface.dismiss();

                        }
                    });

                    builder.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();

                        }
                    });
                    builder.show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckAndSetScheduling() {

        reference1=FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("schedule");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){

                    HashMap hp = new HashMap();
                    hp.put("left","3");
                    reference1.setValue(hp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void checkWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                MyWorkerClass.class,20, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(workTag, ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest);
//        WorkManager.getInstance().enqueue(periodicWorkRequest);
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void askIgnoreOptimization() {

//        Intent intent = new Intent();
//        String packageName = getPackageName();
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (pm.isIgnoringBatteryOptimizations(packageName))
//            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//        else {
//            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//            intent.setData(Uri.parse("package:" + packageName));
//        }
//        startActivity(intent);
//        Intent intent = new Intent();
//        intent.setData(Uri.parse("package:"+getPackageName()));
//
//        startActivityForResult(intent, 0);

//
//
//        try {
//            Intent intent1 = new Intent();
//            intent1.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
//            intent1.putExtra("package_name", getPackageName());
//            intent1.putExtra("package_label", getText(R.string.app_name));
//            startActivity(intent1);
//        } catch (ActivityNotFoundException anfe) {
//
//            if(getDeviceName().startsWith("Realme")){
//
//                try {
//                    Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }else{
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.AlertDialog_AppCompat)
//                        .setTitle("Important Procedure ⚠️")
//                        .setCancelable(false)
//                        .setMessage("Setting > Battery > Manage App in Background(or as per your device).\n\nAllow Ek Chitthi App to run in Background !")
//                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
//
//
//                            }
//                        });
//                alertDialog.show();
//
//
//            }




//        }


//    }

//    private void SetAlarm() {
//        alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, EkChitthiBroadCastReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
//        final Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(1650088560);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
//        Toast.makeText(MainActivity.this, "alarm set", Toast.LENGTH_SHORT).show();
//
//
//    }

//    private void createNotiChannel() {
//        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
//            CharSequence name = "EkChitthiService";
//            String description = "Ek chitthi channel";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("ekchitthi",name,importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//
//        }

//    }


    private void FabClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (btnStatus) {
                    case 0:
//                       Toast.makeText(MainActivity.this, "new post", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, NewLetterActivity.class));

                        break;

                    case 2:
                        startActivity(new Intent(MainActivity.this, BuyStampsActivity.class));
                        break;
                }
            }
        });
    }

    private void FindViewByID() {
        viewPager = findViewById(R.id.viewPager_user);
        tabLayout = findViewById(R.id.tabLayout_user);
        older_letters_btn=findViewById(R.id.btn_old_letters);
        fab = findViewById(R.id.fab);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseMessaging= FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic(user.getUid());
        location = findViewById(R.id.location);
        reference = FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0, 4) + "CurrentTimeStamp");
        reference2 = FirebaseDatabase.getInstance().getReference().child("update");
        toolbar = findViewById(R.id.toolbar);
        bmac=findViewById(R.id.bmac);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SettingUpViewPagerWithTabLayout();
//        FireStamp();

        try {
            if(stamp_call!=null){
                if (stamp_call.equals("1")) {
                    viewPager.setCurrentItem(2);
                } else if (stamp_call.equals("2")) {
                    viewPager.setCurrentItem(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        bmac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, BMAC.class));
            }
        });

        older_letters_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OldLettersActivity.class));
            }
        });

    }

    private void FireStamp() {
        reference.setValue(ServerValue.TIMESTAMP);
    }

    private void SettingUpViewPagerWithTabLayout() {

        ViewPagerUserAdapter adapter = new ViewPagerUserAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {

                    fab.setText("Generate");
                    fab.setVisibility(View.VISIBLE);
                    btnStatus = 2;

                } else if (position == 0) {

                    fab.setText("new post");
                    fab.setVisibility(View.VISIBLE);
                    btnStatus = 0;


                } else if (position == 1) {
                    fab.setVisibility(View.GONE);
                    btnStatus = 1;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Settings:

                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class location extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
//           String url = "https://www.google.com/search?q=in+which+city+i+am";
            String url = "https://www.google.com/search?q=my+location";
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

            try {
                doc = Jsoup.connect(url).userAgent(userAgent).get();
                elements = doc.getElementsByClass("desktop-title-content");
                elements1 = doc.getElementsByClass("desktop-title-subcontent");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            try {
                location.setText(elements1.text());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();


    }


    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                        return true;
                    }
                }
            }
        }


        return false;

    }

    private void checkNetwork() {

        if (!isNetworkAvailable() == true) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setTitle("Internet Connection Alert")
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
        } else if (isNetworkAvailable() == true) {

        }
    }


    public boolean checkServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

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


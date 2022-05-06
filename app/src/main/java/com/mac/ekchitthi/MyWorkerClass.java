package com.mac.ekchitthi;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mac.ekchitthi.Service2.ForegroundService;


public class MyWorkerClass extends Worker {
    private static final String TAG ="MyWorkerClass";
    public MyWorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
        if(!checkServiceRunning(ForegroundService.class)){
//           Toast.makeText(this, "not running", Toast.LENGTH_SHORT).show();
            getApplicationContext().startForegroundService(new Intent(getApplicationContext(),ForegroundService.class));
        }else {
            Log.d(TAG, "doWork: Worker already started");
//           Toast.makeText(this, "running", Toast.LENGTH_SHORT).show();
        }
        return Result.success();
    }

    public boolean checkServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
}

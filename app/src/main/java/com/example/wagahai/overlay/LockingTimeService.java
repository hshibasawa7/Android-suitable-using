package com.example.wagahai.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by wagahai on 2018/01/31.
 */

public class LockingTimeService extends Service {
    private WindowManager wm;
    private View lockingTimeView;
    private TextView clockTextView;
    private ProgressBar progressBar;

    private static final int REPEAT_INTERVAL = 1000;
    private Handler mHandler = new Handler();

    private int lockingTime;
    private long startTime;


    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        lockingTimeView = View.inflate(this, R.layout.locking_time, null);

        clockTextView = lockingTimeView.findViewById(R.id.textView10);
        progressBar = lockingTimeView.findViewById(R.id.progressBar);

        lockingTimeView.setBackgroundColor(0xFFFFFFFF);
        /*
        lockingTimeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE );
        //View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
*/
        WindowManager.LayoutParams wmLP = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                //WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ,


                0,
                PixelFormat.TRANSLUCENT);
        //wmLP.gravity = Gravity.LEFT | Gravity.TOP;
        wm.addView(lockingTimeView, wmLP);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lockingTime = intent.getExtras().getInt("lockingTime");
        startTime = intent.getExtras().getLong("startTime");

        clockTextView.setText(UsingTimeActivity.toClockString(lockingTime));
        progressBar.setMax(lockingTime);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if ( updateTime() <= 0 ) {
                    finishLockingTime();
                } else {
                    mHandler.postDelayed(this, REPEAT_INTERVAL);
                }
            }
        };
        updateTime();
        mHandler.postDelayed(runnable, REPEAT_INTERVAL);

        return START_NOT_STICKY;
        //return START_STICKY_COMPATIBILITY;
    }

    public int updateTime() {
        long now = SystemClock.elapsedRealtime();
        int progress = (int)((now - startTime)/1000); //Log.d("usingTimeActivity", "progress: "+progress);
        int rest = lockingTime - progress;

        clockTextView.setText(UsingTimeActivity.toClockString(rest));
        progressBar.setProgress(progress);

        return rest;
    }

    public void finishLockingTime() {
        Intent intent = new Intent(this, SetTimeService.class);
        startService(intent);

        wm.removeView(lockingTimeView);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

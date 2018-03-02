package com.example.wagahai.overlay;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


/**
 * Created by wagahai on 2018/01/23.
 */

public class OverlayActivity extends Activity {
    private static final int REPEAT_INTERVAL = 3000;
    Handler mHandler = new Handler();

    private TextView mTextView, mTextView2, mTextView3, mTextView4;
    private KeyguardManager mKeyguardManager;
    boolean isDeviceLocked = false;
    private long lockedTime = 0;
    private long unlockedTime = 0;

    private boolean isBackground = false;
    //private String packageName = "";
    //private UsageStatsManager usageStatsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_textbox);

        //packageName = this.getPackageName();
        //usageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        View overlayView = LayoutInflater.from(this).inflate(R.layout.test_textbox, null);
        overlayView.setBackgroundColor(0x99FF0000);

        mTextView = overlayView.findViewById(R.id.textView);
        mTextView2 = overlayView.findViewById(R.id.textView2);
        mTextView3 = overlayView.findViewById(R.id.textView3);
        mTextView4 = overlayView.findViewById(R.id.textView4);

        WindowManager.LayoutParams wmLP = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        wmLP.gravity = Gravity.RIGHT | Gravity.TOP;
        wm.addView(overlayView, wmLP);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateStatus();

                mHandler.postDelayed(this, REPEAT_INTERVAL);
            }
        };
        updateStatus();
        mHandler.postDelayed(runnable, REPEAT_INTERVAL);

    }

    @Override
    protected void onStart() {
        super.onStart();

        isBackground = false;
        Log.d("lifecycle", "start");
    }

    @Override
    protected void onStop() {
        super.onStop();

        isBackground = true;
        Log.d("lifecycle", "stop");

    }

    private void updateStatus() {
        long now = SystemClock.elapsedRealtime();
        mTextView.setText("now: " + now/1000);
        if (mKeyguardManager.isDeviceLocked()) {
            if (isDeviceLocked) {

            } else {
                lockedTime = now;
                isDeviceLocked = true;
            }
        } else {
            if (isDeviceLocked) {
                unlockedTime = now;
                isDeviceLocked = false;
            } else {

            }

        }
        mTextView2.setText("lockedTime: " + lockedTime/1000);
        mTextView3.setText("unlockedTime: " + unlockedTime/1000);
        //boolean isKeyguardLocked = mKeyguardManager.isKeyguardLocked();
        //mTextView2.setText("keyguardLocked: " + isKeyguardLocked);

        //boolean isBackground = usageStatsManager.isAppInactive(packageName);
        if (isBackground) {
            mTextView4.setText("Background");
        } else {
            mTextView4.setText("Foreground");
        }

    }
}

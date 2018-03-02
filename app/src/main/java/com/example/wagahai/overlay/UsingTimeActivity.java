package com.example.wagahai.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

/**
 * Created by wagahai on 2018/01/27.
 */

public class UsingTimeActivity extends Activity {
    private long startTime;//ms
    private int usingTime;//sec

    private static final int REPEAT_INTERVAL = 1000;
    private Handler mHandler = new Handler();

    private WindowManager wm;
    private View overlayView;
    private ProgressBar overlayProgressBar;

    private TextView clockTextView;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.using_time);

        startTime = getIntent().getExtras().getLong("startTime");
        usingTime = getIntent().getExtras().getInt("usingTime");

        clockTextView = findViewById(R.id.textView10);
        clockTextView.setText(toClockString(usingTime));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(usingTime);

        wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        overlayView = View.inflate(this, R.layout.overlay_upper_progress_bar, null);
        overlayProgressBar = overlayView.findViewById(R.id.progressBar2);
        overlayProgressBar.setMax(usingTime);

        WindowManager.LayoutParams wmLP = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        wmLP.gravity = Gravity.TOP;
        wm.addView(overlayView, wmLP);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if ( updateTime() <= 0 ) {
                    finishUsingTime();
                } else {
                    mHandler.postDelayed(this, REPEAT_INTERVAL);
                }
            }
        };
        updateTime();
        mHandler.postDelayed(runnable, REPEAT_INTERVAL);


    }

    static public String toClockString(int time) {
        //h:mm:ss
        int h = time/3600;
        int m = time%3600/60;
        int s = time%60;
        return String.format("%d:%02d:%02d", h, m, s);
    }
    public int updateTime() {
        long now = SystemClock.elapsedRealtime();
        int progress = (int)((now - startTime)/1000); //Log.d("usingTimeActivity", "progress: "+progress);
        int rest = usingTime - progress;

        clockTextView.setText(toClockString(rest));
        progressBar.setProgress(progress);

        overlayProgressBar.setProgress(progress);

        return rest;
    }
    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("onKeyDown", "Back button pushed.");
            /*
            選択肢： 1. 何もしない 2. バックグラウンドへ 3. アプリ終了
             */

            return true;
        }
        return false;
    }
    public void finishUsingTime() {
        Intent intent = new Intent(this, LockingTimeService.class);
        intent.putExtra("lockingTime", usingTime);
        intent.putExtra("startTime", SystemClock.elapsedRealtime());
        startService(intent);
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeView(overlayView);
        Log.d("usingTime", "finished");

    }
}


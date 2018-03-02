package com.example.wagahai.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wagahai on 2018/01/26.
 */

public class SetTimeService extends Service {
    private static final int MIN_USING_TIME = 180;
    WindowManager wm;
    View setTimeView;
    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        setTimeView = View.inflate(this, R.layout.set_time, null);
        final NumberPicker numberPicker = setTimeView.findViewById(R.id.numberPicker);
        final NumberPicker numberPicker2 = setTimeView.findViewById(R.id.numberPicker2);
        final NumberPicker numberPicker3 = setTimeView.findViewById(R.id.numberPicker3);
        Button button = setTimeView.findViewById(R.id.button);
        final TextView textViewError = setTimeView.findViewById(R.id.text_error);

        numberPicker.setMaxValue(3);
        numberPicker.setMinValue(0);
        numberPicker2.setMaxValue(5);
        numberPicker2.setMinValue(0);
        numberPicker3.setMaxValue(9);
        numberPicker3.setMinValue(0);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int usingTime = numberPicker.getValue()*3600 + numberPicker2.getValue()*600 + numberPicker3.getValue()*60;
                if (usingTime < MIN_USING_TIME) {
                    Log.d("SetTimeService", "usingTime: " + usingTime);
                    String errorMessage = "入力は無効です。" + MIN_USING_TIME/60 + "分以上の時間を設定してください。";
                    textViewError.setText(errorMessage);
                } else {
                    Intent intent = new Intent(v.getContext(), UsingTimeActivity.class);
                    intent.putExtra("usingTime", usingTime);//int
                    intent.putExtra("startTime", SystemClock.elapsedRealtime());//long
                    startActivity(intent);

                    wm.removeView(setTimeView);
                    //Toast.makeText(v.getContext(), "Start, " + usingTime + " sec", Toast.LENGTH_LONG).show();
                    stopSelf();

                }
            }
        });


        setTimeView.setBackgroundColor(0xFFFFFFFF);
        /*
        setTimeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
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
        wm.addView(setTimeView, wmLP);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

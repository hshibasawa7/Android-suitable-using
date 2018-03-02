package com.example.wagahai.overlay;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private boolean mDispInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        setContentView(R.layout.activity_main);

        //findViewById(R.id.mainLayout).setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        Button dispInfoButton = findViewById(R.id.button);
        dispInfoButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OverlayActivity.class);
                startActivity(intent);
                mDispInfo = true;
            }
        });

        Button setTimeServiceStartButton = findViewById(R.id.button2);
        setTimeServiceStartButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SetTimeService.class);
                startService(intent);
            }
        });
*/

        setContentView(R.layout.using_time);
        Intent intent = new Intent(this, SetTimeService.class);
        startService(intent);


        //TextView text = (TextView)findViewById(R.id.textView);
        //text.setText("test message");



    }
}

package com.brennangambling.awarenesstesting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.awareness.fence.AwarenessFence;

public class MainActivity extends Activity {

    TextView vehicleTextView;
    TextView bicycleTextView;
    TextView footTextView;
    TextView walkingTextView;
    TextView runningTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}

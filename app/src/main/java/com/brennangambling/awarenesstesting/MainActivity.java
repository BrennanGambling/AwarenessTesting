package com.brennangambling.awarenesstesting;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private TextView vehicleTextView;
    private TextView bicycleTextView;
    private TextView footTextView;
    private TextView walkingTextView;
    private TextView runningTextView;
    private TextView stillTextView;
    private TextView tiltingTextView;
    private TextView unknownTextView;
    private TextView headphoneTextView;

    private AwarenessFence vehicleFence;
    private AwarenessFence bicycleFence;
    private AwarenessFence footFence;
    private AwarenessFence walkingFence;
    private AwarenessFence runningFence;
    private AwarenessFence stillFence;
    private AwarenessFence tiltingFence;
    private AwarenessFence unknownFence;
    private AwarenessFence headphoneFence;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Main Activity", "onCreate called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get TextViews for displaying status of each of the following activities.
        vehicleTextView = (TextView) findViewById(R.id.inVehicleText);
        bicycleTextView = (TextView) findViewById(R.id.onBicycleText);
        footTextView = (TextView) findViewById(R.id.onFootText);
        walkingTextView = (TextView) findViewById(R.id.walkingText);
        runningTextView = (TextView) findViewById(R.id.runningText);
        stillTextView = (TextView) findViewById(R.id.stillText);
        tiltingTextView = (TextView) findViewById(R.id.tiltingText);
        unknownTextView = (TextView) findViewById(R.id.unknownText);
        headphoneTextView = (TextView) findViewById(R.id.headphonesText);

        //set all TextViews text to N/A.
        TextView[] textViews = {vehicleTextView, bicycleTextView, footTextView, walkingTextView, runningTextView, stillTextView, tiltingTextView, unknownTextView, headphoneTextView};
        for (TextView current : textViews) {
            current.setText("N/A");
        }

        if (googleApiClient == null) {
            Log.d("Main Activity", "Building Google API Client.");
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Awareness.API)
                    .build();
        }

        vehicleFence = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
        bicycleFence = DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE);
        footFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
        walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
        runningFence = DetectedActivityFence.during(DetectedActivityFence.RUNNING);
        stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
        tiltingFence = DetectedActivityFence.during(DetectedActivityFence.TILTING);
        unknownFence = DetectedActivityFence.during(DetectedActivityFence.UNKNOWN);
        headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
    }

    @Override
    protected void onDestroy() {
        Log.d("Main Activity", "onDestroy called.");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d("Main Activity", "onStart called.");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("Main Activity", "onStop called.");
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Main Activity", "onConnected called.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Main Activity", "onConnectionSuspended called.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Main Activity", "onConnectionFailed called.");
    }
}

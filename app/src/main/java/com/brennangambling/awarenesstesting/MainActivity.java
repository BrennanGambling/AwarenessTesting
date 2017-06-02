package com.brennangambling.awarenesstesting;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import static com.google.android.gms.internal.zzt.TAG;

public class MainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    static private final String FENCE_ACTION = "WK8Gh9mTvve8T92qVtSp";

    static private final String VEHICLE_KEY = "tOUNgsjbYL4t2kd2OcTX";
    static private final String BICYCLE_KEY = "fZfKmVtrSzNrks0oooYW";
    static private final String FOOT_KEY = "oiPEdAnWN7KQs7k0C6jK";
    static private final String WALKING_KEY = "RNNueGrEwxKSfzeOFJ12";
    static private final String RUNNING_KEY = "drNVr5D08denKnW4hcBC";
    static private final String STILL_KEY = "ht5eSdcg3IemR37eo2iD";
    static private final String TILTING_KEY = "9l2VHeupiS3tLgqjvKLO";
    static private final String UNKNOWN_KEY = "NqK6TmIrMs48CPkybWE1";
    static private final String HEADPHONE_KEY = "KhJmptvo9uvPC3FR0EkA";

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

    private PendingIntent fencePendingIntent;
    private IntentFilter fenceIntentFilter;
    private FenceBroadcastReceiver fenceBroadcastReceiver;

    /*private boolean vehicleState;
    private boolean bicycleState;
    private boolean footState;
    private boolean walkingState;
    private boolean runningState;
    private boolean stillState;
    private boolean tiltingState;
    private boolean unknownState;
    private boolean headphoneState;*/

    private GoogleApiClient googleApiClient;

    private static void debugLogging(Context context, String tag, String message) {
        Log.d(tag, message);
        Toast toast = Toast.makeText(context, tag + ": " + message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debugLogging(this,"MainActivity", "onCreate called.");
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

        Intent intent = new Intent(FENCE_ACTION);
        fencePendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                10001,
                intent,
                0);
        fenceIntentFilter = new IntentFilter(FENCE_ACTION);
        fenceBroadcastReceiver = new FenceBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        debugLogging(this, "MainActivity", "onDestroy called.");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        debugLogging(this, "MainActivity", "onStart called.");
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        Awareness.FenceApi.updateFences(
                googleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(VEHICLE_KEY, vehicleFence, fencePendingIntent)
                        .addFence(BICYCLE_KEY, bicycleFence, fencePendingIntent)
                        .addFence(FOOT_KEY, footFence, fencePendingIntent)
                        .addFence(WALKING_KEY, walkingFence, fencePendingIntent)
                        .addFence(RUNNING_KEY, runningFence, fencePendingIntent)
                        .addFence(STILL_KEY, stillFence, fencePendingIntent)
                        .addFence(TILTING_KEY, tiltingFence, fencePendingIntent)
                        .addFence(UNKNOWN_KEY, unknownFence, fencePendingIntent)
                        .addFence(HEADPHONE_KEY, headphoneFence, fencePendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) {
                            Log.d(TAG, "Fence was successfully registered.");
                        } else {
                            Log.e(TAG, "Fence was NOT successfully registered.: " + status);
                        }
                    }
                });

        registerReceiver(fenceBroadcastReceiver, fenceIntentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        debugLogging(this, "MainActivity", "onStop called.");
        if (googleApiClient != null && (googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            googleApiClient.disconnect();
        }

        Awareness.FenceApi.updateFences(
                googleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(VEHICLE_KEY)
                        .removeFence(BICYCLE_KEY)
                        .removeFence(FOOT_KEY)
                        .removeFence(WALKING_KEY)
                        .removeFence(RUNNING_KEY)
                        .removeFence(STILL_KEY)
                        .removeFence(TILTING_KEY)
                        .removeFence(UNKNOWN_KEY)
                        .removeFence(HEADPHONE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {


            @Override
            public void onSuccess(@NonNull Status status) {
                Log.d(TAG, "Fences successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.e(TAG, "Fences were NOT successfully removed.");
            }
        });


        unregisterReceiver(fenceBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        debugLogging(this, "MainActivity", "onSaveInstanceState called.");
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(VEHICLE_KEY, vehicleTextView.getText().toString());
        savedInstanceState.putString(BICYCLE_KEY, bicycleTextView.getText().toString());
        savedInstanceState.putString(FOOT_KEY, footTextView.getText().toString());
        savedInstanceState.putString(WALKING_KEY, walkingTextView.getText().toString());
        savedInstanceState.putString(RUNNING_KEY, runningTextView.getText().toString());
        savedInstanceState.putString(STILL_KEY, stillTextView.getText().toString());
        savedInstanceState.putString(TILTING_KEY, tiltingTextView.getText().toString());
        savedInstanceState.putString(UNKNOWN_KEY, unknownTextView.getText().toString());
        savedInstanceState.putString(HEADPHONE_KEY, headphoneTextView.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        debugLogging(this, "MainActivity", "onRestoreInstanceState called.");
        super.onRestoreInstanceState(savedInstanceState);
        vehicleTextView.setText(savedInstanceState.getString(VEHICLE_KEY));
        bicycleTextView.setText(savedInstanceState.getString(BICYCLE_KEY));
        footTextView.setText(savedInstanceState.getString(FOOT_KEY));
        walkingTextView.setText(savedInstanceState.getString(WALKING_KEY));
        runningTextView.setText(savedInstanceState.getString(RUNNING_KEY));
        stillTextView.setText(savedInstanceState.getString(STILL_KEY));
        tiltingTextView.setText(savedInstanceState.getString(TILTING_KEY));
        unknownTextView.setText(savedInstanceState.getString(UNKNOWN_KEY));
        headphoneTextView.setText(savedInstanceState.getString(HEADPHONE_KEY));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        debugLogging(this, "MainActivity", "onConnected called.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        debugLogging(this, "MainActivity", "onConnectionSuspended called.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        debugLogging(this, "MainActivity", "onConnectionFailed called. Retrying connection.");
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    class FenceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            debugLogging(context, "FenceBroadcastReceiver", "onReceive called.");
            FenceState fenceState = FenceState.extract(intent);
            if(fenceState.getFenceKey().equals(VEHICLE_KEY)) {

            }
        }

        private void setState(Context context, String fenceName, TextView textView, FenceState fenceState) {
            /*switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    debugLogging(context, "FenceBroadcastReceiver:", fenceName + " is true.");
                    break;
                case FenceStat
            }*/
        }


    }
}

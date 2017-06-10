package com.brennangambling.awarenesstesting;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class MainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    static private final String FENCE_ACTION = "WK8Gh9mTvve8T92qVtSp";

    static private final String VEHICLE_KEY = "tOUNgsjbYL4t2kd2OcTX";
    static private final String BICYCLE_KEY = "fZfKmVtrSzNrks0oooYW";
    static private final String FOOT_KEY = "oiPEdAnWN7KQs7k0C6jK";
    static private final String WALKING_KEY = "RNNueGrEwxKSfzeOFJ12";
    static private final String RUNNING_KEY = "drNVr5D08denKnW4hcBC";
    static private final String STILL_KEY = "ht5eSdcg3IemR37eo2iD";
    static private final String UNKNOWN_KEY = "NqK6TmIrMs48CPkybWE1";
    static private final String HEADPHONE_KEY = "KhJmptvo9uvPC3FR0EkA";

    private TextView vehicleTextView;
    private TextView bicycleTextView;
    private TextView footTextView;
    private TextView walkingTextView;
    private TextView runningTextView;
    private TextView stillTextView;
    private TextView unknownTextView;
    private TextView headphoneTextView;

    private AwarenessFence vehicleFence;
    private AwarenessFence bicycleFence;
    private AwarenessFence footFence;
    private AwarenessFence walkingFence;
    private AwarenessFence runningFence;
    private AwarenessFence stillFence;
    private AwarenessFence unknownFence;
    private AwarenessFence headphoneFence;

    private PendingIntent fencePendingIntent;
    private IntentFilter fenceIntentFilter;
    private FenceBroadcastReceiver fenceBroadcastReceiver;

    private GoogleApiClient googleApiClient;

    private static void debugLogging(Context context, String tag, String message) {
        Log.d(tag, message);
        Toast toast = Toast.makeText(context, tag + ": " + message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_create).toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get TextViews for displaying status of each of the following activities.
        vehicleTextView = (TextView) findViewById(R.id.inVehicleText);
        bicycleTextView = (TextView) findViewById(R.id.onBicycleText);
        footTextView = (TextView) findViewById(R.id.onFootText);
        walkingTextView = (TextView) findViewById(R.id.walkingText);
        runningTextView = (TextView) findViewById(R.id.runningText);
        stillTextView = (TextView) findViewById(R.id.stillText);
        unknownTextView = (TextView) findViewById(R.id.unknownText);
        headphoneTextView = (TextView) findViewById(R.id.headphonesText);

        //set all TextViews text to N/A.
        TextView[] textViews = {vehicleTextView, bicycleTextView, footTextView, walkingTextView, runningTextView, stillTextView, unknownTextView, headphoneTextView};
        for (TextView current : textViews) {
            current.setText(getText(R.string.state_NA).toString());
        }

        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.build_api_client).toString());
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Awareness.API)
                .build();


        vehicleFence = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
        bicycleFence = DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE);
        footFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
        walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
        runningFence = DetectedActivityFence.during(DetectedActivityFence.RUNNING);
        stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
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
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_destroy).toString());
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_start).toString());
        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.google_api_null).toString());
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_stop).toString());

        Awareness.FenceApi.updateFences(
                googleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(VEHICLE_KEY)
                        .removeFence(BICYCLE_KEY)
                        .removeFence(FOOT_KEY)
                        .removeFence(WALKING_KEY)
                        .removeFence(RUNNING_KEY)
                        .removeFence(STILL_KEY)
                        .removeFence(UNKNOWN_KEY)
                        .removeFence(HEADPHONE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {

            @Override
            public void onSuccess(@NonNull Status status) {
                Log.d(getText(R.string.main_activity).toString(), getText(R.string.fences_removed).toString());
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.e(getText(R.string.main_activity).toString(), getText(R.string.fences_not_removed).toString());
            }
        });

        googleApiClient.unregisterConnectionCallbacks(this);
        googleApiClient.unregisterConnectionFailedListener(this);

        if (googleApiClient != null) {
            googleApiClient.disconnect();
        } else {
            debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.google_api_null).toString());
        }

        unregisterReceiver(fenceBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_save_instance_state).toString());
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(VEHICLE_KEY, vehicleTextView.getText().toString());
        savedInstanceState.putString(BICYCLE_KEY, bicycleTextView.getText().toString());
        savedInstanceState.putString(FOOT_KEY, footTextView.getText().toString());
        savedInstanceState.putString(WALKING_KEY, walkingTextView.getText().toString());
        savedInstanceState.putString(RUNNING_KEY, runningTextView.getText().toString());
        savedInstanceState.putString(STILL_KEY, stillTextView.getText().toString());
        savedInstanceState.putString(UNKNOWN_KEY, unknownTextView.getText().toString());
        savedInstanceState.putString(HEADPHONE_KEY, headphoneTextView.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_restore_instance_state).toString());
        super.onRestoreInstanceState(savedInstanceState);
        vehicleTextView.setText(savedInstanceState.getString(VEHICLE_KEY));
        bicycleTextView.setText(savedInstanceState.getString(BICYCLE_KEY));
        footTextView.setText(savedInstanceState.getString(FOOT_KEY));
        walkingTextView.setText(savedInstanceState.getString(WALKING_KEY));
        runningTextView.setText(savedInstanceState.getString(RUNNING_KEY));
        stillTextView.setText(savedInstanceState.getString(STILL_KEY));
        unknownTextView.setText(savedInstanceState.getString(UNKNOWN_KEY));
        headphoneTextView.setText(savedInstanceState.getString(HEADPHONE_KEY));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        debugLogging(this, getText(R.string.main_activity).toString(), getString(R.string.on_connected).toString());
        Awareness.FenceApi.updateFences(
                googleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(VEHICLE_KEY, vehicleFence, fencePendingIntent)
                        .addFence(BICYCLE_KEY, bicycleFence, fencePendingIntent)
                        .addFence(FOOT_KEY, footFence, fencePendingIntent)
                        .addFence(WALKING_KEY, walkingFence, fencePendingIntent)
                        .addFence(RUNNING_KEY, runningFence, fencePendingIntent)
                        .addFence(STILL_KEY, stillFence, fencePendingIntent)
                        .addFence(UNKNOWN_KEY, unknownFence, fencePendingIntent)
                        .addFence(HEADPHONE_KEY, headphoneFence, fencePendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) {
                            Log.d(getText(R.string.main_activity).toString(), getText(R.string.fence_registered).toString());
                        } else {
                            Log.e(getText(R.string.main_activity).toString(), getText(R.string.fence_not_registered).toString() + " " + status);
                        }
                    }
                });
        registerReceiver(fenceBroadcastReceiver, fenceIntentFilter);
    }

    @Override
    public void onConnectionSuspended(int i) {
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_connection_suspended).toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_connection_failed).toString());
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.connection_result).toString() + connectionResult.toString());
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    class FenceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.on_receive).toString());
            FenceState fenceState = FenceState.extract(intent);
            String fenceKey = fenceState.getFenceKey();
            switch(fenceKey) {
                case VEHICLE_KEY:
                    checkState(context, getText(R.string.vehicle_fence).toString(), vehicleTextView, fenceState);
                    break;
                case BICYCLE_KEY:
                    checkState(context, getText(R.string.bicycle_fence).toString(), bicycleTextView, fenceState);
                    break;
                case FOOT_KEY:
                    checkState(context, getText(R.string.foot_fence).toString(), footTextView, fenceState);
                    break;
                case WALKING_KEY:
                    checkState(context, getText(R.string.walking_fence).toString(), walkingTextView, fenceState);
                    break;
                case RUNNING_KEY:
                    checkState(context, getText(R.string.running_fence).toString(), runningTextView, fenceState);
                    break;
                case STILL_KEY:
                    checkState(context, getText(R.string.still_fence).toString(), stillTextView, fenceState);
                    break;
                case UNKNOWN_KEY:
                    checkState(context, getText(R.string.unknown_fence).toString(), unknownTextView, fenceState);
                    break;
                case HEADPHONE_KEY:
                    checkState(context, getText(R.string.headphone_fence).toString(), headphoneTextView, fenceState);
                    break;
                default:
                    debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.unrecognized_fence).toString());
                    break;
            }
        }

        private void checkState(Context context, String fenceName, TextView textView, FenceState fenceState) {
            int currentState = fenceState.getCurrentState();
            int previousState = fenceState.getPreviousState();
            switch (currentState) {
                case FenceState.TRUE:
                    setState(context, fenceName, getText(R.string.state_true).toString(), textView);
                    break;
                case FenceState.FALSE:
                    setState(context, fenceName, getText(R.string.state_false).toString(), textView);
                    break;
                case FenceState.UNKNOWN:
                    setState(context, fenceName, getText(R.string.state_unknown).toString(), textView);
                    break;
                default:
                    debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.unrecognized_fence_state).toString());
                    break;
            }

            switch (previousState) {
                case FenceState.TRUE:
                    printPreviousState(context, fenceName, getText(R.string.state_true).toString());
                    break;
                case FenceState.FALSE:
                    printPreviousState(context, fenceName, getText(R.string.state_false).toString());
                    break;
                case FenceState.UNKNOWN:
                    printPreviousState(context, fenceName, getText(R.string.state_unknown).toString());
                    break;
                default:
                    debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.unrecognized_fence_state).toString());
                    break;
            }
        }

        private void setState(Context context, String fenceName, String fenceState, TextView textView) {
            textView.setText(fenceState);
            String message = getText(R.string.current_fence_state).toString() + " " + fenceName + " " + getText(R.string.is).toString() + " " + fenceState;
            debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), message);
        }

        private void printPreviousState(Context context, String fenceName, String fenceState) {
            String message = getText(R.string.previous_fence_state).toString()+ " " + fenceName + " " + getText(R.string.was).toString() + " " + fenceState;
            debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), message);
        }


    }
}

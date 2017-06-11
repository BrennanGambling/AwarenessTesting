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

//Google Awareness API imports.
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
//Google API Client imports.
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

public class MainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    //Unique key for the FENCE_ACTION intent filter.
    static private final String FENCE_ACTION = "WK8Gh9mTvve8T92qVtSp";

    //Unique keys for each of the 8 fences.
    static private final String VEHICLE_KEY = "tOUNgsjbYL4t2kd2OcTX";
    static private final String BICYCLE_KEY = "fZfKmVtrSzNrks0oooYW";
    static private final String FOOT_KEY = "oiPEdAnWN7KQs7k0C6jK";
    static private final String WALKING_KEY = "RNNueGrEwxKSfzeOFJ12";
    static private final String RUNNING_KEY = "drNVr5D08denKnW4hcBC";
    static private final String STILL_KEY = "ht5eSdcg3IemR37eo2iD";
    static private final String UNKNOWN_KEY = "NqK6TmIrMs48CPkybWE1";
    static private final String HEADPHONE_KEY = "KhJmptvo9uvPC3FR0EkA";

    //TextViews for each of the 8 fences.
    private TextView vehicleTextView;
    private TextView bicycleTextView;
    private TextView footTextView;
    private TextView walkingTextView;
    private TextView runningTextView;
    private TextView stillTextView;
    private TextView unknownTextView;
    private TextView headphoneTextView;

    //The 8 AwarenessFences. These describe the action that will trigger the FenceBroadcastReceiver.
    private AwarenessFence vehicleFence;
    private AwarenessFence bicycleFence;
    private AwarenessFence footFence;
    private AwarenessFence walkingFence;
    private AwarenessFence runningFence;
    private AwarenessFence stillFence;
    private AwarenessFence unknownFence;
    private AwarenessFence headphoneFence;

    //PendingIntent for registering AwarenessFences.
    private PendingIntent fencePendingIntent;
    //IntentFilter for registration of the FenceBroadcastReceiver.
    private IntentFilter fenceIntentFilter;
    //The BroadcastReceiver that responds to callbacks from the registered AwarenessFences.
    private FenceBroadcastReceiver fenceBroadcastReceiver;

    //The GoogleApiClient used to register the fences.
    private GoogleApiClient googleApiClient;

    /**
     * Logs the given tag follows by the message to Logcat then creates a tost with the same tag and message.
     * @param context the context used to create the Toast.
     * @param tag an identifier placed before the message. "tag: message".
     * @param message the message.
     */
    private static void debugLogging(Context context, String tag, String message) {
        //log message to logcat at the debug level.
        Log.d(tag, message);
        //create and display toast with the message.
        Toast toast = Toast.makeText(context, tag + ": " + message, Toast.LENGTH_SHORT);
        toast.show();
    }

    //First step in activity lifecycle. This is the first method called on creation of the activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //log start of onCreate method.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_create).toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get TextViews for displaying status of each of the following activities.
        vehicleTextView = findViewById(R.id.inVehicleText);
        bicycleTextView = findViewById(R.id.onBicycleText);
        footTextView = findViewById(R.id.onFootText);
        walkingTextView = findViewById(R.id.walkingText);
        runningTextView = findViewById(R.id.runningText);
        stillTextView = findViewById(R.id.stillText);
        unknownTextView = findViewById(R.id.unknownText);
        headphoneTextView = findViewById(R.id.headphonesText);

        //set all TextViews text to N/A.
        TextView[] textViews = {vehicleTextView, bicycleTextView, footTextView, walkingTextView, runningTextView, stillTextView, unknownTextView, headphoneTextView};
        for (TextView current : textViews) {
            current.setText(getText(R.string.state_NA).toString());
        }

        //build the GoogleApiClient.
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Awareness.API)
                .build();

        //create fences that will be true during each of the 8 activities.
        vehicleFence = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
        bicycleFence = DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE);
        footFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
        walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
        runningFence = DetectedActivityFence.during(DetectedActivityFence.RUNNING);
        stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
        unknownFence = DetectedActivityFence.during(DetectedActivityFence.UNKNOWN);
        headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

        //the intent that will be started when any of the fence states change.
        Intent intent = new Intent(FENCE_ACTION);
        //because the intent will be called outside of the application by the Google Play Services
        //a PendingIntent must be used to maintain the same permissions.
        fencePendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                10001,
                intent,
                0);
        //create IntentFilter for the BroadcastReceiver. This IntentFilter only allows intents with the
        //action FENCE_ACTION.
        fenceIntentFilter = new IntentFilter(FENCE_ACTION);
        //create the FenceBroadcastReceiver to be triggered when a fence state changes.
        fenceBroadcastReceiver = new FenceBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        //log the start of onStart method.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_start).toString());
        //if the google api client is not null connect it.
        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            //if the google api client is null log it.
            debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.google_api_null).toString());
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        //log the start of onStop method.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_stop).toString());

        //create fence update request removing all of the fences.
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

        //unregister connection callbacks and connection failed listener.
        googleApiClient.unregisterConnectionCallbacks(this);
        googleApiClient.unregisterConnectionFailedListener(this);

        //if the google api client is not null disconnect.
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        } else {
            //if the google api client is null log it.
            debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.google_api_null).toString());
        }

        //unregister the FenceBroadcastReceiver.
        unregisterReceiver(fenceBroadcastReceiver);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        //log the start of onDestroy method.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_destroy).toString());
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //log the start of onConnected method.
        debugLogging(this, getText(R.string.main_activity).toString(), getString(R.string.on_connected).toString());
        //add all fences using there key and the PendingIntent created in onCreate method.
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

        //register the FenceBroadcastReceiver with the FENCE_ACTION IntentFilter.
        registerReceiver(fenceBroadcastReceiver, fenceIntentFilter);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //log the start of onConnectionSuspended.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_connection_suspended).toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //log the start of onConnectionFailed.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_connection_failed).toString());
        //log the connection result.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.connection_result).toString() + connectionResult.toString());
        //if the google api client is not null try to connect again.
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //log the start of onSaveInstanceState method.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_save_instance_state).toString());
        super.onSaveInstanceState(savedInstanceState);
        //save the text from all of the TextViews in the savedInstanceState Bundle.
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
        //log the start of onRestoreInstanceState method.
        debugLogging(this, getText(R.string.main_activity).toString(), getText(R.string.on_restore_instance_state).toString());
        super.onRestoreInstanceState(savedInstanceState);
        //set all of the TextViews to their previously saved values from the savedInstanceState Bundle.
        vehicleTextView.setText(savedInstanceState.getString(VEHICLE_KEY));
        bicycleTextView.setText(savedInstanceState.getString(BICYCLE_KEY));
        footTextView.setText(savedInstanceState.getString(FOOT_KEY));
        walkingTextView.setText(savedInstanceState.getString(WALKING_KEY));
        runningTextView.setText(savedInstanceState.getString(RUNNING_KEY));
        stillTextView.setText(savedInstanceState.getString(STILL_KEY));
        unknownTextView.setText(savedInstanceState.getString(UNKNOWN_KEY));
        headphoneTextView.setText(savedInstanceState.getString(HEADPHONE_KEY));
    }

    /**
     * FenceBroadcastReceiver responds to fence state updates.
     */
    class FenceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //log the start of onReceive method.
            debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.on_receive).toString());
            //extract the fence state from the intent,
            FenceState fenceState = FenceState.extract(intent);
            //get the fence key from the fence state.
            String fenceKey = fenceState.getFenceKey();
            //update the appropriate TextView.
            switch(fenceKey) {
                case VEHICLE_KEY:
                    //if the fence key is the vehicle key the vehicle fence has been updated.
                    checkState(context, getText(R.string.vehicle_fence).toString(), vehicleTextView, fenceState);
                    break;
                case BICYCLE_KEY:
                    //if the fence key is the bicycle key the bicycle fence has been updated.
                    checkState(context, getText(R.string.bicycle_fence).toString(), bicycleTextView, fenceState);
                    break;
                case FOOT_KEY:
                    //if the fence key is the foot key the foot fence has been updated.
                    checkState(context, getText(R.string.foot_fence).toString(), footTextView, fenceState);
                    break;
                case WALKING_KEY:
                    //if the fence key is the walking key the walking fence has been updated.
                    checkState(context, getText(R.string.walking_fence).toString(), walkingTextView, fenceState);
                    break;
                case RUNNING_KEY:
                    //if the fence key is the running key the running fence has been updated.
                    checkState(context, getText(R.string.running_fence).toString(), runningTextView, fenceState);
                    break;
                case STILL_KEY:
                    //if the fence key is the still key the still fence has been updated.
                    checkState(context, getText(R.string.still_fence).toString(), stillTextView, fenceState);
                    break;
                case UNKNOWN_KEY:
                    //if the fence key is the unknown key the unknown fence has been updated.
                    checkState(context, getText(R.string.unknown_fence).toString(), unknownTextView, fenceState);
                    break;
                case HEADPHONE_KEY:
                    //if the fence key is the headphone key the headphone fence has been updated.
                    checkState(context, getText(R.string.headphone_fence).toString(), headphoneTextView, fenceState);
                    break;
                default:
                    //if the fence key is not one of the 8 above keys an unrecognized fence has been updated.
                    debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.unrecognized_fence).toString());
                    break;
            }
        }

        /**
         * Set the TextView for the updated fence to the new state and log the current and previous states.
         * @param context the context for logging with Toasts
         * @param fenceName the name of the fence that's state was updated.
         * @param textView the TextView for the fence that was updated.
         * @param fenceState the new state of the fence.
         */
        private void checkState(Context context, String fenceName, TextView textView, FenceState fenceState) {
            //get the current and previous state from the FenceState object.
            int currentState = fenceState.getCurrentState();
            int previousState = fenceState.getPreviousState();

            //check the current state of the fence.
            switch (currentState) {
                case FenceState.TRUE:
                    //if the fence state is true log it and update the TextView.
                    setState(context, fenceName, getText(R.string.state_true).toString(), textView);
                    break;
                case FenceState.FALSE:
                    //if the fence state is false log it and update the TextView.
                    setState(context, fenceName, getText(R.string.state_false).toString(), textView);
                    break;
                case FenceState.UNKNOWN:
                    //if the fence state is unknown log it and update the TextView.
                    setState(context, fenceName, getText(R.string.state_unknown).toString(), textView);
                    break;
                default:
                    //if the fence state is unrecognized log it.
                    debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.unrecognized_fence_state).toString());
                    break;
            }

            //check the previous state of the fence.
            switch (previousState) {
                case FenceState.TRUE:
                    //if the fence state was true log it.
                    printPreviousState(context, fenceName, getText(R.string.state_true).toString());
                    break;
                case FenceState.FALSE:
                    //if the fence state was false log it.
                    printPreviousState(context, fenceName, getText(R.string.state_false).toString());
                    break;
                case FenceState.UNKNOWN:
                    //if the fence state was unknown log it.
                    printPreviousState(context, fenceName, getText(R.string.state_unknown).toString());
                    break;
                default:
                    //if the fence state was unrecognized log it.
                    debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), getText(R.string.unrecognized_fence_state).toString());
                    break;
            }
        }

        /**
         * Set the fences TextView to the new state and log it.
         * @param context the context for creating the Toast.
         * @param fenceName the name of the fence that was updated.
         * @param fenceState the new state of the fence.
         * @param textView the TextView for the fence that was updated.
         */
        private void setState(Context context, String fenceName, String fenceState, TextView textView) {
            //set the TextView to the new state.
            textView.setText(fenceState);
            //log the new state of the fence.
            String message = getText(R.string.current_fence_state).toString() + " " + fenceName + " " + getText(R.string.is).toString() + " " + fenceState;
            debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), message);
        }

        /**
         * Log the previous state of the fence.
         * @param context the context for creating the Toast.
         * @param fenceName the name of the fence that was updated.
         * @param fenceState the new state of the fence.
         */
        private void printPreviousState(Context context, String fenceName, String fenceState) {
            //log the previous state of the fence.
            String message = getText(R.string.previous_fence_state).toString()+ " " + fenceName + " " + getText(R.string.was).toString() + " " + fenceState;
            debugLogging(context, getText(R.string.fence_broadcast_receiver).toString(), message);
        }


    }
}

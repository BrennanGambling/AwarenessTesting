package com.brennangambling.awarenesstesting;

import android.Manifest;
import android.app.Activity;
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
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

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

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String message = "onCreate called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
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
        String message = "onDestroy called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        String message = "onStart called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        String message = "onStop called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        if (googleApiClient != null && (googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String message = "onSaveInstanceState called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
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
        String message = "onRestoreInstanceState called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
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
        String message = "onConnected called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        String message = "onConnectionSuspended called.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String message = "onConnectionFailed called. Retrying connection.";
        Log.d("Main Activity", message);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }
}

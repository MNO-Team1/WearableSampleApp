package com.samsung.mno.wearable.connection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by kkumar1 on 8/19/16.
 */

public class Connection {
    private static final String TAG = Connection.class.getSimpleName();
    private GoogleApiClient mGoogleClient;
    private static String WEARABLE_CAPABILITY_NAME = "wearable";
    private ConnectionThread mSendDataThread;
    protected boolean mWearConnected;
    protected Context mContext;
    private static Connection sInstance;


    public Connection(final Context context) {
        mContext = context;
        init();
    }

    private void init() {
        WearConnectionCb mConnectionCb = new WearConnectionCb();
        mGoogleClient = new GoogleApiClient.Builder(mContext)
                .addApiIfAvailable(Wearable.API)
                .addConnectionCallbacks(mConnectionCb)
                .addOnConnectionFailedListener(mConnectionCb)
                .build();
        mGoogleClient.connect();

        mSendDataThread = new ConnectionThread("WearableDataLayerThread", mGoogleClient);
        mSendDataThread.start();

    }

    public static Connection getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new Connection(context);
        }
        return sInstance;
    }


    public GoogleApiClient getGoogleClient() {
        return mGoogleClient;
    }

    public boolean isWearConnected() {
        return mWearConnected;
    }

    public void send(String message) {
        Log.d(TAG, "sendMessage >>>>>>>> ");
        if (mGoogleClient.isConnected() && isWearConnected()) {
            mSendDataThread.sendMessage(message);
        }
    }

    /**
     * Connection Callbacks for Android wearable connections
     */
    class WearConnectionCb implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, CapabilityApi.CapabilityListener {

        @Override
        public void onConnected(Bundle bundle) {
            Log.i(TAG, ">> onConnected <<");
            Wearable.CapabilityApi.addCapabilityListener(mGoogleClient, this, WEARABLE_CAPABILITY_NAME);
            validateWearConnect();
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, ">> onConnectionSuspended <<");
            Wearable.CapabilityApi.removeCapabilityListener(mGoogleClient, this, WEARABLE_CAPABILITY_NAME);
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i(TAG, ">> onConnectionFailed <<");
            Wearable.CapabilityApi.removeCapabilityListener(mGoogleClient, this, WEARABLE_CAPABILITY_NAME);
            validateWearConnect();
        }

        private void validateWearConnect() {
            Wearable.CapabilityApi.getCapability(
                    mGoogleClient, WEARABLE_CAPABILITY_NAME,
                    CapabilityApi.FILTER_REACHABLE).setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {
                @Override
                public void onResult(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {
                    CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
                    if (capabilityInfo == null) {
                        Log.i(TAG, "Capability is empty");
                        return;
                    }

                    Set<Node> nodes = capabilityInfo.getNodes();
                    if (null != nodes && nodes.size() > 0) {
                        Log.i(TAG, ">>> Targeted wear is connected <<< ");
                        mWearConnected = true;
                        return;
                    }
                    Log.i(TAG, ">>> All wears dis-connected <<< ");
                    mWearConnected = false;
                }
            });
        }

        @Override
        public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
            Log.d(TAG, ">> onCapabilityChanged <<" + capabilityInfo);
            validateWearConnect();
        }
    }
}

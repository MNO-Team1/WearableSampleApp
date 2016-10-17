package com.samsung.mno.wearable.connection.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.samsung.mno.wearable.App;
import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.Utils;
import com.samsung.mno.wearable.common.WearableException;
import com.samsung.mno.wearable.connection.DefaultConnection;
import com.samsung.mno.wearable.connection.ISendResult;

import java.util.Set;

/**
 * Created by vgandhi on 8/19/16.
 */

public class AndroidConnection extends DefaultConnection {
    private static final String TAG = AndroidConnection.class.getSimpleName();
    private GoogleApiClient mGoogleClient;
    private AndroidConnectionThread mSendDataThread;

    public AndroidConnection(final Context context) {
        super(Constants.ANDROID_WEAR);
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

        mSendDataThread = new AndroidConnectionThread("WearableDataLayerThread", mGoogleClient);
        mSendDataThread.start();
    }

    @Override
    public void send(final String data, ISendResult result) {
        if (mGoogleClient.isConnected() && isWearConnected()) {
            mSendDataThread.sendData(data, result);
        } else {
            result.onSendResult(Constants.JSON_REQUEST,
                    Constants.FAILURE,
                    getConnectionType(),
                    new WearableException(App.getAppContext(), WearableException.DEVICE_NOT_CONNECTED));
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mGoogleClient.disconnect();
        mConnectionChange = null;
    }

    /**
     * Connection Callbacks for Android wearable connections. Provides informaiton related to
     * connection events
     */
    class WearConnectionCb implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, CapabilityApi.CapabilityListener {

        @Override
        public void onConnected(Bundle bundle) {
            Log.i(TAG, ">> onConnected <<");
            Wearable.CapabilityApi.addCapabilityListener(mGoogleClient, this, Utils.getCapability(mContext));
            validateWearConnect();
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, ">> onConnectionSuspended <<");
            Wearable.CapabilityApi.removeCapabilityListener(mGoogleClient, this, Utils.getCapability(mContext));
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i(TAG, ">> onConnectionFailed <<");
            Wearable.CapabilityApi.removeCapabilityListener(mGoogleClient, this, Utils.getCapability(mContext));
            validateWearConnect();
        }

        private void validateWearConnect() {
            Wearable.CapabilityApi.getCapability(
                    mGoogleClient, Utils.getCapability(mContext),
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
                        Log.i(TAG, ">>> Android  wear is connected <<< ");
                        mWearConnected = true;
                        updateConnectionChange();
                        return;
                    }
                    Log.i(TAG, ">>> Android wear disconnected <<< ");
                    mWearConnected = false;
                    updateConnectionChange();
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

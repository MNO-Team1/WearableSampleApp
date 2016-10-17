package com.samsung.mno.wearable.connection;

import android.content.Context;
import android.util.Log;

import com.samsung.mno.wearable.App;
import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.Utils;
import com.samsung.mno.wearable.connection.android.AndroidConnection;
import com.samsung.mno.wearable.connection.tizen.TizenConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by vgandhi on 8/19/16.
 */
public class WearableManager {
    public String TAG;
    private static WearableManager sManager = null;
    private HashMap<Integer, IWearableConnection> mConnectors = new HashMap<Integer, IWearableConnection>();

    private WearableManager() {
        TAG = "WearableManager";
    }

    /**
     * Static method to get Wearable Manager instance
     *
     * @param context
     * @return
     */
    public static WearableManager getInstance(Context context) {
        if (sManager == null) {
            sManager = new WearableManager();
        }
        return sManager;
    }

    /**
     * Add connection adapter in local cache for future references
     *
     * @param deviceType type of the device for which connection is requested
     * @param cb         Callback fro connection change events of requested connection
     */
    public void addConnectionAdapter(final int deviceType, IConnectionChange cb) {
        if (mConnectors.containsKey(deviceType)) {
            Log.i(TAG, "Connection already present");
            return;
        }

        synchronized (mConnectors) {
            switch (deviceType) {
                case Constants.ANDROID_WEAR:
                    IWearableConnection androidAdapter = new AndroidConnection(App.getAppContext());
                    androidAdapter.registerConnectionChange(cb);
                    mConnectors.put(Constants.ANDROID_WEAR, androidAdapter);
                    break;
                case Constants.TIZEN_GEAR:
                    IWearableConnection tizenAdapter = new TizenConnection(App.getAppContext());
                    tizenAdapter.registerConnectionChange(cb);
                    mConnectors.put(Constants.TIZEN_GEAR, tizenAdapter);
                    break;
                default:
                    Utils.showToast(App.getAppContext(), "Feature not supported");
            }
        }
    }

    /**
     * Remove connection adapters and performs its cleanup
     *
     * @param deviceType
     */
    public void removeConnectionAdapter(final int deviceType) {
        if (!mConnectors.containsKey(deviceType)) {
            Log.i(TAG, "Connection already present");
            return;
        }

        synchronized (mConnectors) {
            // Perform connection cleanup and then remove it from adapter
            mConnectors.get(deviceType).cleanup();

            switch (deviceType) {
                case Constants.ANDROID_WEAR:
                    mConnectors.remove(deviceType);
                    break;
                case Constants.TIZEN_GEAR:
                    mConnectors.remove(deviceType);
                    break;
                default:
                    Utils.showToast(App.getAppContext(), "Feature not supported");
            }
        }

    }

    /**
     * Send data to respective connected wearable device. Send a send status callback based on
     * send operation status.
     *
     * @param data   data to be sent to connected wearable device
     * @param result Callback for send operation status
     */
    public void send(final String data, ISendResult result) {
        if (mConnectors.size() == 0) {
            Utils.showToast(App.getAppContext(), "Please enable connection");
            return;
        }

        synchronized (mConnectors) {
            Set<Map.Entry<Integer, IWearableConnection>> entrySet = mConnectors.entrySet();
            for (Map.Entry conn : entrySet) {
                ((IWearableConnection) conn.getValue()).send(data, result);
            }
        }
    }

    /**
     * Close existing connection adapters and cleanup wear connections
     */
    public void closeConnection() {
        synchronized (mConnectors) {
            synchronized (mConnectors) {
                Set<Map.Entry<Integer, IWearableConnection>> entrySet = mConnectors.entrySet();
                for (Map.Entry conn : entrySet) {
                    ((IWearableConnection) conn.getValue()).cleanup();
                }
            }
            mConnectors.clear();
        }
    }
}

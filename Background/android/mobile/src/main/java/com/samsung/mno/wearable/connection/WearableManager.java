package com.samsung.mno.wearable.connection;

import android.content.Context;

import com.samsung.mno.wearable.connection.android.AndroidConnection;
import com.samsung.mno.wearable.connection.tizen.TizenConnection;

import java.util.HashSet;

/**
 * Created by vgandhi on 8/19/16.
 */
public class WearableManager {
    public String TAG = "WearableManager";
    private static WearableManager sManager = null;
    private HashSet<IWearableConnection> mConnectors = new HashSet<IWearableConnection>();

    private WearableManager(final Context context) {
        mConnectors.add(new AndroidConnection(context));
        mConnectors.add(new TizenConnection(context));
    }

    /**
     * Static method to get Wearable Manager instance
     *
     * @param context
     * @return
     */
    public static WearableManager getInstance(Context context) {
        if (sManager == null) {
            sManager = new WearableManager(context);
        }
        return sManager;
    }

    /**
     * Send data to respective connected wearable device. Send a send status callback based on
     * send operation status.
     *
     * @param data   data to be sent to connected wearable device
     * @param result Callback for send operation status
     */
    public void send(final String data, ISendResult result) {
        synchronized (mConnectors) {
            for (IWearableConnection conn : mConnectors) {
                conn.send(data, result);
            }
        }
    }

    /**
     * Register callback for connection change
     *
     * @param cb callback for conneciton change
     */
    public void registerConnectionChange(IConnectionChange cb) {
        synchronized (mConnectors) {
            for (IWearableConnection conn : mConnectors) {
                conn.registerConnectionChange(cb);
            }
        }
    }

    /**
     * Unregister callbacks for connection change
     *
     * @param cb Callback for connection change
     */
    public void unregisterConnecitonChange(IConnectionChange cb) {
        synchronized (mConnectors) {
            for (IWearableConnection conn : mConnectors) {
                conn.unRegisterConnectionChange(cb);
            }
        }
    }
}

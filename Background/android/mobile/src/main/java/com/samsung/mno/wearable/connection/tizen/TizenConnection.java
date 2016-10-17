package com.samsung.mno.wearable.connection.tizen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.connection.DefaultConnection;
import com.samsung.mno.wearable.connection.ISendResult;


/**
 * Created by vgandhi on 8/19/16.
 */
public class TizenConnection extends DefaultConnection implements ITizenConnectionCb {
    private static final String TAG = TizenConnection.class.getSimpleName();
    private TizenService mService = null;
    private boolean mIsBound = false;

    public TizenConnection(final Context context) {
        super(Constants.TIZEN_GEAR);
        mContext = context;
        mWearConnected = false;
        mIsBound = mContext.bindService(new Intent(mContext, TizenService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Service connection class listening for connection events
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, ">> onServiceConnected <<");
            mService = ((TizenService.LocalBinder) service).getService();
            mService.registerConnectionCb(TizenConnection.this);
            mService.findAgents();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, ">> onServiceDisconnected <<");
            mService = null;
            mIsBound = false;
        }
    };

    @Override
    public void send(final String data, ISendResult result) {
        super.send(data, result);
        if (!mWearConnected && mService != null) {
            mService.findAgents();
        }
        mService.sendObject(data, result);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mConnectionChange = null;
        mContext.unbindService(mConnection);
    }

    @Override
    public void onConnectionChange(String peerId, boolean connected) {
        mWearConnected = connected;
        updateConnectionChange();
    }
}
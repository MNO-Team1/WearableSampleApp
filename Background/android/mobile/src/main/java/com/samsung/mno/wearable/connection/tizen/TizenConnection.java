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
public class TizenConnection extends DefaultConnection implements IPeerConnectivityCB {
    private static final String TAG = TizenConnection.class.getSimpleName();
    private TizenService mService = null;
    private boolean mIsBound = false;

    public TizenConnection(final Context context) {
        super(Constants.TIZEN_GEAR);
        Log.i(TAG, ">> TizenConnection init() <<");
        mContext = context;
        mWearConnected = false;
        init();
    }

    private void init() {
        // Bind service
        mIsBound = mContext.bindService(new Intent(mContext, TizenService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, ">> mIsBound <<: " + mIsBound);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, ">> onServiceConnected <<");
            mService = ((TizenService.LocalBinder) service).getService();
            mService.registerPeerDisconnectCB(TizenConnection.this);
            //mService.registerFileAction(getFileAction());
            mService.connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, ">> onServiceDisconnected <<");
            mService = null;
            mIsBound = false;
            //mWearConnected = false;
            //updateConnectionChange();
        }
    };

    protected void finalize() throws Throwable {
        Log.i(TAG, ">> finalize <<");
        try {
            mService.closeConnection();
            mContext.unbindService(mConnection);
        } finally {
            super.finalize();
        }
    }

    @Override
    public void send(final String data, ISendResult result) {
        super.send(data, result);
        Log.i(TAG, ">> send <<");
        mService.sendObject(data, result);
    }


    @Override
    public void connect() {
        mWearConnected = true;
        updateConnectionChange();
    }

    @Override
    public void disconnect() {
        mService.closeConnection();
        mContext.unbindService(mConnection);
        mWearConnected = false;
        updateConnectionChange();
    }
}
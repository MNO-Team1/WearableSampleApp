package com.samsung.mno.wearable.connection;

import android.content.Context;

/**
 * Created by vgandhi on 8/19/16.
 */

public class DefaultConnection implements IWearableConnection {
    protected boolean mWearConnected;
    protected Context mContext;
    private int mConnectiontype;
    protected IConnectionChange mConnectionChange;

    public boolean isWearConnected() {
        return mWearConnected;
    }

    public DefaultConnection(final int connType) {
        mConnectiontype = connType;
    }

    @Override
    public void send(String data, ISendResult result) {
    }

    @Override
    public int getConnectionType() {
        return mConnectiontype;
    }

    @Override
    public void registerConnectionChange(IConnectionChange connectionCb) {
        mConnectionChange = connectionCb;
    }

    protected final void updateConnectionChange() {
        if (mConnectionChange == null) {
            return;
        }
        mConnectionChange.onConnectionChange(mWearConnected, mConnectiontype);
    }

    @Override
    public void cleanup() {

    }
}

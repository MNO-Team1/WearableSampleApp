package com.samsung.mno.wearable.connection.tizen;

/**
 * Created by kristapher on 9/1/16.
 */

interface ITizenConnectionCb {
    /**
     * Updated conneciton status from Tizen service to Tizen connector
     *
     * @param connected true if connected else false
     */
    public void onConnectionChange(String peerId, boolean connected);
}
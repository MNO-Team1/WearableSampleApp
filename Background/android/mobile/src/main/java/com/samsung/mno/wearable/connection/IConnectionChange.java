package com.samsung.mno.wearable.connection;

/**
 * Created by vgandhi on 8/29/16.
 */

public interface IConnectionChange {
    /**
     * Provides details of type of device connected/disconnected
     *
     * @param connected    true for connected, false for disconnect
     * @param wearableType Type of wearable, Constant.TIZEN_GEAR or Constant.ANDROID_WEAR
     */
    void onConnectionChange(final boolean connected, int wearableType);
}

package com.samsung.mno.wearable.connection;

/**
 * Created by vgandhi on 8/19/16.
 */

public interface IWearableConnection {

    /**
     * Supports send operation for String data
     *
     * @param data   Content to be sent
     * @param result Callback for send operation result
     */
    void send(String data, ISendResult result);

    /**
     * Register callback for connection change events
     *
     * @param connectionCb connection change callback
     */
    void registerConnectionChange(IConnectionChange connectionCb);

    /**
     * Returns a device type used for connection
     *
     * @return . WearableConstant.TIZEN_GEAR or WearableConstant.ANDROID_WEAR
     */
    int getConnectionType();

    /**
     * Cleanup of respective adapters
     */
    void cleanup();

}

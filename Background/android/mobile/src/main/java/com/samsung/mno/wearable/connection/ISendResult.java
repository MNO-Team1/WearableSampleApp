package com.samsung.mno.wearable.connection;

import com.samsung.mno.wearable.common.WearableException;

/**
 * Created by vgandhi on 10/10/16.
 */

public interface ISendResult {
    /**
     * Callback for send request result
     *
     * @param requestType Type of the request. Values can be Constant,JSON_REQUEST, FILE_REQUEST etc.
     * @param result      SUCCESS or FAILURE
     * @param deviceType  Tizen or Android device type
     * @param exception   details of exception if connection fails.
     */
    void onSendResult(int requestType, int result, int deviceType, WearableException exception);
}

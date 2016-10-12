package com.samsung.mno.wearable.connection.android;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.samsung.mno.wearable.App;
import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.WearableException;
import com.samsung.mno.wearable.connection.ISendResult;

import java.util.Set;

/**
 * Created by vgandhi on 8/19/16.
 */

public class AndroidConnectionThread extends HandlerThread {
    private static final String TAG = AndroidConnectionThread.class.getSimpleName();
    private GoogleApiClient mGoogleClient;
    private MessageHandler mHandler;

    class Communication {
        final String mMessage;
        ISendResult mCallback;

        Communication(final String message, ISendResult result) {
            mMessage = message;
            mCallback = result;
        }
    }

    public AndroidConnectionThread(String name, GoogleApiClient googleApiClient) {
        super(name);
        mGoogleClient = googleApiClient;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new MessageHandler(this.getLooper());
    }

    /**
     * @param data
     */
    public void sendData(final String data, ISendResult result) {
        Message msg = Message.obtain();
        msg.what = Constants.SEND_MESSAGE;
        Communication comm = new Communication(data, result);
        msg.obj = comm;
        mHandler.sendMessage(msg);
    }

    /**
     * Handler using handler thread looper
     */
    class MessageHandler extends Handler {

        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case Constants.SEND_MESSAGE:

                    CapabilityApi.GetCapabilityResult capabilityResult =
                            Wearable.CapabilityApi.getCapability(
                                    mGoogleClient, Constants.WEARABLE_CAPABILITY,
                                    CapabilityApi.FILTER_REACHABLE).await();

                    CapabilityInfo capabilityInfo = capabilityResult.getCapability();
                    if (capabilityInfo == null) {
                        return;
                    }

                    Set<Node> connectedNodes = capabilityInfo.getNodes();
                    if (connectedNodes != null) {
                        for (Node node : connectedNodes) {
                            /**
                             * Send operation for Message Data API
                             */
                            Log.v(TAG, "Connected Node name : " + node.getDisplayName());
                            Log.v(TAG, "Is Nearby : " + node.isNearby());

                            Communication commObj = (Communication) msg.obj;

                            if (commObj != null) {
                                if (!TextUtils.isEmpty(commObj.mMessage)) {
                                    MessageApi.SendMessageResult result =
                                            Wearable.MessageApi.sendMessage(mGoogleClient,
                                                    node.getId(),
                                                    Constants.SEND_MESSAGE_PATH, commObj.mMessage.getBytes()).await();
                                    commObj.mCallback.onSendResult(Constants.JSON_REQUEST,
                                            result.getStatus().isSuccess() ? Constants.SUCCESS : Constants.FAILURE,
                                            Constants.ANDROID_WEAR,
                                            result.getStatus().isSuccess() ? null : new WearableException(App.getAppContext(), WearableException.SEND_OPERATION_FAILED));
                                }
                            }
                        }
                    }

                    break;

                default:
                    break;
            }

        }
    }
}

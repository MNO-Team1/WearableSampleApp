package com.samsung.mno.wearable.connection;

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

import java.util.Set;

import com.samsung.mno.wearable.common.WearConstants;

/**
 * Created by kkumar1 on 8/19/16.
 */

public class ConnectionThread extends HandlerThread {
    private static final String TAG = ConnectionThread.class.getSimpleName();
    private GoogleApiClient mGoogleClient;
    private MessageHandler mHandler;

    public ConnectionThread(String name, GoogleApiClient googleApiClient) {
        super(name);
        mGoogleClient = googleApiClient;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new MessageHandler(this.getLooper());
    }

    public void sendMessage(final String message) {
        Message msg = Message.obtain();
        msg.what = WearConstants.SEND_MESSAGE;
        msg.obj = message;
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
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case WearConstants.SEND_MESSAGE:

                    CapabilityApi.GetCapabilityResult capabilityResult =
                            Wearable.CapabilityApi.getCapability(
                                    mGoogleClient, WearConstants.WEARABLE_CAPABILITY,
                                    CapabilityApi.FILTER_REACHABLE).await();

                    CapabilityInfo capabilityInfo = capabilityResult.getCapability();
                    if (capabilityInfo == null) {
                        return; //// TODO: 8/23/16  need to send callback 
                    }

                    Set<Node> connectedNodes = capabilityInfo.getNodes();
                    if (connectedNodes != null) {
                        for (Node node : connectedNodes) {
                            /**
                             * Send operation for Message Data API
                             */
                            Log.v(TAG, "Connected Node name : " + node.getDisplayName());
                            Log.v(TAG, "Is Nearby : " + node.isNearby());

                            String messageData = (String) msg.obj;

                            if (!TextUtils.isEmpty(messageData)) {
                                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleClient, node.getId(), WearConstants.SEND_MESSAGE_PATH, messageData.getBytes()).await();
                                if (result.getStatus().isSuccess()) {
                                    Log.v(TAG, "Message : {" + messageData + "} sent to: " + node.getDisplayName());
                                } else {
                                    Log.v(TAG, "Failed to send Message to: " + node.getDisplayName());
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

/*
 * Copyright (c) 2015 Samsung Electronics Co., Ltd. All rights reserved. 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that 
 * the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer. 
 *     * Redistributions in binary form must reproduce the above copyright notice, 
 *       this list of conditions and the following disclaimer in the documentation and/or 
 *       other materials provided with the distribution. 
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its contributors may be used to endorse or 
 *       promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.samsung.mno.wearable.connection.tizen;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.samsung.mno.wearable.App;
import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.Utils;
import com.samsung.mno.wearable.common.WearableException;
import com.samsung.mno.wearable.connection.ISendResult;
import com.samsung.mno.wearable.data.DeviceInfo;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by chait.reddy on 8/19/16.
 */
public class TizenService extends SAAgent {
    private static final String TAG = TizenService.class.getSimpleName();
    private static final int OBJECT_CHANNEL_ID = 105;
    private final IBinder mBinder = new LocalBinder();
    private ITizenConnectionCb mConnectionCb = null;
    HashMap<String, ServiceConnection> mConnections = new HashMap<>();

    public TizenService() {
        super(TAG, ServiceConnection.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SA mSAAccessory = new SA();
        try {
            mSAAccessory.initialize(this);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent saPeerAgent, int result) {
        Log.i(TAG, "onFindPeerAgentResponse " + saPeerAgent);
        if (saPeerAgent == null) {
            return;
        }
        requestServiceConnection(saPeerAgent);
    }

    public void findAgents() {
        findPeerAgents();
    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent saPeerAgent, SASocket saSocket, int i) {
        super.onServiceConnectionResponse(saPeerAgent, saSocket, i);
        Log.i(TAG, "onServiceConnectionResponse overloaded=" + statusToString(i));
    }

    @Override
    protected void onServiceConnectionResponse(SASocket saSocket, int result) {
        Log.i(TAG, "onServiceConnectionResponse=" + statusToString(result));
        ServiceConnection connection = (ServiceConnection) saSocket;
        switch (result) {
            case CONNECTION_SUCCESS:
                connection.setPeerId(connection.getConnectedPeerAgent().getPeerId());
                mConnections.put(
                        connection.getPeerId(),
                        connection);
                mConnectionCb.onConnectionChange(
                        connection.getPeerId(),
                        true);
                break;
            case CONNECTION_ALREADY_EXIST:
                connection.setPeerId(connection.getConnectedPeerAgent().getPeerId());
                mConnections.put(
                        connection.getPeerId(),
                        connection);
                mConnectionCb.onConnectionChange(
                        connection.getPeerId(),
                        true);
                break;
            default:
                Utils.showToast(App.getAppContext(), "onServiceConnectionResponse=" + result);
                break;
        }
    }

    /**
     * Converts a result code from an attempt to establish a connection with a peer to a string
     *
     * @param result
     * @return
     */
    public static String statusToString(int result) {
        switch (result) {
            case CONNECTION_ALREADY_EXIST:
                return "connection already exists";
            case CONNECTION_FAILURE_DEVICE_UNREACHABLE:
                return "connection failure device unreachable";
            case CONNECTION_FAILURE_INVALID_PEERAGENT:
                return "connection failure invalid peer agent";
            case CONNECTION_FAILURE_NETWORK:
                return "connection failure network";
            case CONNECTION_FAILURE_PEERAGENT_NO_RESPONSE:
                return "connection failure peer agent no response";
            case CONNECTION_FAILURE_PEERAGENT_REJECTED:
                return "connection failure peer agent rejected";
            case CONNECTION_FAILURE_SERVICE_LIMIT_REACHED:
                return "connection failure service limit reached";
            case ERROR_CONNECTION_INVALID_PARAM:
                return "error connection invalid param";
            case ERROR_FATAL:
                return "error fatal";
            case ERROR_SDK_NOT_INITIALIZED:
                return "error sdk not initialized";
            case CONNECTION_SUCCESS:
                return "Connection success";
        }
        return "unknown status (" + result + ")";
    }

    /**
     * Converts an error code to a string value
     *
     * @param errorCode A connection lost error code
     * @return
     */
    public static String errorToString(int errorCode) {
        switch (errorCode) {
            case SASocket.CONNECTION_LOST_PEER_DISCONNECTED:
                return "CONNECTION_LOST_PEER_DISCONNECTED (" + errorCode + ")";
            case SASocket.CONNECTION_LOST_UNKNOWN_REASON:
                return "CONNECTION_LOST_UNKNOWN_REASON (" + errorCode + ")";
            case SASocket.CONNECTION_LOST_DEVICE_DETACHED:
                return "CONNECTION_LOST_DEVICE_DETACHED (" + errorCode + ")";
            case SASocket.CONNECTION_LOST_RETRANSMISSION_FAILED:
                return "CONNECTION_LOST_RETRANSMISSION_FAILED (" + errorCode + ")";
            case SASocket.ERROR_FATAL:
                return "ERROR_FATAL (" + errorCode + ")";
        }
        return "UNKNOWN (" + errorCode + ")";
    }

    public class LocalBinder extends Binder {
        public TizenService getService() {
            Log.i(TAG, "getService: ");
            return TizenService.this;
        }
    }

    /**
     * Gets the connection object associated with a given peer id.
     *
     * @param peerId The peer id to find.
     * @return Returns null if no peer id is found.
     */
    public ServiceConnection getConnection(String peerId) {
        return mConnections.get(peerId);
    }

    public void registerConnectionCb(ITizenConnectionCb cb) {
        this.mConnectionCb = cb;
    }

    public class SenderBinder extends Binder {
        public TizenService getService() {
            return TizenService.this;
        }
    }

    /**
     *
     */
    class SendResult implements ISendResult {
        @Override
        public void onSendResult(int requestType, int result, int deviceType, WearableException exception) {

        }
    }

    /**
     * SASocket connection
     */
    public class ServiceConnection extends SASocket {
        private String mPeerId;

        public String getPeerId() {
            return mPeerId;
        }

        public void setPeerId(String peerId) {
            mPeerId = peerId;
        }

        public ServiceConnection() {
            super(ServiceConnection.class.getName());
            Log.i(TAG, "ServiceConnection Constructor");
        }

        @Override
        public void onError(int channelId, String errorString, int error) {
            Log.e(TAG, errorString + " (" + error + ")");
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            Log.i(TAG, "onReceive: ");
            final String message = new String(data);
            if (Constants.GET_DEVICE_INFO.equals(message)) {
                sendObject("Android :: " + DeviceInfo.getDetails(), new SendResult());
            } else {
                Utils.showToast(getApplicationContext(), "Connected Tizen model is :: " + message);
            }
        }

        @Override
        protected void onServiceConnectionLost(int reason) {
            Log.i(TAG, "onServiceConnectionLost: ");
            Utils.showToast(getApplicationContext(), "Connection Lost");
            final String peerId = getPeerId();
            mConnections.remove(peerId);
            mConnectionCb.onConnectionChange(peerId, !mConnections.isEmpty());
        }
    }

    public void sendObject(final String content, ISendResult result) {
        byte[] data = content.getBytes();
        Log.i(TAG, "sendObject: ");

        if (mConnections.isEmpty()) {
            result.onSendResult(Constants.JSON_REQUEST,
                    Constants.FAILURE,
                    Constants.TIZEN_GEAR,
                    new WearableException(App.getAppContext(), WearableException.DEVICE_NOT_CONNECTED));
            return;
        }

        try {
            for (ServiceConnection conn : mConnections.values()) {
                if (conn.isConnected()) {
                    conn.send(OBJECT_CHANNEL_ID, data);
                    result.onSendResult(Constants.JSON_REQUEST,
                            Constants.SUCCESS,
                            Constants.TIZEN_GEAR,
                            null);
                } else {
                    result.onSendResult(Constants.JSON_REQUEST,
                            Constants.FAILURE,
                            Constants.TIZEN_GEAR,
                            new WearableException(App.getAppContext(), WearableException.DEVICE_NOT_CONNECTED));
                }
            }
        } catch (IOException e) {
            result.onSendResult(Constants.JSON_REQUEST,
                    Constants.FAILURE,
                    Constants.TIZEN_GEAR,
                    new WearableException(App.getAppContext(), WearableException.IO_EXCEPTION));
        }
    }
}

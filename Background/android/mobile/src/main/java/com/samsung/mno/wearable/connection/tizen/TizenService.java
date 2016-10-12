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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer.EventListener;
import com.samsung.android.sdk.accessoryfiletransfer.SAft;
import com.samsung.mno.wearable.App;
import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.WearableException;
import com.samsung.mno.wearable.connection.ISendResult;

import java.io.IOException;

/**
 * Created by chait.reddy on 8/19/16.
 */
public class TizenService extends SAAgent {
    private static final String TAG = TizenService.class.getSimpleName();
    private static final int COMMAND_CHANNEL_ID = 104;
    private static final int OBJECT_CHANNEL_ID = 105;
    private static final Class<ServiceConnection> SASOCKET_CLASS = ServiceConnection.class;
    private final IBinder mBinder = new LocalBinder();
    private ServiceConnection mConnectionHandler = null;
    Handler mHandler = new Handler();
    private SAPeerAgent mPeerAgent = null;
    private IPeerConnectivityCB mPeerconnectivityAction = null;
    private SAFileTransfer mSAFileTransfer = null;
    private EventListener mCallback = null;

    public TizenService() {
        super(TAG, SASOCKET_CLASS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        SA mSAAccessory = new SA();
        SAft saft = new SAft();
        try {
            mSAAccessory.initialize(this);
            saft.initialize(this);
        } catch (SsdkUnsupportedException e) {
            // try to handle SsdkUnsupportedException
            if (processUnsupportedException(e) == true) {
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        try {
            mSAFileTransfer.close();
            mSAFileTransfer = null;
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage());
        }
        super.onDestroy();
        Log.i(TAG, "FileTransferSender Service is Stopped.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    protected void onFindPeerAgentsResponse(SAPeerAgent[] peerAgents, int result) {
        Log.i(TAG, "onFindPeerAgentsResponse: " + result);
        if ((result == SAAgent.PEER_AGENT_FOUND) && (peerAgents != null)) {
            Log.i(TAG, "PEER_AGENT_FOUND: " + result);
            for (SAPeerAgent peerAgent : peerAgents) {
                requestServiceConnection(peerAgent);
                mPeerAgent = peerAgent;
            }
        } else if (result == SAAgent.FINDPEER_DEVICE_NOT_CONNECTED) {
            Toast.makeText(getApplicationContext(), "FINDPEER_DEVICE_NOT_CONNECTED", Toast.LENGTH_LONG).show();
        } else if (result == SAAgent.FINDPEER_SERVICE_NOT_FOUND) {
            Toast.makeText(getApplicationContext(), "FINDPEER_SERVICE_NOT_FOUND", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No peer found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        Log.i(TAG, "onServiceConnectionRequested: ");
        if (peerAgent == null) {
            Toast.makeText(getBaseContext(), "onServiceConnectionRequested mPeerAgent == null", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "onServiceConnectionRequested ");
        if (peerAgent != null) {
            acceptServiceConnectionRequest(peerAgent);
        }
    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
        Log.i(TAG, "onServiceConnectionResponse: " + result);
        if (result == SAAgent.CONNECTION_SUCCESS) {
            this.mConnectionHandler = (ServiceConnection) socket;
            this.mPeerconnectivityAction.connect();
        } else if (result == SAAgent.CONNECTION_ALREADY_EXIST) {
            Toast.makeText(getBaseContext(), "CONNECTED TO TIZEN", Toast.LENGTH_LONG).show();
        } else if (result == SAAgent.CONNECTION_DUPLICATE_REQUEST) {
            Toast.makeText(getBaseContext(), "CONNECTION_DUPLICATE_REQUEST", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onError(SAPeerAgent peerAgent, String errorMessage, int errorCode) {
        Log.i(TAG, "onError: ");
        super.onError(peerAgent, errorMessage, errorCode);
    }

    @Override
    protected void onPeerAgentsUpdated(SAPeerAgent[] peerAgents, int result) {
        Log.i(TAG, "onPeerAgentsUpdated: ");
        final SAPeerAgent[] peers = peerAgents;
        final int status = result;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (peers != null) {
                    if (status == SAAgent.PEER_AGENT_AVAILABLE) {
                        Toast.makeText(getApplicationContext(), "PEER_AGENT_AVAILABLE", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "PEER_AGENT_UNAVAILABLE", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    class SendResult implements ISendResult {
        @Override
        public void onSendResult(int requestType, int result, int deviceType, WearableException exception) {

        }
    }

    public class ServiceConnection extends SASocket {
        public ServiceConnection() {
            super(ServiceConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
            Log.i(TAG, "onError111: ");
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            Log.i(TAG, "onReceive: ");
            if (null == mConnectionHandler) {
                return;
            }
            final String message = new String(data);
            if (message.equals("GETDEVINFO")) {
                sendObject("Android :: " + getDeviceName(), new SendResult());
            } else {

                Toast.makeText(getApplicationContext(), "Connected Tizen model is :: " + message, Toast.LENGTH_LONG).show();

/*            new Thread(new Runnable() {
                public void run() {
                    try {
                        mConnectionHandler.send(COMMAND_CHANNEL_ID, message.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/
            }
        }

        @Override
        protected void onServiceConnectionLost(int reason) {
            Log.i(TAG, "onServiceConnectionLost: ");
            Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_LONG).show();
            closeConnection();
            mPeerconnectivityAction.disconnect();
            mPeerAgent = null;
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public class LocalBinder extends Binder {
        public TizenService getService() {
            Log.i(TAG, "getService: ");
            return TizenService.this;
        }
    }

    public void findPeers() {
        findPeerAgents();
    }

    public void sendObject(final String content, ISendResult result) {
        byte[] data = content.getBytes();
        Log.i(TAG, "sendObject: ");
        if (mConnectionHandler == null) {
            result.onSendResult(Constants.JSON_REQUEST,
                    Constants.FAILURE,
                    Constants.TIZEN_GEAR,
                    new WearableException(App.getAppContext(), WearableException.DEVICE_NOT_CONNECTED));
            return;
        }

        try {
            mConnectionHandler.send(OBJECT_CHANNEL_ID, data);
            result.onSendResult(Constants.JSON_REQUEST,
                    Constants.SUCCESS,
                    Constants.TIZEN_GEAR,
                    null);
        } catch (IOException e) {
            result.onSendResult(Constants.JSON_REQUEST,
                    Constants.FAILURE,
                    Constants.TIZEN_GEAR,
                    new WearableException(App.getAppContext(), WearableException.IO_EXCEPTION));
        }
    }


    public boolean closeConnection() {
        Log.i(TAG, "closeConnection: ");
        if (mConnectionHandler != null) {
            mConnectionHandler.close();
            mConnectionHandler = null;
            return true;
        } else {
            return false;
        }

    }

    private boolean processUnsupportedException(SsdkUnsupportedException e) {
        Log.i(TAG, "processUnsupportedException: ");
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Log.e(TAG, "You need to install Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Log.e(TAG, "You need to update Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Log.e(TAG, "We recommend that you update your Samsung Accessory SDK before using this application.");
            return false;
        }
        return true;
    }

    public void connect() {
        if (mPeerAgent != null) {
            requestServiceConnection(mPeerAgent);
        } else {
            super.findPeerAgents();
        }
    }


    public void registerPeerDisconnectCB(IPeerConnectivityCB cb) {
        this.mPeerconnectivityAction = cb;
    }

    public class SenderBinder extends Binder {
        public TizenService getService() {
            return TizenService.this;
        }
    }
}

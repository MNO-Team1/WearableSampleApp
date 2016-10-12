package com.samsung.mno.wearable.receiver;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

import com.samsung.mno.wearable.common.WearConstants;
import com.samsung.mno.wearable.common.DeviceInfo;
import com.samsung.mno.wearable.connection.Connection;

/**
 * Created by k.kumar1 on 08/19/2016.
 */
public class ListenerService extends WearableListenerService {
    private static final String TAG = "ListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived >>>>>>>> ");
        if (messageEvent.getData() != null) {
            String data = new String(messageEvent.getData());
            if (TextUtils.isEmpty(data)) {
                return;
            }

            if (WearConstants.GET_DEVICE_INFO.equals(data)) {
                Connection.getInstance(getApplicationContext()).send(new DeviceInfo().toString());
            } else {
                Log.d(TAG, "Data received from phone = " + data);
                showMessage(data);
            }
        }
    }

    private void showMessage(final String data) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });
    }
}
package com.samsung.mno.wearable.connection.android.receiver;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.samsung.mno.wearable.App;
import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.WearableException;
import com.samsung.mno.wearable.common.Utils;
import com.samsung.mno.wearable.connection.ISendResult;
import com.samsung.mno.wearable.connection.WearableManager;
import com.samsung.mno.wearable.data.DeviceInfo;

/**
 * Created by kkumar1 on 8/23/15.
 */
public class DataListenerService extends WearableListenerService {
    static final String TAG = "DataListenerService";

    private SendResult mResult = new SendResult();

    class SendResult implements ISendResult {
        @Override
        public void onSendResult(int requestType, int result, int deviceType, WearableException exception) {
            Log.i(TAG, "requestType=" + requestType
                    + " result=" + result
                    + " " + ((exception != null) ? exception.getDescription() : null));
            if (result == Constants.SUCCESS) {
                Utils.showToast(App.getAppContext(), "Success");
                return;
            }

            Utils.showToast(App.getAppContext(), exception.getDescription());
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived >>>>>>>> ");
        if (messageEvent.getData() != null) {
            String data = new String(messageEvent.getData());

            if(Constants.GET_DEVICE_INFO.equals(data)) {
                WearableManager.getInstance(getApplicationContext()).send(new DeviceInfo().toString(), mResult);
            } else {
                Log.d(TAG, "message from wear : " + data);
                showMessage(data);
            }
        }
    }


    private void showMessage(final String data) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
            }
        });
    }
}

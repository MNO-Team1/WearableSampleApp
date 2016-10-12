package com.samsung.mno.wearable;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.WearableException;
import com.samsung.mno.wearable.connection.IConnectionChange;
import com.samsung.mno.wearable.connection.ISendResult;
import com.samsung.mno.wearable.connection.WearableManager;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Wearable-MainActivity";
    private ImageView mGearImage;
    private ImageView mWearImage;
    private WearableManager mWearableManager;
    private WearableConnectionChangeListener mListener = new WearableConnectionChangeListener();
    private SendResult mResult = new SendResult();


    class SendResult implements ISendResult {
        @Override
        public void onSendResult(int requestType, int result, int deviceType, WearableException exception) {
            Log.i(TAG, "requestType=" + requestType
                    + " result=" + result
                    + " deviceType=" + deviceType
                    + " " + ((exception != null) ? exception.getDescription() : null));
            if (result == Constants.SUCCESS) {
//                Utils.showToast(App.getAppContext(), "Success device=" + deviceType);
                return;
            }

//            Utils.showToast(App.getAppContext(), exception.getDescription() + " device=" + deviceType);
        }
    }

    class WearableConnectionChangeListener implements IConnectionChange {
        @Override
        public void onConnectionChange(final boolean connected, final int deviceType) {
            Log.i(TAG, "onConnectionChange  Connected=" + connected + " deviceType=" + deviceType);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (connected) {
                        if (deviceType == Constants.TIZEN_GEAR) {
                            mGearImage.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                        } else {
                            mWearImage.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                        }
                    } else {
                        if (deviceType == Constants.TIZEN_GEAR) {
                            mGearImage.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black));
                        } else {
                            mWearImage.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black));
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWearImage = (ImageView) findViewById(R.id.wearimage);
        mGearImage = (ImageView) findViewById(R.id.gearimage);
        mWearableManager = WearableManager.getInstance(getApplicationContext());
        mWearableManager.registerConnectionChange(mListener);
    }


    public void onGetInfoClick(View view) {
        mWearableManager.send(Constants.GET_DEVICE_INFO, mResult);
    }

    @Override
    protected void onDestroy() {
        mWearableManager.unregisterConnecitonChange(mListener);
        super.onDestroy();
    }

}

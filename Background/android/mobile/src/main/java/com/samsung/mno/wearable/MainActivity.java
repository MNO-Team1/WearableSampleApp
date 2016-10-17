package com.samsung.mno.wearable;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.samsung.mno.wearable.common.Constants;
import com.samsung.mno.wearable.common.Utils;
import com.samsung.mno.wearable.common.WearableException;
import com.samsung.mno.wearable.connection.IConnectionChange;
import com.samsung.mno.wearable.connection.ISendResult;
import com.samsung.mno.wearable.connection.WearableManager;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Wearable-MainActivity";
    private LinearLayout mTizenLayout;
    private LinearLayout mAndroidLayout;
    private WearableManager mWearableManager;
    private WearableConnectionChangeListener mListener = new WearableConnectionChangeListener();
    private SendResult mResult = new SendResult();


    /**
     * Callback for send request result
     */
    class SendResult implements ISendResult {
        @Override
        public void onSendResult(int requestType, int result, int deviceType, WearableException exception) {
            Log.i(TAG, "requestType=" + requestType
                    + " result=" + result
                    + " deviceType=" + deviceType
                    + " " + ((exception != null) ? exception.getDescription() : null));
            if (result == Constants.SUCCESS) {
                return;
            }

            Utils.showToast(App.getAppContext(), exception.getDescription() + " device=" + deviceType);
        }
    }

    /**
     * Connection change listener for Wearables which are registered by activity
     */
    class WearableConnectionChangeListener implements IConnectionChange {
        @Override
        public void onConnectionChange(final boolean connected, final int deviceType) {
            Log.i(TAG, "onConnectionChange  Connected=" + connected + " deviceType=" + deviceType);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView androidImage = (ImageView) findViewById(R.id.wearimage);
                    ImageView tizenImage = (ImageView) findViewById(R.id.gearimage);

                    if (deviceType == Constants.TIZEN_GEAR) {
                        if (mTizenLayout.getVisibility() == View.VISIBLE) {
                            tizenImage.setColorFilter(ContextCompat.getColor(MainActivity.this,
                                    connected ? R.color.colorPrimary : R.color.black));
                        }
                    } else {
                        if (mAndroidLayout.getVisibility() == View.VISIBLE) {
                            androidImage.setColorFilter(ContextCompat.getColor(MainActivity.this,
                                    connected ? R.color.colorPrimary : R.color.black));
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
        mAndroidLayout = (LinearLayout) findViewById(R.id.android_layout);
        mTizenLayout = (LinearLayout) findViewById(R.id.tizen_layout);

        mWearableManager = WearableManager.getInstance(getApplicationContext());
        CheckBox androidCheckbox = (CheckBox) findViewById(R.id.android_checkbox);
        androidCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleAndroidRequest(isChecked, Constants.ANDROID_WEAR);
            }
        });

        CheckBox tizenCheckBox = (CheckBox) findViewById(R.id.tizen_checkbox);
        tizenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleAndroidRequest(isChecked, Constants.TIZEN_GEAR);
            }
        });
    }

    /**
     * Enable disable requested adapters
     *
     * @param enable
     */
    private void handleAndroidRequest(final boolean enable, final int adapter) {
        switch (adapter) {
            case Constants.ANDROID_WEAR:
                if (enable) {
                    mAndroidLayout.setVisibility(View.VISIBLE);
                    mWearableManager.addConnectionAdapter(Constants.ANDROID_WEAR, mListener);
                } else {
                    mAndroidLayout.setVisibility(View.INVISIBLE);
                    mWearableManager.removeConnectionAdapter(Constants.ANDROID_WEAR);
                }
                break;
            case Constants.TIZEN_GEAR:
                if (enable) {
                    mTizenLayout.setVisibility(View.VISIBLE);
                    mWearableManager.addConnectionAdapter(Constants.TIZEN_GEAR, mListener);
                } else {
                    mTizenLayout.setVisibility(View.INVISIBLE);
                    mWearableManager.removeConnectionAdapter(Constants.TIZEN_GEAR);
                }
                break;
            default:
                Utils.showToast(this, "Not supported");
        }
    }

    public void onGetInfoClick(View view) {
        mWearableManager.send(Constants.GET_DEVICE_INFO, mResult);
    }

    @Override
    protected void onDestroy() {
        mWearableManager.closeConnection();
        super.onDestroy();
    }
}

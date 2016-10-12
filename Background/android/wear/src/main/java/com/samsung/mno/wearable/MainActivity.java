package com.samsung.mno.wearable;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.mno.wearable.common.WearConstants;
import com.samsung.mno.wearable.connection.Connection;

public class MainActivity extends Activity {

    private TextView mTextView;
    private Button mGetDeviceInfo;

    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connection = Connection.getInstance(getApplicationContext());
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mGetDeviceInfo = (Button) stub.findViewById(R.id.getDeviceInfo);
            }
        });
    }

    public void getDeviceInfo(View view) {
        if (connection == null || !connection.isWearConnected()) {
            Toast.makeText(this, "Phone is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        connection.send(WearConstants.GET_DEVICE_INFO);
    }
}
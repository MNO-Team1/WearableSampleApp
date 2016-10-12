package com.samsung.mno.wearable.data;

import android.os.Build;

/**
 * Created by vgandhi on 8/19/16.
 */
public class DeviceInfo {

    private String mDeviceID = Build.ID;
    private String mDeviceHw = Build.HARDWARE;
    private String mDeviceBuildInfo = Build.DISPLAY;
    private String mDeviceDetails = Build.MANUFACTURER + " " + Build.MODEL;

    @Override
    public String toString() {
        return new String("Device-ID=" + mDeviceID + " Hardware Version=" + mDeviceHw + " BuildInfo=" + mDeviceBuildInfo + " Model=" + mDeviceDetails);
    }
}

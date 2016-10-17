package com.samsung.mno.wearable.data;

import android.os.Build;

/**
 * Created by vgandhi on 8/19/16.
 */
public class DeviceInfo {

    private static String mDeviceID = Build.ID;
    private static String mDeviceHw = Build.HARDWARE;
    private static String mDeviceBuildInfo = Build.DISPLAY;
    private static String mDeviceDetails = Build.MANUFACTURER + " " + Build.MODEL;

    public static String getDetails() {
        return new String("Device-ID=" + mDeviceID + " Hardware Version=" + mDeviceHw + " BuildInfo=" + mDeviceBuildInfo + " Model=" + mDeviceDetails);
    }

}

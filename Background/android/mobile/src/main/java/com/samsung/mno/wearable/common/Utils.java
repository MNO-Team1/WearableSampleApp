package com.samsung.mno.wearable.common;

import android.content.Context;
import android.widget.Toast;

import com.samsung.mno.wearable.R;

/**
 * Created by jayantheesh on 10/7/16.
 */

public class Utils {

    public static void showToast(final Context context, final String data) {
        Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
    }

    public static String getCapability(Context context) {
        String[] array = context.getResources().getStringArray(R.array.android_wear_capabilities);
        if (array != null && array.length != 0) {
            return array[0];
        }

        return null;
    }
}

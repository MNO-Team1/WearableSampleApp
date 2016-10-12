package com.samsung.mno.wearable.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jayantheesh on 10/7/16.
 */

public class Utils {

    public static void showToast(final Context context, final String data) {
        Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
    }
}

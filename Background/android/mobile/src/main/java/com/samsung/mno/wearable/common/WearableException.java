package com.samsung.mno.wearable.common;

import android.content.Context;

import com.samsung.mno.wearable.R;

import java.util.HashMap;

/**
 * Created by vgandhi on 8/22/16.
 */

public class WearableException extends Exception {
    private static HashMap<Integer, Integer> mExceptionMap = new HashMap<Integer, Integer>();
    private Context mContext;
    private int mExceptionType = NO_EXCEPTION;
    public final static int NO_EXCEPTION = 0;
    public final static int DEVICE_NOT_CONNECTED = 1;
    public final static int IO_EXCEPTION = 2;
    public final static int SEND_OPERATION_FAILED = 3;

    public WearableException(final Context context, final int exception) {
        mContext = context;
        mExceptionType = exception;
        init();
    }

    public WearableException(Exception exception) {
        if (exception == null) {
            return;
        }

        //TODO: CHeck for type of exception and update object here...
        mExceptionType = IO_EXCEPTION;
    }


    public final String getDescription() {
        return mContext.getString(mExceptionMap.get(mExceptionType));
    }

    private static void init() {
        mExceptionMap.put(DEVICE_NOT_CONNECTED, R.string.exception_not_connected);
        mExceptionMap.put(IO_EXCEPTION, R.string.io_exception);
        mExceptionMap.put(SEND_OPERATION_FAILED, R.string.send_operation_failed);
    }
}

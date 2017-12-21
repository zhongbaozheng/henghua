package com.hengdian.henghua.androidUtil;

import android.app.Activity;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by Anderok on 2017/1/24.
 */

public class BaseHandler extends Handler {
    private final WeakReference<Activity> mActivity;

    public BaseHandler(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
    }
}

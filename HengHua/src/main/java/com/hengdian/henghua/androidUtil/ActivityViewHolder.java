package com.hengdian.henghua.androidUtil;

import android.app.Activity;
import android.view.View;

/**
 * Created by ander on 2017/4/27.
 */

public abstract class ActivityViewHolder {
    private boolean hasFound = false;
    private Activity activity = null;
    private boolean refind = false;

    public ActivityViewHolder(Activity activity,boolean refind) {
        this.activity = activity;
        this.refind = refind;

        initViews();
    }

    /**
     * 初始化布局控件
     */
    private void initViews() {
        //找过持有就不用再找了
        if (hasFound && !refind)
            return;

        findViews();
        this.activity = null;//findView完毕,释放持有的activity

        hasFound = true;
    }

    /**
     * findViewById的泛型简写
     *
     * @param resId
     * @param <T>
     * @return
     */
    public   <T extends View> T $(int resId) {
        return (T) activity.findViewById(resId);
    }


    /**
     * 查找view，推荐使用$(R.id.itemRoot_ll);
     * 当然也可以用基本的(*view)findViewById(R.id.itemRoot_ll)
     */
    protected abstract void findViews();
}
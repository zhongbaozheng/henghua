package com.hengdian.henghua.androidUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hengdian.henghua.R;

/**
 * Created by ander on 2017/4/27.
 */

public abstract class ViewViewHolder {
    private boolean hasFound = false;
    private View view = null;
    private boolean refind = false;

    /**
     * @param view
     * @param refind 是否重新查找
     */
    public ViewViewHolder(View view, boolean refind) {
        this.view = view;
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
        this.view = null;////findView完毕,释放持有的activity

        hasFound = true;
    }

    /**
     * findViewById的泛型简写
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T $(int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 查找view，推荐使用$(R.id.itemRoot_ll);
     * 当然也可以用基本的(*view)findViewById(R.id.itemRoot_ll)
     */
    protected abstract void findViews();

}
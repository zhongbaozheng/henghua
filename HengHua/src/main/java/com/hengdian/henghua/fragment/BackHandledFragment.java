package com.hengdian.henghua.fragment;

import android.os.Bundle;

/**
 * Activity需要实现BackHandledFragment.BackHandleInterface;
 *
 *
 * Created by admin on 2017-01-16.
 */
public abstract class BackHandledFragment extends BaseFragment {

    public interface BackPressedHandler {
        public abstract void setSelectedFragment(BackHandledFragment selectedFragment);
    }

    protected BackPressedHandler mBackPressedHandler;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消费时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof BackPressedHandler)) {
            throw new ClassCastException("Hosting Activity must implement BackPressedHandler.BackHandledInterface");
        } else {
            this.mBackPressedHandler = (BackPressedHandler) getActivity();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶  
        //mBackPressedHandler.setSelectedFragment(this);
    }
}

package com.hengdian.henghua.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.hengdian.henghua.androidUtil.MyApplication;

/**
 * 主要为了初始化一些内容，如：xutils
 * <p>
 * Created by Anderok on 2017/1/13.
 */


public class BaseActivity extends AppCompatActivity {
    MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
//        x.view().inject(this);
//        mApp = MyApplication.getINSTANCE();
        mApp = (MyApplication) getApplication();

    }


    public MyApplication getMyApplication(){
        return mApp;
    }
}

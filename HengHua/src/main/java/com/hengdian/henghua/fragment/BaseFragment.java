package com.hengdian.henghua.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hengdian.henghua.androidUtil.SharePerferenceUtil;
import com.hengdian.henghua.utils.Constant;

/**
 * Created by Anderok on 2017/1/6.
 */

public abstract class BaseFragment extends Fragment {
    public View rootView;//用于缓存Fragment view
    public boolean keepRootView = true;//是否缓存Fragment view
    public Context activityCtx;
    public Bundle mArguments;
    public boolean isLoading = false;//是否正在加载/请求数据

    public String fromTag = "";//来源标志
    public String chapterID = ""; //章节ID           //一进来就记录当前的章节ID
    public String chapterName = "";
    public String bookID = ""; //教材ID
    public String bookName = "";

    /**
     * 获取传过来的标志
     */
    private void initJumpIndex() {
        if (mArguments != null) {
            fromTag = mArguments.getString(Constant.Jump.FROM_TAG);
            chapterID = mArguments.getString(Constant.Jump.CHAPTER_ID);
            chapterName = mArguments.getString(Constant.Jump.CHAPTER_NAME);
            bookID = mArguments.getString(Constant.Jump.BOOK_ID);
            bookName = mArguments.getString(Constant.Jump.BOOK_NAME);
        }

        Log.e("BaseFragment", "fromTag：" + fromTag + ",bookID：" + bookID + ",chapterID:" + chapterID + "，BookName：" + bookName + ",chapterName:" + chapterName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("Base onCreate", this.getClass().getName() + " onCreate");

        super.onCreate(savedInstanceState);

        activityCtx = getActivity();
        mArguments = getArguments();
        SharePerferenceUtil.register(activityCtx);

        initJumpIndex();
        initChapterId();

    }

    public void initChapterId(){}




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("Base onCreateView", this.getClass().getName() + " onCreateView");

        //Fragment之间切换时每次都会调用onCreateView方法，
        // 导致每次Fragment的布局都重绘，无法保持Fragment原有状态。
        //解决办法：在Fragment onCreateView方法中缓存View.

        if (keepRootView) {
            //rootView用于缓存Fragment view
            if (rootView == null) {
                rootView = initFragmentView(inflater, container, savedInstanceState);
            }
            //缓存的rootView需要判断是否已经被加过parent，
            // 如果有parent需要从parent删除，
            // 要不然会发生这个rootView已经有parent的错误
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        } else {
            rootView = initFragmentView(inflater, container, savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //加载数据
        initFragmentData();
    }

    /**
     * onCreateView调用
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public abstract View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * onActivityCreated调用
     */
    public abstract void initFragmentData();

}


package com.hengdian.henghua.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.adapter.ReviewListExpandAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.model.contentDataModel.Rs_CWXX_BooksWithChapters;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.GsonUtil;
import com.hengdian.henghua.utils.RefreshHolder;

/**
 * Created by Anderok on 2017/1/6.
 */

public class ReviewFragment extends BaseFragment {
    ViewHolder viewHolder;

    public ReviewListExpandAdapter myAdapter;
    public Rs_CWXX_BooksWithChapters data;
    public int lastPosition = -1;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {

                case Constant.HandlerFlag.REQUEST_DATA_ON_RESULT:

                    if (data == null) {
                        viewHolder.tipTV.setText("数据加载失败...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);
                        return;
                    } else if (data.getStatus() != 200) {
                        viewHolder.tipTV.setText("数据加载失败：" + data.getStatusMsg());
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);

                        RefreshHolder.REVIEW_LIST = RefreshHolder.REFRESH_NET;
                        return;

                    } else if (data.getBookList().size() == 0) {
                        viewHolder.tipTV.setText("没有内容...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);
                        return;
                    }

                    viewHolder.bookChapterELV.setGroupIndicator(null); //去除折叠指示箭头

                    myAdapter = new ReviewListExpandAdapter(ReviewFragment.this, activityCtx, data);

                    viewHolder.bookChapterELV.setAdapter(myAdapter);
                    if (lastPosition != -1) {
                        viewHolder.bookChapterELV.expandGroup(lastPosition);
                    }

                    //MyApplication.appHandler.sendEmptyMessage(Constant.HandlerFlag.CLOSE_LEFT_MENU);

                    OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT);

                    break;
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onResume() {

//        ((MainActivity) activityCtx).setSelectedFragment(this);

        if ((data == null || data.getStatus() != 200 || data.getBookList().size() == 0) && !isLoading) {
            RefreshHolder.REVIEW_LIST = RefreshHolder.REFRESH_NET;
            Log.e("net refresh","is true");
        }
        getData();
        super.onResume();
    }


    @Override
    public void initFragmentData() {

        if (MyApplication.isLogInSuccess) {
            Log.e("isLogInSuccess",MyApplication.isLogInSuccess+"");
            getData();
        } else {

            MyApplication.getAppHandler().sendEmptyMessage(Constant.HandlerFlag.LOGIN_IN);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            }, 1000);
        }
    }


    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //rootView = View.inflate(activityCtx, R.layout.fr_tab_review, null);
        rootView = inflater.inflate(R.layout.fr_tab_review, null);

        if (viewHolder == null) {
            viewHolder = new ViewHolder(rootView);
        }

        initActionListener();

        return rootView;
    }

    private void initActionListener() {
        //item点击监听 ,实现只能展开一个group
        viewHolder.bookChapterELV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                //默认情况下为可展开(未展开)，记录展开的位置
                if (lastPosition == -1) {
                    //展开选中的group
                    viewHolder.bookChapterELV.expandGroup(groupPosition);
                    lastPosition = groupPosition;

                } else if (lastPosition == groupPosition) {
                    //点击了同一个group，将其关闭
                    viewHolder.bookChapterELV.collapseGroup(lastPosition);
                    //重置（同一个条目点两次等于没点）
                    lastPosition = -1;
                } else {
                    //第二次点击了第二个条目，关闭上一个条目
                    viewHolder.bookChapterELV.collapseGroup(lastPosition);
                    viewHolder.bookChapterELV.expandGroup(groupPosition);
                    lastPosition = groupPosition;
                }
                //默认false，true点击不会展开group
                return true;
            }
        });
    }

    @Override
    public void onPause() {

        if (data != null && data.getStatus() == 200) {
            new DBUtil(activityCtx).saveReviewBookChapterList(data);
        }

        super.onPause();
    }

    private void getData() {
        if (isLoading) {
            return;
        }

        isLoading = true;

        if (viewHolder != null) {
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_PROGRESS_BAR);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.e(LOGTAG.FRAG_REVIEW_LIST, "获取教材章节列表数据" + RefreshHolder.REVIEW_LIST);

                    //如果本地获取失败或者要网络获取，就网络获取
                    if (data == null || RefreshHolder.REVIEW_LIST == RefreshHolder.REFRESH_NET && NetUtil.isNetworkActive(activityCtx)) {
                        data = DataRequestUtil.cwxx_getAllBooksWithChapters(MyApplication.getAccountInfo().getTokenID());
                        Log.e("data",GsonUtil.toJson(data));
                        if (data != null && data.getStatus() == 200) {
                            new DBUtil(activityCtx).saveReviewBookChapterList(data);
                        }
                        RefreshHolder.REVIEW_LIST = RefreshHolder.REFRESH_LOCAL;
                    }

                    data = new DBUtil(activityCtx).getReviewBookChapterList();
                    LogUtil.e(LOGTAG.FRAG_REVIEW_LIST, GsonUtil.toJson(data) + "===local");
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_REVIEW_LIST, "获取教材章节列表数据异常");
                    e.printStackTrace();
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.REQUEST_DATA_ON_RESULT);
            }
        }).start();
    }

    public class ViewHolder {
        RelativeLayout tipRL;
        ProgressBar progressBar;
        TextView tipTV;
        RelativeLayout courseRootRL;
        ExpandableListView bookChapterELV;

        public View[] showGroup;


        ViewHolder(View view) {
            findViews(view);
        }

        /**
         * 初始化布局控件,比注解节省系统开销
         */
        private void findViews(View view) {
            tipRL = (RelativeLayout) view.findViewById(R.id.tip_rl);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            tipTV = (TextView) view.findViewById(R.id.tip_tv);
            courseRootRL = (RelativeLayout) view.findViewById(R.id.fr_course);
            bookChapterELV = (ExpandableListView) view.findViewById(R.id.book_chapter_elv);

            showGroup = new View[]{progressBar, tipTV, bookChapterELV};

//            bookChapterELV.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    switch (motionEvent.getAction()){
//
//                    }
//                    return false;
//                }
//            });
        }
    }
}

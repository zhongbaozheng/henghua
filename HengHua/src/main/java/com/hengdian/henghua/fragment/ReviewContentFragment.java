package com.hengdian.henghua.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.adapter.PagerScrollAdapter;
import com.hengdian.henghua.adapter.ReviewNavCardLvAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.model.contentDataModel.ChapterContent;
import com.hengdian.henghua.model.contentDataModel.Rs_CW_ChapterContents;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.GadgetUtil;
import com.hengdian.henghua.utils.RefreshHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * 重温 内容页
 */
public class ReviewContentFragment extends BaseFragment implements View.OnClickListener {
    //private SpannableString SpannableStr;
    ContentActivity activity;
    public ViewHolder viewHolder;
    private boolean  is_clean = false;
    public List<View> mListView = new ArrayList<>();
    private PagerScrollAdapter mAdapter;

    public List<ChapterContent> chapterContentList = new ArrayList<>();

    //答题卡显示状态,true=已打开
    public boolean isNavCardShowed = false;

    public Rs_CW_ChapterContents data;
    private ChapterContent curContent;

    public int contentSize = 0;//内容总数
    public int curIndex1 = 0; //记录当前题号


    private boolean isAllRead() {
        if (data == null || data.getChapterContentList().size() == 0) {
            return true;
        }

        List<ChapterContent> list = data.getChapterContentList();

        for (ChapterContent content : list) {
            if (content.getState() < ChapterContent.STATE1_READING) {
                return false;
            }
        }

        return true;
    }


    /**
     * 提示更新内容
     */
    private void checkToReFresh() {
        String msg = "";
        if (!isAllRead()) {
            msg = "还有内容没看完呢，";
        }

        msg += "更新后将清除本章节内容的阅读进度和标记，是否继续更新？";

        AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
        builder.setTitle("更新提示")
                .setMessage(msg)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RefreshHolder.REVIEW_CONTENT = RefreshHolder.REFRESH_NET;
                                getData();
                                is_clean = true;
                                dialog.cancel();
                                //进度更新的同时，关闭界面，通过onPause()保存数据，并退出返回主界面
                                onPause();
                                getActivity().finish();
                                ToastUtil.toastMsgShort("进度已更新！");
                            }
                        }
                )

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                )
                .show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ContentActivity) getActivity();
    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.HandlerFlag.GET_DATA_ON_RESULT:

                    showNavCard(false);

                    if (data == null) {
                        viewHolder.tipTV.setText("数据加载失败...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);
                        return;
                    } else if (data.getStatus() != 200) {
                        viewHolder.tipTV.setText("数据加载失败：" + data.getStatusMsg());
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);

                        RefreshHolder.REVIEW_CONTENT = RefreshHolder.REFRESH_NET;
                        return;

                    }

                    contentSize = data.getChapterContentList().size();
                    curIndex1 = data.curIndex;
                    chapterContentList = data.getChapterContentList();

                    // //组装view，再到Adapter组装数据
                    mAdapter = new PagerScrollAdapter(activityCtx,chapterContentList,mListView);
                    for(int i=0;i<data.getChapterContentList().size();i++){
                        View v = View.inflate(activityCtx,R.layout.review_scroll_pager,null);
                        mListView.add(v);
                    }

                    viewHolder.mViewPager.setAdapter(mAdapter);
                    //设置滑动监听，判断是左还是右
                    viewHolder.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            curIndex1 = position;
                            setViewData(data.getChapterContentList(),position);
                            //更新我们列表的item
                            if(isNavCardShowed){
                                navCardLvAdapter.notifyDataSetChanged();
                                navCardLvAdapter.reviewNavCardGvAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                    OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT);

                    //设置一进来就显示内容
                    setViewData(data.getChapterContentList(), curIndex1);
                    break;
            }
        }
    };


    /**
     * @param listData
     * @param index    当前章节内容位置
     */
    public void setViewData(List<ChapterContent> listData, final int index) {

        chapterContentList = listData;

        viewHolder.contentSV.fullScroll(ScrollView.FOCUS_UP);
        //无数据
        if (listData == null || listData.size() == 0) {
            viewHolder.tipTV.setText("没有内容...");
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);
            viewHolder.contentLL.setVisibility(View.INVISIBLE);
            return;
        }

        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT);


        /**
         * 重置上一个题目为非选中
         */
        if (curContent != null) {
            curContent.isShow = false;
        }

        curContent = listData.get(index);
        curContent.isShow = true;

        curContent.setState(ChapterContent.STATE1_READING);

        viewHolder.mViewPager.setCurrentItem(index);

    }

    /**
     *
     */
    private void getData() {
        if (isLoading) {
            return;
        }

        isLoading = true;

        if (viewHolder != null) {
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_PROGRESS_BAR);
        }

        contentSize = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(LOGTAG.FRAG_REVIEW_CONTENT, "获取知识重温内容数据");
                try {
                    if (data == null || RefreshHolder.REVIEW_CONTENT == RefreshHolder.REFRESH_LOCAL) {
                        data = new DBUtil(activityCtx).getReviewContent(bookID, chapterID);
                    }

                    if ((data == null || data.getStatus() != 200 || RefreshHolder.REVIEW_CONTENT == RefreshHolder.REFRESH_NET)&& NetUtil.isNetworkActive(activityCtx)) {
                        data = DataRequestUtil.cw_getChapterContents(chapterID, MyApplication.getAccountInfo().getTokenID());
                        if (data != null && data.getStatus() == 200) {
                            new DBUtil(activityCtx).saveReviewContent(bookID, chapterID, data);
                        }
                        RefreshHolder.REVIEW_CONTENT = RefreshHolder.REFRESH_LOCAL;

                    }
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_REVIEW_CONTENT, "获取知识重温内容异常");
                    e.printStackTrace();
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);
            }
        }).start();

    }


    @Override
    public void onClick(View view) {

        if (navCardLvAdapter != null) {
            navCardLvAdapter.notifyDataSetChanged();
        }

        switch (view.getId()) {

            case R.id.lastOne_ll:
                backForward();
                break;

            case R.id.nextOne_ll:
                goForward();
                break;

            case R.id.centerButton_ll:

                if (data != null && data.getChapterContentList().size() > 0) {
                    switchNavCardShowHide();
                }
                break;

            case R.id.titleBarBack_iv:

                ((ContentActivity) activityCtx).finish();
                break;
        }

    }

    private void backForward() {
        viewHolder.contentLL.setSelected(false);
        viewHolder.mViewPager.arrowScroll(1);    //上一页
        if (curIndex1 < 1) {
            ToastUtil.toastMsgShort("已到开头");
        } else {
            setViewData(data.getChapterContentList(), curIndex1);
        }
    }


    private void goForward() {
        viewHolder.mViewPager.arrowScroll(2);   //下一页
        viewHolder.navCardLL.setSelected(false);
        if (curIndex1 >= contentSize - 1) {
            ToastUtil.toastMsgShort("已到结尾");

        } else { //不是最后一题
            setViewData(data.getChapterContentList(), curIndex1);
        }
    }

    /**
     * 切换答题卡显示或隐藏
     */
    public void switchNavCardShowHide() {
        //如果导航卡已经显示,则隐藏
        showNavCard(!isNavCardShowed);
    }

    ReviewNavCardLvAdapter navCardLvAdapter;

    /**
     * 显示或隐藏答题卡
     */
    public void showNavCard(boolean show) {
        if (show) {
            viewHolder.centerButtonLL.setSelected(true);
            viewHolder.navCardLL.setVisibility(View.VISIBLE);

            navCardLvAdapter = new ReviewNavCardLvAdapter(activityCtx, data.getChapterContentList());
//            this.setListViewHeightBasedOnChildren(viewHolder.navCardLV); //去掉这行代码，否则会把item宽高固定死，迷之bug，解决了
            viewHolder.navCardLV.setAdapter(navCardLvAdapter);
//            navCardLvAdapter.reviewNavCardGvAdapter.notifyDataSetChanged();
        } else {
            viewHolder.centerButtonLL.setSelected(false);
            viewHolder.navCardLL.setVisibility(View.INVISIBLE);
        }

        isNavCardShowed = show;
    }


    /**
     * 计算listview item 宽高
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = viewHolder.navCardLV.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(5, 5, 5, 5);
        listView.setLayoutParams(params);
    }


    /**
     * 设置状态栏
     */
    private void initTopBar() {

        activity.viewHolder.usrIV.setVisibility(View.INVISIBLE);
        activity.viewHolder.backIV.setVisibility(View.VISIBLE);
        activity.viewHolder.titleBarTextTV.setText("知识重温");
        activity.viewHolder.titleBarTextTV.setVisibility(View.VISIBLE);
        activity.viewHolder.commitTV.setText("更新");
        activity.viewHolder.commitTV.setVisibility(View.VISIBLE);
        activity.viewHolder.commitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToReFresh();
            }
        });

//        activity.viewHolder.questionTypeChooserLO.setVisibility(View.INVISIBLE);

    }

    /**
     * 设置底部按钮
     */
    private void initBottomButton() {
        viewHolder.centerButtonTV.setText("导航卡");
        viewHolder.centerButtonIV.setImageResource(R.drawable.select_test_nav_img);
        viewHolder.lastOneTV.setText("上一条");
        viewHolder.nextOneTV.setText("下一条");

        viewHolder.centerButtonLL.setOnClickListener(this);
        viewHolder.lastOneLL.setOnClickListener(this);
        viewHolder.nextOneLL.setOnClickListener(this);
    }


    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fr_review_content, null);

        if (viewHolder == null) {
            viewHolder = new ViewHolder(rootView);
        }

        activity.viewHolder.backIV.setOnClickListener(this);

//        viewHolder.contentSV.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int direct = UIUtil.getTouchDirection(event, 50);
//                if (direct == UIUtil.TOUCH_LEFT) {
//                    goForward();
//
//                } else if (direct == UIUtil.TOUCH_RIGHT) {
//                    backForward();
//                }
//
//                return false;
//            }
//        });


        initTopBar();
        initBottomButton();

        return rootView;

    }

    @Override
    public void initFragmentData() {

        LogUtil.i(LOGTAG.FRAG_REVIEW_CONTENT, "fromTag:" + fromTag + ",bookID:" + bookID + ",chapterID:" + chapterID);
        getData();

    }

    @Override
    public void onResume() {
//        ((ContentActivity) activityCtx).setSelectedFragment(this);

        if ((data == null || data.getStatus() != 200 || data.getChapterContentList().size() == 0) && !isLoading) {
            RefreshHolder.REVIEW_CONTENT = RefreshHolder.REFRESH_NET;
            getData();
        }
        super.onResume();
    }

    @Override
    public void onPause() {

        if (data != null && data.getStatus() == 200) {
            Log.e("curIndex1",curIndex1+"");
            data.setCurIndex(curIndex1);
            if (!is_clean){
                new DBUtil(activityCtx).saveReviewContent(bookID, chapterID, data);
            }

        }

        super.onPause();
    }

    public class ViewHolder {

        ViewPager mViewPager;

        // RelativeLayout tipRL;
        TextView tipTV;
        ProgressBar progressBar;

        LinearLayout lastOneLL;
        public LinearLayout centerButtonLL;

        LinearLayout nextOneLL;
        ImageView lastOneIV;
        ImageView centerButtonIV;

        ImageView nextOneIV;
        TextView lastOneTV;
        TextView centerButtonTV;

        TextView nextOneTV;

        LinearLayout contentLL;
        ScrollView contentSV;

//        TextView itemNumTV;
//        TextView itemTitleTV;
//        TextView contentTV;

        LinearLayout navCardLL;
        ListView navCardLV;

        View[] showGroup;

        ViewHolder(View view) {
            findViews(view);
        }

        /**
         * 初始化布局控件,比注解节省系统开销
         */
        private void findViews(View view) {

            lastOneLL = (LinearLayout) view.findViewById(R.id.lastOne_ll);
            centerButtonLL = (LinearLayout) view.findViewById(R.id.centerButton_ll);
            nextOneLL = (LinearLayout) view.findViewById(R.id.nextOne_ll);

            lastOneIV = (ImageView) view.findViewById(R.id.lastOne_iv);
            centerButtonIV = (ImageView) view.findViewById(R.id.centerButton_iv);
            nextOneIV = (ImageView) view.findViewById(R.id.nextOne_iv);

            lastOneTV = (TextView) view.findViewById(R.id.lastOne_tv);
            centerButtonTV = (TextView) view.findViewById(R.id.centerButton_tv);
            nextOneTV = (TextView) view.findViewById(R.id.nextOne_tv);
            //初始化ViewPager
            mViewPager = (ViewPager)view.findViewById(R.id.viewPager);


            // tipRL = (RelativeLayout) view.findViewById(R.id.tip_rl);
            tipTV = (TextView) view.findViewById(R.id.tip_tv);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            contentLL = (LinearLayout) view.findViewById(R.id.content_ll);
            contentSV = (ScrollView) view.findViewById(R.id.content_sv);

//            itemNumTV = (TextView) view.findViewById(R.id.itemNum_tv);
//            itemTitleTV = (TextView) view.findViewById(R.id.itemTitle_tv);
//            itemTitleTV.setVisibility(View.GONE);
//            contentTV = (TextView) view.findViewById(R.id.content_tv);

            navCardLL = (LinearLayout) view.findViewById(R.id.navCard_ll);
            navCardLV = (ListView) view.findViewById(R.id.navCard_lv);
            //需要变
//            showGroup = new View[]{progressBar, tipTV, contentLL};
            showGroup = new View[]{progressBar,tipTV,contentLL};

            navCardLL.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });


        }
    }
}

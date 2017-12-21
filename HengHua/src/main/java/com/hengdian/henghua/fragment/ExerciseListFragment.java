package com.hengdian.henghua.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.adapter.ExerciseListExpandAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.model.contentDataModel.Rs_CWXX_BooksWithChapters;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.RefreshHolder;

/**
 * Created by Anderok on 2017/1/6.
 */

public class ExerciseListFragment extends BackHandledFragment {
    private ViewHolder viewHolder;
    public Rs_CWXX_BooksWithChapters data;

    public int lastPosition = -1;
    public ExerciseListExpandAdapter myAdapter;

    public Rs_CWXX_BooksWithChapters getDataOfFragment() {
        return data;
    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.HandlerFlag.GET_DATA_ON_RESULT:

                    //如果没有数据
                    if (data == null) {
                        viewHolder.tipTV.setText("数据加载失败...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        return;
                    } else if (data.getStatus() != 200) {
                        viewHolder.tipTV.setText("数据加载失败：" + data.getStatusMsg());
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());

                        RefreshHolder.TEST_EXERCISE_LIST = RefreshHolder.REFRESH_NET;
                        return;
                    } else if (data.getBookList().size() == 0) {
                        viewHolder.tipTV.setText("没有内容...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        return;
                    }

                    myAdapter = new ExerciseListExpandAdapter(ExerciseListFragment.this, activityCtx, data);
                    viewHolder.bookChapterELV.setAdapter(myAdapter);

                    if (lastPosition != -1) {
                        viewHolder.bookChapterELV.expandGroup(lastPosition);
                    }

                    OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT, (MainActivity) getActivity());
                    break;

            }
        }
    };


    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
    public void initFragmentData() {
        getData();
    }


    public void getData() {

        if (isLoading) {
            return;
        }

        isLoading = true;

        if (viewHolder != null) {
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_PROGRESS_BAR, (MainActivity) getActivity());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "获取练习列表数据");

                    if ((data == null || RefreshHolder.TEST_EXERCISE_LIST == RefreshHolder.REFRESH_NET) && NetUtil.isNetworkActive(activityCtx)) {
                        data = DataRequestUtil.cwxx_getAllBooksWithChapters(MyApplication.getAccountInfo().getTokenID());

                        if (data != null && data.getStatus() == 200) {
                            new DBUtil(activityCtx).saveExerciseBookChapterList(data);
                        }
                        RefreshHolder.TEST_EXERCISE_LIST = RefreshHolder.REFRESH_LOCAL;
                    }

                    data = new DBUtil(activityCtx).getExerciseBookChapterList();

                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_EXERCISE_LIST, "获取练习列表数据");
                    e.printStackTrace();
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);
            }
        }).start();
    }

    @Override
    public boolean onBackPressed() {
        //如果不是在线测试页，忽略返回动作处理
        if (((MainActivity) activityCtx).curTabIndex != 1) {
            return false;
        }

        LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "练习模式Frag执行返回");

        TestFragment testFragment = (TestFragment) this.getParentFragment();

        if (testFragment == null) {
            return false;
        }

        FragmentManager childFragManager = testFragment.getChildFragmentManager();
        FragmentTransaction fragmentTrans = childFragManager.beginTransaction();

        ExerciseListFragment exerciseFrag = (ExerciseListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_EXERCISE_CHAPTER);

        if (exerciseFrag != null)
            fragmentTrans.hide(exerciseFrag);

        fragmentTrans.commit();

        //按了返回，应该将选中设为 父即testFragment,没有或不需要控制则null
        ((MainActivity) activityCtx).setSelectedFragment(null);
        //显示选项
        testFragment.showTestModeOption(true);

        FragmentManager fragManager = getFragmentManager();
        //如果当前是在线测试
//        if (((MainActivity) activityCtx).tabHost.getCurrentTab() == 1) {
//            fragManager.popBackStack();
//        } else {
//        int backStackEntryCount = fragManager.getBackStackEntryCount();
        // ToastUtil.toastMsgShort(activityCtx, "返回栈Frag个数：" + backStackEntryCount, false);
        // fragManager.popBackStackImmediate();
        //ToastUtil.toastMsgShort(activityCtx, "返回栈Frag个数：" + backStackEntryCount, false);
//            if (backStackEntryCount > 0) {
//                for (int i = 0; i < backStackEntryCount; i++) {
//                    FragmentManager.BackStackEntry tag = fragManager.getBackStackEntryAt(i);
//                    fragManager.popBackStackImmediate();
//                   // ToastUtil.toastMsgShort(activityCtx, "弹出的Frag：" + tag, false);
//                }
//            }
//            Toast.makeText(ctx, "在这里也可以弹窗然后finish", Toast.LENGTH_SHORT).show();
//            int currentTab = ((MainActivity) activityCtx).tabHost.getCurrentTab();
//            ((MainActivity) activityCtx).tabHost.onTabChanged(((MainActivity) activityCtx).tabText[currentTab]);
//            return true;
//        }

        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "onActivityResult,requestCode" + requestCode + ",resultCode:" + resultCode);

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {

//        ((MainActivity) activityCtx).setSelectedFragment(this);

        if ((data == null || data.getStatus() != 200 || data.getBookList().size() == 0) && !isLoading) {
            RefreshHolder.TEST_EXERCISE_LIST = RefreshHolder.REFRESH_NET;
        }
        getData();
        super.onResume();
    }


    @Override
    public void onPause() {
        LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "onPause");

        if (data != null && data.getStatus() == 200) {
            new DBUtil(activityCtx).saveExerciseBookChapterList(data);
        }

        super.onPause();
    }


    public class ViewHolder {
        RelativeLayout tipRL;
        ProgressBar progressBar;
        TextView tipTV;
        RelativeLayout courseRootRL;
        ExpandableListView bookChapterELV;

        View[] showGroup;

        ViewHolder(View view) {
            findViews(view);
        }

        private void findViews(View view) {
            tipRL = (RelativeLayout) view.findViewById(R.id.tip_rl);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            tipTV = (TextView) view.findViewById(R.id.tip_tv);
            courseRootRL = (RelativeLayout) view.findViewById(R.id.fr_course);
            bookChapterELV = (ExpandableListView) view.findViewById(R.id.book_chapter_elv);

            showGroup = new View[]{progressBar, tipTV, bookChapterELV};
        }
    }
}

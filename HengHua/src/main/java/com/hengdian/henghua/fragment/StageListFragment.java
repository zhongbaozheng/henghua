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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.adapter.StageListAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.model.contentDataModel.Chapter;
import com.hengdian.henghua.model.contentDataModel.Rs_CG_Chapters;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.RefreshHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andernok on 2017/2/14.
 */

public class StageListFragment extends BackHandledFragment {
    private ViewHolder viewHolder;
    //数据对象
    private Rs_CG_Chapters data;


    public Rs_CG_Chapters getDataOfFragment() {
        return data;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.HandlerFlag.GET_DATA_ON_RESULT:

                    if (data == null) {
                        viewHolder.tipTV.setText("数据加载失败...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        return;

                    } else if (data.getStatus() != 200) {
                        viewHolder.tipTV.setText("数据加载失败：" + data.getStatusMsg());
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        RefreshHolder.TEST_STAGE_LIST = RefreshHolder.REFRESH_NET;
                        return;

                    } else if (data.getChapterList().size() == 0) {
                        viewHolder.tipTV.setText("没有内容...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        return;
                    }

                    List<Map<String, Object>> lisItems = new ArrayList<Map<String, Object>>();

                    List<Chapter> chapterList = data.getChapterList();

                    for (int i = 0; i < chapterList.size(); i++) {

                        Chapter chapter = chapterList.get(i);

                        //如果是第一关，必须解锁
                        if (i == 0 && chapter.getState() == Chapter.STATE0_DEFAULT) {
                            chapter.setState(Chapter.STATE1_UNLOCKED);
                        }

                        //如果不是第一关，则上一关通过了，这关就解锁
                        if (i >= 1 && chapterList.get(i - 1).getState() == Chapter.STATE2_PASSED
                                && chapter.getState() == Chapter.STATE0_DEFAULT) {
                            chapter.setState(Chapter.STATE1_UNLOCKED);
                        }

                        Map<String, Object> lisItem = new HashMap<String, Object>();
                        lisItem.put(StageListAdapter.key[0], "第" + (i + 1) + "关");
                        lisItem.put(StageListAdapter.key[1], chapter.getChapterName());
                        lisItem.put(StageListAdapter.STATUS_KEY, chapter.getState());
                        lisItem.put(Constant.Jump.CHAPTER_ID, chapter.getChapterID() + "");
                        lisItem.put(Constant.Jump.BOOK_ID, mArguments.getString(Constant.Jump.BOOK_ID));

                        lisItems.add(lisItem);
                    }

                    StageListAdapter adapter = new StageListAdapter(activityCtx, lisItems,
                            R.layout.stage_list_item, StageListAdapter.key, StageListAdapter.viewId);
                    viewHolder.courseListLV.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                    OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT, (MainActivity) getActivity());

                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {

        //如果不是在线测试页，忽略返回动作处理
        if (((MainActivity) activityCtx).curTabIndex != 1) {
            return false;
        }

        LogUtil.i(LOGTAG.FRAG_STAGE_LIST, "闯关模式关卡Frag执行返回");

        BreakthroughListFragment brkthFrag = (BreakthroughListFragment) this.getParentFragment();

        if (brkthFrag == null) {
            return false;
        }

        FragmentManager childFragManager = brkthFrag.getChildFragmentManager();
        FragmentTransaction fragmentTrans = childFragManager.beginTransaction();

        StageListFragment stageListFragment = (StageListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_STAGE);

        if (stageListFragment != null) {
            //TODO 保存数据
            LogUtil.i(LOGTAG.FRAG_STAGE_LIST, "闯关模式关卡Frag执行返回");

            //按了返回，应该将选中设为 父即testFragment,没有或不需要控制则null
            ((MainActivity) activityCtx).setSelectedFragment(brkthFrag);
        }

        getFragmentManager().popBackStackImmediate();
        fragmentTrans.commit();

        return true;
    }

    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_test_stage_exam, null);
        viewHolder = new ViewHolder(rootView);
        //viewHolder.courseListLV.setVisibility(View.GONE);
        if (data != null) {
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.JUST_HIDE_PROGRESS_BAR, (MainActivity) getActivity());
        }
        return rootView;
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
                LogUtil.i(LOGTAG.FRAG_STAGE_LIST, "获取关卡列表数据");

                try {
                    if (data == null || RefreshHolder.TEST_STAGE_LIST == RefreshHolder.REFRESH_NET) {
                        data = DataRequestUtil.cg_getChapters(bookID, MyApplication.getAccountInfo().getTokenID());
                    }
                    data = new DBUtil(activityCtx).getStageList(bookID, data);
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_STAGE_LIST, "获取关卡列表数据异常");
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);

            }
        }).start();
    }

    @Override
    public void onResume() {
        ((MainActivity) activityCtx).setSelectedFragment(this);

        if ((data == null || data.getStatus() != 200 || data.getChapterList().size() == 0) && !isLoading && NetUtil.isNetworkActive(activityCtx)) {
            RefreshHolder.TEST_STAGE_LIST = RefreshHolder.REFRESH_NET;
        }
        getData();
        super.onResume();
    }

    @Override
    public void onPause() {
        //TODO 保存数据
        if (data != null && data.getStatus() == 200) {

        }

        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(LOGTAG.FRAG_STAGE_LIST, "onActivityResult,requestCode:" + requestCode + ",resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class ViewHolder {
        RelativeLayout tipRL;
        ProgressBar progressBar;
        TextView tipTV;
        RelativeLayout courseRootRL;
        ListView courseListLV;
        FrameLayout subContainerFL;

        View[] showGroup;

        ViewHolder(View view) {
            findViews(view);
        }

        private void findViews(View view) {
            tipRL = (RelativeLayout) view.findViewById(R.id.tip_rl);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            tipTV = (TextView) view.findViewById(R.id.tip_tv);
            courseRootRL = (RelativeLayout) view.findViewById(R.id.fr_course);
            courseListLV = (ListView) view.findViewById(R.id.courseList_lv);

            subContainerFL = (FrameLayout) view.findViewById(R.id.subContainer_fl);

            showGroup = new View[]{progressBar, tipTV, courseListLV};
        }
    }
}

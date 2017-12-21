package com.hengdian.henghua.fragment;

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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.adapter.BreakthroughListAdapter;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.model.contentDataModel.Book;
import com.hengdian.henghua.model.contentDataModel.Rs_CG_Books;
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

public class BreakthroughListFragment extends BackHandledFragment {
    private ViewHolder viewHolder;
    //数据对象
    private Rs_CG_Books data;
    private List<Map<String, Object>> lisItems;
    private final String[] key = {"bookTV"};
    private final int[] viewId = {R.id.bookName_tv};


    public Rs_CG_Books getDataOfFragment() {
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

                        RefreshHolder.TEST_BREAKTHROUGH_LIST = RefreshHolder.REFRESH_NET;

                        return;

                    } else if (data.getBookList().size() == 0) {
                        viewHolder.tipTV.setText("没有内容...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        return;
                    }

                    lisItems = new ArrayList<Map<String, Object>>();

                    List<Book> bookList = data.getBookList();
                    Book book;
                    for (int i = 0; i < bookList.size(); i++) {
                        book = bookList.get(i);
                        Map<String, Object> lisItem = new HashMap<String, Object>();
                        lisItem.put(key[0], book.getBookName());
                        lisItem.put(Constant.Jump.BOOK_ID, book.getBookID() + "");

                        lisItems.add(lisItem);
                    }

                    SimpleAdapter adapter = new BreakthroughListAdapter(activityCtx, lisItems,
                            R.layout.stage_book_list_item, key, viewId);
                    viewHolder.courseListLV.setAdapter(adapter);

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

        TestFragment testFragment = (TestFragment) this.getParentFragment();

        FragmentManager childFragManager = testFragment.getChildFragmentManager();
        FragmentTransaction fragmentTrans = childFragManager.beginTransaction();

        BreakthroughListFragment breakthroughFrag = (BreakthroughListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_BREAKTHROUGH);

        if (breakthroughFrag != null)
            fragmentTrans.hide(breakthroughFrag);

        fragmentTrans.commit();

        //显示选项
        testFragment.showTestModeOption(true);

        //按了返回，应该将选中设为 父即testFragment,没有或不需要控制则null
        ((MainActivity) activityCtx).setSelectedFragment(null);

        return true;
    }


    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_test_stage_exam, null);

        viewHolder = new ViewHolder(rootView);

        if (data != null) {
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.JUST_HIDE_PROGRESS_BAR, (MainActivity) getActivity());
        }

        return rootView;
    }


    @Override
    public void initFragmentData() {
        //getData();
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
                LogUtil.i(LOGTAG.FRAG_BREAKTHROUGH_LIST, "获取闯关教材列表数据");

                try {
                    data = DataRequestUtil.cg_getBooks(MyApplication.getAccountInfo().getTokenID());
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_BREAKTHROUGH_LIST, "获取闯关教材列表数据异常");
                    e.printStackTrace();
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);
            }
        }).start();
    }


    @Override
    public void onResume() {
//        ((MainActivity) activityCtx).setSelectedFragment(this);

        if ((data == null || data.getStatus() != 200 || data.getBookList().size() == 0) && !isLoading && NetUtil.isNetworkActive(activityCtx)) {
                getData();
        }

        super.onResume();
    }

    @Override
    public void onPause() {

        if (data != null && data.getStatus() == 200) {

        }

        super.onPause();
    }

    class ViewHolder {
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
            tipRL = (RelativeLayout) rootView.findViewById(R.id.tip_rl);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            tipTV = (TextView) rootView.findViewById(R.id.tip_tv);
            courseRootRL = (RelativeLayout) rootView.findViewById(R.id.fr_course);
            courseListLV = (ListView) rootView.findViewById(R.id.courseList_lv);

            subContainerFL = (FrameLayout) rootView.findViewById(R.id.subContainer_fl);

            showGroup = new View[]{progressBar, tipTV, courseListLV};
        }
    }


}


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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.adapter.ExamListAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.model.contentDataModel.Book;
import com.hengdian.henghua.model.contentDataModel.BookWithScore;
import com.hengdian.henghua.model.contentDataModel.Rs_KS_Books;
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

public class ExamListFragment extends BackHandledFragment {
    private ViewHolder viewHolder;
    //数据对象
    private Rs_KS_Books data;

    private List<Map<String, Object>> lisItems;

    public Rs_KS_Books getDataOfFragment() {
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

                        RefreshHolder.TEST_EXAM_LIST = RefreshHolder.REFRESH_NET;
                        return;

                    } else if (data.getBookList().size() == 0) {
                        viewHolder.tipTV.setText("没有内容...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        return;
                    }

                    List<BookWithScore> bookList = data.getBookList();

                    lisItems = new ArrayList<Map<String, Object>>();
                    BookWithScore book;
                    for (int i = 0; i < bookList.size(); i++) {
                        book = bookList.get(i);

                        if (book.getTestAchieved() > 0 && book.getScore() == -1) {
                            book.setState(Book.STATE1_CONTINUE);
                        } else if (book.getScore() != -1) {
                            book.setState(Book.STATE2_ALL_DONE);
                        }

                        String statusBtnStr = "";

                        if (book.getState() == Book.STATE2_ALL_DONE) {
                            statusBtnStr = ExamListAdapter.SCORE_KEY + book.getScore();
                        } else {
                            statusBtnStr = ExamListAdapter.getBtnText(book.getState());
                        }


                        Map<String, Object> lisItem = new HashMap<String, Object>();
                        lisItem.put(Book.KEY_BOOK_ID, book.getBookID() + "");
                        lisItem.put(ExamListAdapter.key[0], book.getBookName());
                        lisItem.put(ExamListAdapter.key[1], book.getTestAchieved() == 100000 ? 0 : book.getTestAchieved());
                        lisItem.put(ExamListAdapter.key[2], book.getTestTotal());
                        //已作答人数API
//                        lisItem.put(ExamListAdapter.key[3], new Random().nextInt(100));
                        lisItem.put(ExamListAdapter.key[3], statusBtnStr);
                        // lisItem.put(ExamListAdapter.key[4], book.getScore() == -1 ? 0 : book.getScore());

                        lisItems.add(lisItem);
                    }

                    SimpleAdapter adapter = new ExamListAdapter(activityCtx, lisItems,
                            R.layout.exam_book_list_item, ExamListAdapter.key, ExamListAdapter.viewId);
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

        LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "考试模式Frag执行返回");

        TestFragment testFragment = (TestFragment) this.getParentFragment();

        if (testFragment == null) {
            return false;
        }

        FragmentManager childFragManager = testFragment.getChildFragmentManager();
        FragmentTransaction fragmentTrans = childFragManager.beginTransaction();

        ExamListFragment examListFrag = (ExamListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_EXAM);


        if (examListFrag != null)
            fragmentTrans.hide(examListFrag);

        fragmentTrans.commit();

        //按了返回，应该将选中设为 父即testFragment,没有或不需要控制则null
        ((MainActivity) activityCtx).setSelectedFragment(null);

        //显示选项
        testFragment.showTestModeOption(true);

        FragmentManager fragManager = getFragmentManager();

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
                LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "获取考试教材列表数据");

                try {
                    data = new DBUtil(activityCtx).getExamBookList(
                            DataRequestUtil.ks_getAllBooksWithScore(MyApplication.getAccountInfo().getTokenID()));
                } catch (Exception e) {
                    LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "获取考试教材列表数据异常");
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

//        if ((data == null || data.getStatus() != 200 || data.getBookList().size() == 0) && !isLoading && NetUtil.isNetworkActive(activityCtx)) {
//
//        }
        getData();

        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "onActivityResult,requestCode:" + requestCode + ",resultCode:" + resultCode);

        super.onActivityResult(requestCode, resultCode, data);
    }

    class ViewHolder {
        RelativeLayout tipRL;
        ProgressBar progressBar;
        TextView tipTV;
        RelativeLayout courseRootRL;
        ListView courseListLV;

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

            showGroup = new View[]{progressBar, tipTV, courseListLV};
        }
    }


}



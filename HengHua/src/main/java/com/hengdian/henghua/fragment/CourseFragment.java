package com.hengdian.henghua.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.hengdian.henghua.adapter.CourseListAdapter;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.model.contentDataModel.CourseWithBuyStatus;
import com.hengdian.henghua.model.contentDataModel.Rs_SP_CoursesWithBuyStatus;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.RefreshHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anderok on 2017/1/6.
 */

public class CourseFragment extends BaseFragment {
    private ViewHolder viewHolder;
    //数据对象
    private Rs_SP_CoursesWithBuyStatus data;


    private final String[] key = {"dateTV", "yearTV", "statusIV", "courseTitleTV", "courseDescTV", "buyStatusTV", "priceTV"};
    private final int[] viewId = {R.id.date_tv, R.id.year_tv, R.id.status_iv, R.id.courseTitle_tv, R.id.courseDesc_tv, R.id.buyStatus_tv, R.id.price_tv};

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

                        RefreshHolder.COURSE_LIST = RefreshHolder.REFRESH_NET;
                        return;

                    } else if (data.getCourseList().size() == 0) {
                        viewHolder.tipTV.setText("没有内容...");
                        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP, (MainActivity) getActivity());
                        return;
                    }

                    List<Map<String, Object>> lisItems = new ArrayList<Map<String, Object>>();

                    List<CourseWithBuyStatus> courseList = data.getCourseList();
                    CourseWithBuyStatus course;
                    for (int i = 0; i < courseList.size(); i++) {
                        course = courseList.get(i);
                        Map<String, Object> lisItem = new HashMap<String, Object>();
                        //TODO 日期
                        lisItem.put(key[0], "JAN 15");
                        lisItem.put(key[1], "2017");
                        lisItem.put(key[2], course.getBuyStatus() == 0 ? R.drawable.isbuy_false : R.drawable.isbuy_true);
                        lisItem.put(key[3], course.getCourseName());
                        lisItem.put(key[4], "课程简介：" + course.getSummary());
                        lisItem.put(key[5], course.getBuyStatus() == 0 ? "购 买" : "已购买");
                        lisItem.put(key[6], "￥:" + course.getPrice());

                        lisItems.add(lisItem);
                    }

                    SimpleAdapter adapter = new CourseListAdapter(activityCtx, lisItems,
                            R.layout.course_list_item, key, viewId);
                    viewHolder.courseListLV.setAdapter(adapter);

                    MyApplication.appHandler.sendEmptyMessage(Constant.HandlerFlag.CLOSE_LEFT_MENU);

                    OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT, (MainActivity) getActivity());

                    break;
            }
        }
    };


    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_tab_course, null);
        viewHolder = new ViewHolder(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        //((MainActivity) activityCtx).setSelectedFragment(this);
        getData();
//        if ((data == null || data.getStatus() != 200 || data.getCourseList().size() == 0) && !isLoading && NetUtil.isNetworkActive(activityCtx)) {
//
//        }
        super.onResume();
    }

    @Override
    public void initFragmentData() {
        //getData();
    }

    private void getData() {
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
                LogUtil.i(LOGTAG.FRAG_COURSE_LIST, "获取视频课程列表数据");

                try {
                    data = DataRequestUtil.sp_getAllCourseWithBuyStatus(MyApplication.getAccountInfo().getTokenID());
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_COURSE_LIST, "获取视频课程列表数据异常");
                    e.printStackTrace();
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);
            }
        }).start();
    }


    @Override
    public void onPause() {

        if (data != null && data.getStatus() == 200) {
//            new DBUtil(activityCtx).saveReviewBookChapterList(data);
            RefreshHolder.COURSE_LIST = RefreshHolder.REFRESH_NET;
        }


        super.onPause();
    }


    static class ViewHolder {
        RelativeLayout tipRL;
        ProgressBar progressBar;
        TextView tipTV;
        RelativeLayout courseRootRL;
        ListView courseListLV;

        View[] showGroup;

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
            courseListLV = (ListView) view.findViewById(R.id.courseList_lv);

            showGroup = new View[]{progressBar, tipTV, courseListLV};
        }
    }
}


//        List<Map<String, Object>> lisItems = new ArrayList<Map<String, Object>>();
//
//        for (int i = 0; i < name.length; i++) {
//            Map<String, Object> listem = new HashMap<String, Object>();
//            listem.put("head", imageids[i]);
//            listem.put("name", name[i]);
//            listem.put("desc", desc[i]);
//            listems.add(listem);
//        }
//
//        /*SimpleAdapter的参数说明
//         * 第一个参数 表示访问整个android应用程序接口，基本上所有的组件都需要
//         * 第二个参数表示生成一个Map(String ,Object)列表选项list
//         * 第三个参数表示界面布局的id  表示该文件作为列表项的组件int
//         * 第四个参数表示该Map对象的哪些key对应value来生成列表项int[]
//         * 第五个参数表示来填充的组件 Map对象key对应的资源一依次填充组件 顺序有对应关系String[]
//         * 注意的是map对象可以key可以找不到 但组件的必须要有资源填充  因为 找不到key也会返回null 其实就相当于给了一个null资源
//         * 下面的程序中如果 new String[] { "name", "head", "desc","name" } new int[] {R.id.name,R.id.head,R.id.desc,R.id.head}
//         * 这个head的组件会被name资源覆盖
//         * */
//        SimpleAdapter simplead = new SimpleAdapter(this, lisItems,
//                R.layout.simple_item, new String[] { "name", "head", "desc" },
//                new int[] {R.id.name,R.id.head,R.id.desc});
//
//        lt1=(ListView)findViewById(R.id.lt1);
//        lt1.setAdapter(simplead);
package com.hengdian.henghua.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.utils.Constant;

/**
 * Created by Anderok on 2017/1/6.
 */
public class TestFragment extends BackHandledFragment implements View.OnClickListener {
    public ViewHolder viewHolder;

    @Override
    public void initFragmentData() {
        //
    }

    @Override
    public boolean onBackPressed() {

        //如果不是在线测试页，忽略返回动作处理
        if (((MainActivity) activityCtx).curTabIndex != 1) {
            return false;
        }

        LogUtil.i(LOGTAG.FRAG_TEST_MODE, "在线测试Frag执行返回");

        //按了返回，应该将选中设为父Fragment,没有或不需要控制则null
        ((MainActivity) activityCtx).setSelectedFragment(null);

        return false;
    }

    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = View.inflate(activityCtx, R.layout.fr_tab_test, null);

        viewHolder = new ViewHolder(getActivity());
        initActionListener();

        //判断是否有章节教材列表子Fragment则隐藏模式选择视图
        if (getChildFragment(Constant.FragTag.TEST_EXERCISE_CHAPTER) != null
                || getChildFragment(Constant.FragTag.TEST_BREAKTHROUGH) != null
                || getChildFragment(Constant.FragTag.TEST_EXAM) != null) {
            showTestModeOption(false);
        } else {
            showTestModeOption(true);
        }
        return rootView;
    }

    private Fragment getChildFragment(String fragmentTag) {
        return getChildFragmentManager().findFragmentByTag(fragmentTag);
    }

    private void initActionListener() {
        viewHolder.exerciseIV.setOnClickListener(this);
        viewHolder.breakthroughIV.setOnClickListener(this);
        viewHolder.examIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        TestFragment testFragment = (TestFragment) getFragmentManager().findFragmentByTag(((MainActivity) activityCtx).tabText[1]);
        FragmentManager childFragManager = testFragment.getChildFragmentManager();
        FragmentTransaction fragTrans = childFragManager.beginTransaction();
        //fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit); //fragment动画

        //隐藏测试模式选择界面
        showTestModeOption(false);

        switch (view.getId()) {
            case R.id.exercise_iv:

                LogUtil.i(LOGTAG.FRAG_TEST_MODE, "显示练习模式");

                ((MainActivity) activityCtx).viewHolder.titleBarTextTV.setText(R.string.lx);
                MainActivity.curTestChildFragTag = Constant.FragTag.TEST_EXERCISE_CHAPTER;

                ExerciseListFragment exerciseListFrag = (ExerciseListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_EXERCISE_CHAPTER);
                //如果没有练习模式子fragment,则创建
                if (exerciseListFrag == null) {
                    exerciseListFrag = new ExerciseListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.Jump.FROM_TAG, Constant.FragTag.TEST_EXERCISE_CHAPTER);
                    exerciseListFrag.setArguments(bundle);

                    fragTrans.add(R.id.testContainer_fl, exerciseListFrag, Constant.FragTag.TEST_EXERCISE_CHAPTER);
                    //fragTrans.addToBackStack(Constant.FragTag.TEST_EXERCISE_CHAPTER);
                } else {
                    fragTrans.show(exerciseListFrag);

                    if (exerciseListFrag.getDataOfFragment() == null || exerciseListFrag.getDataOfFragment().getStatus() != 200) {
                        exerciseListFrag.getData();
                    }
                }

                ((MainActivity) activityCtx).setSelectedFragment(exerciseListFrag);

                break;
            case R.id.breakthrough_iv:

                LogUtil.i(LOGTAG.FRAG_TEST_MODE, "显示闯关模式");

                ((MainActivity) activityCtx).viewHolder.titleBarTextTV.setText(R.string.cg);
                MainActivity.curTestChildFragTag = Constant.FragTag.TEST_BREAKTHROUGH;

                BreakthroughListFragment breakthroughListFrag = (BreakthroughListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_BREAKTHROUGH);
                //如果没有练习模式子fragment,则创建
                if (breakthroughListFrag == null) {
                    breakthroughListFrag = new BreakthroughListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.Jump.FROM_TAG, Constant.FragTag.TEST_BREAKTHROUGH);
                    breakthroughListFrag.setArguments(bundle);

                    fragTrans.add(R.id.testContainer_fl, breakthroughListFrag, Constant.FragTag.TEST_BREAKTHROUGH);
                    //fragTrans.addToBackStack(Constant.FragTag.TEST_BREAKTHROUGH);
                } else {
                    fragTrans.show(breakthroughListFrag);

                    if (breakthroughListFrag.getDataOfFragment() == null || breakthroughListFrag.getDataOfFragment().getStatus() != 200) {
                        breakthroughListFrag.getData();
                    }
                }

                ((MainActivity) activityCtx).setSelectedFragment(breakthroughListFrag);

                break;
            case R.id.exam_iv:

                LogUtil.i(LOGTAG.FRAG_TEST_MODE, "显示考试模式");
                ((MainActivity) activityCtx).viewHolder.titleBarTextTV.setText(R.string.ks);
                MainActivity.curTestChildFragTag = Constant.FragTag.TEST_EXAM;

                ExamListFragment examListFrag = (ExamListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_EXAM);
                //如果没有练习模式子fragment,则创建
                if (examListFrag == null) {
                    examListFrag = new ExamListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.Jump.FROM_TAG, Constant.FragTag.TEST_EXAM);
                    examListFrag.setArguments(bundle);

                    fragTrans.add(R.id.testContainer_fl, examListFrag, Constant.FragTag.TEST_EXAM);
                    //fragTrans.addToBackStack(Constant.FragTag.TEST_EXAM);
                } else {
                    fragTrans.show(examListFrag);

                    if (examListFrag.getDataOfFragment() == null || examListFrag.getDataOfFragment().getStatus() != 200) {
                        examListFrag.getData();
                    }
                }

                ((MainActivity) activityCtx).setSelectedFragment(examListFrag);

                break;
        }

        /*
        FragmentTransaction只能在activity存储它的状态（当用户要离开activity时）之前调用commit()，
        如果在存储状态之后调用commit()，将会抛出一个异常。
        这是因为当activity再次被恢复时commit之后的状态将丢失。
        如果丢失也没关系，那么使用commitAllowingStateLoss()方法。
        */

//        fragTrans.commit();
        fragTrans.commitAllowingStateLoss();
    }


    /**
     * 显示或隐藏 测试模式 选项界面
     *
     * @param show
     */
    public void showTestModeOption(boolean show) {
        MainActivity.isRootFrag = show;
        if (show) {
            ((MainActivity) activityCtx).setRootTopBar(true);
            MainActivity.curTestChildFragTag = "";
            viewHolder.modeOptionsLL.setVisibility(View.VISIBLE);
            viewHolder.testContainerFL.setVisibility(View.INVISIBLE);
        } else {
            ((MainActivity) activityCtx).setRootTopBar(false);

            viewHolder.modeOptionsLL.setVisibility(View.INVISIBLE);
            viewHolder.testContainerFL.setVisibility(View.VISIBLE);

        }
    }


    public class ViewHolder {
        RelativeLayout frTestRL;
        //列表容器
        FrameLayout testContainerFL;

        LinearLayout modeOptionsLL;
        ImageView exerciseIV;
        ImageView breakthroughIV;
        ImageView examIV;

        ViewHolder(Activity activity) {
            findViews(activity);
        }

        /**
         * 初始化布局控件,比注解节省系统开销
         */
        private void findViews(Activity activity) {
            frTestRL = (RelativeLayout) rootView.findViewById(R.id.fr_test_rl);

            testContainerFL = (FrameLayout) rootView.findViewById(R.id.testContainer_fl);

            modeOptionsLL = (LinearLayout) rootView.findViewById(R.id.modeOptions_ll);
            exerciseIV = (ImageView) rootView.findViewById(R.id.exercise_iv);
            breakthroughIV = (ImageView) rootView.findViewById(R.id.breakthrough_iv);
            examIV = (ImageView) rootView.findViewById(R.id.exam_iv);
        }
    }
}

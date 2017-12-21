package com.hengdian.henghua.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hengdian.henghua.R;
import com.hengdian.henghua.androidUtil.ActivityViewHolder;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.UIUtil;
import com.hengdian.henghua.fragment.BackHandledFragment;
import com.hengdian.henghua.fragment.ExamContentFragment;
import com.hengdian.henghua.fragment.ExerciseContentFragment;
import com.hengdian.henghua.fragment.ReviewContentFragment;
import com.hengdian.henghua.fragment.StageContentFragment;
import com.hengdian.henghua.utils.Constant;

/**
 * Created by admin on 2017-01-08.
 */
public class ContentActivity extends FragmentActivity implements BackHandledFragment.BackPressedHandler {

    public ViewHolder viewHolder;
    private Bundle bundle;
    public Context ctx;
    private final String TAG = "ContentActivity";

    public String fromTag = "";//来源标志
    public String chapterID = ""; //章节ID
    public String chapterName = "";
    public String bookID = ""; //教材ID
    public String bookName = "";

    /**
     * 获取传过来的标志
     */

    private void initJumpIndex(Bundle bundle) {
        fromTag = bundle.getString(Constant.Jump.FROM_TAG);
        chapterID = bundle.getString(Constant.Jump.CHAPTER_ID);
        chapterName = bundle.getString(Constant.Jump.CHAPTER_NAME);
        bookID = bundle.getString(Constant.Jump.BOOK_ID);
        bookName = bundle.getString(Constant.Jump.BOOK_NAME);

        LogUtil.i(TAG, "fromTag：" + fromTag
                + ",bookID：" + bookID + "，BookName：" + bookName +
                "，chapterID:" + chapterID + ",chapterName:" + chapterName);
    }


    /**
     * 子fragment 监听返回键按下
     */
    private BackHandledFragment mBackHandedFragment;

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {

        //fragment 为空 或者 fragment onBack 返回false
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();

            } else {
                //oast.makeText(this, "弹窗提示是否退出", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().popBackStack();
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        viewHolder = new ViewHolder(this,false);
        ctx = getApplicationContext();

        UIUtil.initImmersiveTitleBar(this, viewHolder.statusBarLO);

        bundle = getIntent().getExtras();

        initJumpIndex(bundle);

        viewHolder = new ViewHolder(this,false);

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (fromTag) {
            case Constant.FragTag.REVIEW_CHAPTER:
                replaceToReviewContentFrag(fragmentManager);
                break;

            case Constant.FragTag.TEST_EXERCISE_CHAPTER:
                replaceToExerciseContentFrag(fragmentManager);
                break;

            case Constant.FragTag.TEST_STAGE:
                replaceToStageContentFrag(fragmentManager);
                break;

            case Constant.FragTag.TEST_EXAM:
                replaceToExamContentFrag(fragmentManager);
                break;

        }
    }

    /**
     * 使用练习模式做题页面
     *
     * @param fragmentManager
     */
    private void replaceToExamContentFrag(FragmentManager fragmentManager) {
        ExamContentFragment examContentFragment = new ExamContentFragment();
        examContentFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fl, examContentFragment, Constant.FragTag.TEST_EXAM_CONTENT)
                .commit();

        this.setSelectedFragment(examContentFragment);
    }


    /**
     * 使用闯关模式做题页面
     *
     * @param fragmentManager
     */
    private void replaceToStageContentFrag(FragmentManager fragmentManager) {
        StageContentFragment stageContentFragment = new StageContentFragment();
        stageContentFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(R.id.container_fl, stageContentFragment, Constant.FragTag.TEST_STAGE_CONTENT)
                .commit();
    }


    /**
     * 使用练习模式做题页面
     *
     * @param fragmentManager
     */
    private void replaceToExerciseContentFrag(FragmentManager fragmentManager) {
        ExerciseContentFragment exerciseContentFragment = new ExerciseContentFragment();
        exerciseContentFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fl, exerciseContentFragment, Constant.FragTag.TEST_EXERCISE_CONTENT)
                .commit();
    }

    /**
     * 使用知识重温阅读页面
     *
     * @param fragmentManager
     */
    private void replaceToReviewContentFrag(FragmentManager fragmentManager) {
        ReviewContentFragment reviewContentFragment = new ReviewContentFragment();
        reviewContentFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fl, reviewContentFragment, Constant.FragTag.REVIEW_CONTENT)
                .commit();
        viewHolder.titleBarTextTV.setText(R.string.cw);
    }


    public static class ViewHolder extends ActivityViewHolder {
        //顶部控件
        protected LinearLayout topBarLO;
        public LinearLayout statusBarLO;
        public RelativeLayout titleBarRO;
        public ImageView usrIV;
        public ImageView backIV;
        public TextView titleBarTextTV;
        public TextView commitTV;

        public LinearLayout questionTypeChooserLO;
        public TextView singleChoiceTV;
        public TextView multipleChoiceTV;
        public TextView trueFalseTV;

        public FrameLayout containerFL;

        ViewHolder(Activity activity,boolean refind) {
           super(activity,refind);
        }

        public void findViews() {
            topBarLO = $(R.id.top_bar_lo);
            statusBarLO = $(R.id.status_bar_lo);
            titleBarRO = $(R.id.title_bar_ro);

            usrIV = $(R.id.titleBarUsr_iv);
            backIV = $(R.id.titleBarBack_iv);
            titleBarTextTV = $(R.id.titleBarText_tv);
            commitTV = $(R.id.commit_tv);

            questionTypeChooserLO = $(R.id.titleBar_questionType_chooser_lo);
            singleChoiceTV = $(R.id.single_choice_tv);
            multipleChoiceTV = $(R.id.multiple_choice_tv);
            trueFalseTV = $(R.id.true_false_tv);

            containerFL = $(R.id.container_fl);

            //隐藏头像，显示返回按钮
            usrIV.setVisibility(View.GONE);
            backIV.setVisibility(View.VISIBLE);
        }
    }
}
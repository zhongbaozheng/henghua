package com.hengdian.henghua.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.adapter.ExerciseNavCardLvAdapter;
//import com.hengdian.henghua.adapter.ExercisePagerScrollAdapter;
import com.hengdian.henghua.adapter.ReviewNavCardLvAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.model.TestResult;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.model.contentDataModel.Rs_Questions_GroupByType;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.GadgetUtil;
import com.hengdian.henghua.utils.RefreshHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static com.hengdian.henghua.utils.GadgetUtil.countResult;

/**
 * 试题界面
 */
public class ExerciseContentFragment extends BaseFragment implements View.OnClickListener {
    ContentActivity activity;
    public ViewHolder viewHolder;

    //答题卡显示状态,true=已打开
    public boolean isNavCardShowed = false;

    private LinearLayout[] mOptionLL;
    private TextView[] mOptionTV;
    private String[] mAnswers; //答案选中项

    int mOptionLlLength;

    private Rs_Questions_GroupByType data;

    public int sizeSingle = 0;
    public int sizeMultiple = 0;
    public int sizeTrueFalse = 0;

    //三种封装的view
    public List<View> mSingleListView = new ArrayList<>();
    public List<View> mMutiplListView = new ArrayList<>();
    public List<View> mTrueOrFalseListView = new ArrayList<>();

//    public ExercisePagerScrollAdapter mAdapter;


    //记录当前题号
    public int curIndexSingle = 0;
    public int curIndexMultiple = 0;
    public int curIndexTrueFalse = 0;

    private Question curQuestion;
    public int curQuestionType = Question.TYPE_SINGLE;//当前题型，0单选，1多选，2判断

    int optionIndex = 0; //选项开始位置
    int optionLength = 0;//选项长度

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ContentActivity) getActivity();
    }


    /**
     * 设置状态栏
     */
    private void initTopBar() {

        activity.viewHolder.usrIV.setVisibility(View.INVISIBLE);
        activity.viewHolder.backIV.setVisibility(View.VISIBLE);


        activity.viewHolder.titleBarTextTV.setVisibility(View.INVISIBLE);
        activity.viewHolder.commitTV.setText("更新");
        activity.viewHolder.commitTV.setVisibility(View.VISIBLE);
        activity.viewHolder.commitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToReFresh();
            }
        });
        activity.viewHolder.questionTypeChooserLO.setVisibility(View.VISIBLE);

        //显示标题栏组件
        activity.viewHolder.singleChoiceTV.setOnClickListener(this);
        //默认选中 单选题
        activity.viewHolder.singleChoiceTV.setSelected(true);
        activity.viewHolder.multipleChoiceTV.setOnClickListener(this);
        activity.viewHolder.trueFalseTV.setOnClickListener(this);

    }

    /**
     * 设置底部按钮
     */
    private void initBottomButton() {
        viewHolder.navCardTitleTV.setVisibility(View.VISIBLE);

        viewHolder.centerButtonTV.setText("导航卡");
        viewHolder.centerButtonIV.setImageResource(R.drawable.select_test_nav_img);

        viewHolder.lastOneTV.setText("上一题");
        viewHolder.nextOneTV.setText("下一题");

        viewHolder.centerButtonLL.setOnClickListener(this);
        viewHolder.lastOneLL.setOnClickListener(this);
        viewHolder.nextOneLL.setOnClickListener(this);
    }

    public View initFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fr_test_content, null);

        if (viewHolder == null) {
            viewHolder = new ViewHolder(rootView);
        }

        activity.viewHolder.backIV.setOnClickListener(this);

        //选项布局
        mOptionLL = new LinearLayout[]{viewHolder.optionALL,
                viewHolder.optionBLL, viewHolder.optionCLL, viewHolder.optionDLL, viewHolder.optionELL
                , viewHolder.optionFLL, viewHolder.optionGLL, viewHolder.optionTrueLL, viewHolder.optionFalseLL};

        mOptionLlLength = mOptionLL.length;

        //选项文字
        mOptionTV = new TextView[]{viewHolder.optionATV, viewHolder.optionBTV,
                viewHolder.optionCTV, viewHolder.optionDTV, viewHolder.optionETV, viewHolder.optionFalseTV
                , viewHolder.optionGTV, viewHolder.optionTrueTV, viewHolder.optionFalseTV};

        initTopBar();
        initBottomButton();


        for (int i = 0; i < mOptionLL.length; i++) {
            mOptionLL[i].setOnClickListener(this);
        }

        viewHolder.ensureBtnTV.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void initFragmentData() {

        LogUtil.i(LOGTAG.FRAG_EXAM_CONTENT, "fromTag:" + fromTag + ",bookID:" + bookID + ",chapterID:" + chapterID);
        getData();

    }

    private TestResult getTestResult() {

        if (data == null) {
            Log.e("data","data is null");
            return new TestResult();
        }

        switch (curQuestionType) {

            case Question.TYPE_SINGLE:
                return countResult(data.getSingleChoiceQuestionList());

            case Question.TYPE_MULTIPLE:

                return countResult(data.getMultipleChoiceQuestionList());

//            case Question.TYPE_TRUE_FALSE:
            default:
                return countResult(data.getTrueOrFalseQuestionList());
        }
    }


    private boolean isAllDone() {
        if (data == null || sizeSingle + sizeMultiple + sizeTrueFalse == 0) {
            return true;
        }

        List<Question> single = data.getSingleChoiceQuestionList();
        List<Question> multiple = data.getMultipleChoiceQuestionList();
        List<Question> trueFalse = data.getTrueOrFalseQuestionList();


        for (Question question : single) {
            if (question.getState() < Question.STATE2_ANSWERED) {
                return false;
            }
        }

        for (Question question : multiple) {
            if (question.getState() < Question.STATE2_ANSWERED) {
                return false;
            }
        }

        for (Question question : trueFalse) {
            if (question.getState() < Question.STATE2_ANSWERED) {
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
        if (!isAllDone()) {
            msg = "还有习题没做完呢，";
        }

        msg += "更新后将清除本章节的做题进度和标记，是否继续更新？";

        AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
        builder.setTitle("更新提示")
                .setMessage(msg)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RefreshHolder.TEST_EXERCISE_CONTENT = RefreshHolder.REFRESH_NET;
                                getData();
                                dialog.cancel();
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


    Handler mHandler = new Handler() {
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

                        RefreshHolder.TEST_EXERCISE_CONTENT = RefreshHolder.REFRESH_NET;
                        return;

                    }

                    curQuestionType = data.getCurType();
                    curIndexSingle = data.getCurIndexSingle();
                    curIndexMultiple = data.getCurIndexMultiple();
                    curIndexTrueFalse = data.getCurIndexTrueOrFalse();

                    sizeSingle = data.getSingleChoiceQuestionList().size();
                    sizeMultiple = data.getMultipleChoiceQuestionList().size();
                    sizeTrueFalse = data.getTrueOrFalseQuestionList().size();

                    //组装数据和View
                    for(int i=0;i<data.getSingleChoiceQuestionList().size();i++){
                        View view = View.inflate(activityCtx,R.layout.execrise_scroll_pager,null);
                        mSingleListView.add(view);
                    }

                    for(int i=0;i<data.getMultipleChoiceQuestionList().size();i++){
                        View view = View.inflate(activityCtx,R.layout.execrise_scroll_pager,null);
                        mMutiplListView.add(view);
                    }
                    for(int i=0;i<data.getTrueOrFalseQuestionList().size();i++){
                        View view = View.inflate(activityCtx,R.layout.execrise_scroll_pager,null);
                        mTrueOrFalseListView.add(view);
                    }

//                    mAdapter = new ExercisePagerScrollAdapter(activityCtx,
//                            data.getSingleChoiceQuestionList(),mSingleListView,
//                            data.getMultipleChoiceQuestionList(),mMutiplListView,
//                            data.getTrueOrFalseQuestionList(),mTrueOrFalseListView,
//                            curQuestionType);
//
//                    viewHolder.mViewPager.setAdapter(mAdapter);
//
//                    viewHolder.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                        @Override
//                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                        }
//
//                        @Override
//                        public void onPageSelected(int position) {
//                            //设置当前
//                        }
//
//                        @Override
//                        public void onPageScrollStateChanged(int state) {
//
//                        }
//                    });

                    //设置题目
                    dealWithIndex();

                    break;
            }
        }
    };


    private void setTypeSwitcher(int type) {
        switch (type) {
            case Question.TYPE_SINGLE:
                activity.viewHolder.singleChoiceTV.setSelected(true);
                activity.viewHolder.multipleChoiceTV.setSelected(false);
                activity.viewHolder.trueFalseTV.setSelected(false);

                break;
            case Question.TYPE_MULTIPLE:
                activity.viewHolder.singleChoiceTV.setSelected(false);
                activity.viewHolder.multipleChoiceTV.setSelected(true);
                activity.viewHolder.trueFalseTV.setSelected(false);

                break;
            case Question.TYPE_TRUE_FALSE:
                activity.viewHolder.singleChoiceTV.setSelected(false);
                activity.viewHolder.multipleChoiceTV.setSelected(false);
                activity.viewHolder.trueFalseTV.setSelected(true);

                break;
        }
    }

    /**
     * 题号索引处理
     */
    public void dealWithIndex() {
        switch (curQuestionType) {
            case Question.TYPE_SINGLE:
                setContent(data.getSingleChoiceQuestionList(), curIndexSingle);
                break;
            case Question.TYPE_MULTIPLE:
                setContent(data.getMultipleChoiceQuestionList(), curIndexMultiple);
                break;
            case Question.TYPE_TRUE_FALSE:
                setContent(data.getTrueOrFalseQuestionList(), curIndexTrueFalse);
                break;
        }
    }


    /**
     * 设置题目内容显示
     *
     * @param listData
     * @param listIndex
     */
    private void setContent(List<Question> listData, int listIndex) {
        viewHolder.contentSV.fullScroll(ScrollView.FOCUS_UP);

        //无数据
        if (listData == null || listData.size() == 0) {

            viewHolder.tipTV.setText("没有内容...");
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);

            //设置标题栏选中状态
            setTypeSwitcher(curQuestionType);

            //隐藏题目内容布局
            viewHolder.itemNumTV.setText("");
            viewHolder.itemTitleTV.setText("");
            viewHolder.itemTitleTV.setText("");

            for (int i = 0; i < mOptionLL.length; i++) {
                mOptionLL[i].setVisibility(View.INVISIBLE);
                mOptionTV[i].setText("");
            }

//            viewHolder.ensureBtnTV.setVisibility(View.GONE);

            return;
        }

        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT);


        /**
         * 重置上一个题目为非选中
         */
        if (curQuestion != null) {
            curQuestion.isShow = false;
        }

        curQuestion = listData.get(listIndex);
        curQuestion.isShow = true;
        curQuestionType = curQuestion.getType();
        //设置标题栏题型选中状态
        setTypeSwitcher(curQuestionType);

//        viewHolder.mViewPager.setCurrentItem(listIndex); //设置当前页的viewPager 2017/12/20 15:43

        //题号
        String questionNum = GadgetUtil.formatItemNum(listIndex + 1); //题号跟脚标差别
        viewHolder.itemNumTV.setText(questionNum);
        viewHolder.itemTypeTV.setText(curQuestion.getQuestionTypeName());
        viewHolder.itemTitleTV.setText(curQuestion.getQuestionContent() + " (" + curQuestion.getScore() + "分)");
        viewHolder.titleModelRL.setVisibility(View.VISIBLE);

        String[] options = curQuestion.getOptions();
        //显示对应数量选项
        for (int i = 0; i < mOptionLL.length; i++) {
            //初始化
            mOptionLL[i].setSelected(false);

            if (curQuestionType == Question.TYPE_TRUE_FALSE) {
                optionIndex = mOptionLlLength - 2;
                optionLength = 2;

                //设置判断题选项
                if (i == mOptionLlLength - 2) {

                    //对,倒2
                    mOptionTV[i].setText(options[0]);
                    mOptionLL[i].setVisibility(View.VISIBLE);

                } else if (i == mOptionLL.length - 1) {
                    //倒1
                    mOptionTV[i].setText(options[1]);
                    mOptionLL[i].setVisibility(View.VISIBLE);

                } else {
                    //隐藏多余的
                    mOptionLL[i].setVisibility(View.GONE);
                }

            } else {//不是判断题
                optionIndex = 0;
                optionLength = options.length;

                if (i < options.length) {
                    mOptionTV[i].setText(options[i]);
                    mOptionLL[i].setVisibility(View.VISIBLE);

                } else {
                    //隐藏多余的
                    mOptionLL[i].setVisibility(View.GONE);
                }
            }
        }

        //设置选项的选中状态
        mAnswers = curQuestion.getSelected();

        for (int i = 0; i < mAnswers.length; i++) {
            if (!mAnswers[i].isEmpty()) {
                mOptionLL[i].setSelected(true);
            } else {
                mOptionLL[i].setSelected(false);
            }
        }

        if (curQuestion.showAnswer) {
            dealWithAnswerText(curQuestion, true);
        } else {
            dealWithAnswerText(curQuestion, false);
        }
    }


    private void getData() {

        if (isLoading) {
            return;
        }

        isLoading = true;

        if (viewHolder != null) {
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_PROGRESS_BAR);
        }

        sizeSingle = 0;
        sizeMultiple = 0;
        sizeTrueFalse = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {

                LogUtil.i(LOGTAG.FRAG_EXAM_CONTENT, "获取练习题数据");

                try {

                    if (data == null && RefreshHolder.TEST_EXERCISE_CONTENT == RefreshHolder.REFRESH_LOCAL) {
                        data = new DBUtil(activityCtx).getExerciseContent(bookID, chapterID);
                    }

                    if ((RefreshHolder.TEST_EXERCISE_CONTENT == RefreshHolder.REFRESH_NET || data == null || data.getStatus() != 200) && NetUtil.isNetworkActive(activityCtx)) {
                        data = DataRequestUtil.xx_getQuestionsGroupByType(chapterID, MyApplication.getAccountInfo().getTokenID());
                        RefreshHolder.TEST_EXERCISE_CONTENT = RefreshHolder.REFRESH_LOCAL;
                    }
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_EXAM_CONTENT, "获取练习题数据异常");
                    e.printStackTrace();
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);
            }
        }).start();
    }


    @Override
    public void onClick(View view) {

        if (exerciseNavCardLvAdapter != null) {
            exerciseNavCardLvAdapter.notifyDataSetChanged();
        }

        switch (view.getId()) {

            case R.id.lastOne_ll:  //上一题

                backForward();

                break;

            case R.id.nextOne_ll://下一题

                goForward();

                break;

            case R.id.centerButton_ll:

                if (data != null && sizeSingle + sizeMultiple + sizeTrueFalse > 0) {
                    switchNavCardShowHide();
                }

                break;

            case R.id.single_choice_tv://单选题

                curQuestionType = Question.TYPE_SINGLE;   //这里的确传入了type,2017/12/20 15:17
                dealWithIndex();

                break;
            case R.id.multiple_choice_tv://多选题
                curQuestionType = Question.TYPE_MULTIPLE;
                dealWithIndex();

                break;
            case R.id.true_false_tv: //判断题

                curQuestionType = Question.TYPE_TRUE_FALSE;
                dealWithIndex();

                break;
            case R.id.titleBarBack_iv:
                //点击了返回键,返回，刷新请求
//                Intent intent = new Intent();
//                intent.putExtra("result", "refresh");
//
//                ((ContentActivity) activityCtx).setResult(2, intent);
                ((ContentActivity) activityCtx).finish();
                break;

            default:
                //按下的不是以上按钮,则按下了内容页的按钮
                dealWithOptions(view);
        }

    }


//    TranslateAnimation tAnimGoForward;//横向位移400个单位
//    TranslateAnimation tAnimBackForward;
//    TranslateAnimation tAnimReset;
//    TranslateAnimation
//            mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//
//    TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//            -1.0f);
//
//    private void doGoForwardAnim(View view) {
//        if (tAnimGoForward == null) {
//            tAnimGoForward = new TranslateAnimation(200, 0, 0, 0);
//            tAnimGoForward.setDuration(200);
//            tAnimGoForward.setInterpolator(new AccelerateDecelerateInterpolator());
//        }
//
//        view.startAnimation(tAnimGoForward);
//    }
//
//    private void doBackForwardAnim(View view) {
//        if (tAnimBackForward == null) {
//            tAnimBackForward = new TranslateAnimation(-200, 0, 0, 0);
//            tAnimBackForward.setDuration(200);
//            tAnimBackForward.setInterpolator(new AccelerateDecelerateInterpolator());
//        }
//
//        view.startAnimation(tAnimBackForward);
//    }
//
//
//    private void deReSetAnim(View view) {
//        if (tAnimReset == null) {
//            tAnimReset = new TranslateAnimation(0, 0, 0, 0);
//            tAnimReset.setDuration(0);
//            tAnimReset.setInterpolator(new AccelerateDecelerateInterpolator());
//        }
//
//        view.startAnimation(tAnimReset);
//    }
//
//

    /**
     * 上一题
     */
    private void backForward() {

        switch (curQuestionType) {
            case Question.TYPE_SINGLE:
                if (curIndexSingle < 1) {
                    ToastUtil.toastMsgShort("已到开头");
                } else {
                    curIndexSingle--;
                    //doBackForwardAnim(viewHolder.contentLL);
                    dealWithIndex();
                }

                break;
            case Question.TYPE_MULTIPLE:
                if (curIndexMultiple < 1) {
                    ToastUtil.toastMsgShort("已到开头");
                } else {
                    curIndexMultiple--;
                    //doBackForwardAnim(viewHolder.contentLL);
                    dealWithIndex();
                }

            case Question.TYPE_TRUE_FALSE:
                if (curIndexTrueFalse < 1) {
                    ToastUtil.toastMsgShort("已到开头");
                } else {
                    curIndexTrueFalse--;
                    //doBackForwardAnim(viewHolder.contentLL);
                    dealWithIndex();
                }
                break;
        }
    }


    /**
     * 切换到下一题
     */
    private void goForward() {

        switch (curQuestionType) {
            case Question.TYPE_SINGLE:
                if (curIndexSingle >= sizeSingle - 1) {
                    ToastUtil.toastMsgShort("已到结尾");
                } else {
                    curIndexSingle++;
                    //doGoForwardAnim(viewHolder.contentLL);
                    dealWithIndex();
                }

                break;
            case Question.TYPE_MULTIPLE:
                if (curIndexMultiple >= sizeMultiple - 1) {
                    ToastUtil.toastMsgShort("已到结尾");
                } else {
                    curIndexMultiple++;
//                    doGoForwardAnim(viewHolder.contentLL);
                    dealWithIndex();
                }

                break;
            case Question.TYPE_TRUE_FALSE:
                if (curIndexTrueFalse >= sizeTrueFalse - 1) {
                    ToastUtil.toastMsgShort("已到结尾");
                } else {
                    curIndexTrueFalse++;
                    //doGoForwardAnim(viewHolder.contentLL);
                    dealWithIndex();
                }

                break;
        }


    }


    /**
     * 选项处理
     *
     * @param view
     */
    private void dealWithOptions(View view) {

        switch (view.getId()) {

            case R.id.ensureBtn_tv: //确定

                checkAndShowAnswer(curQuestion, true);

                break;

            default:

                dealWithSelection(view);
        }
    }

    /**
     * 选项处理，并保存
     *
     * @param view
     */
    private void dealWithSelection(View view) {
        //如果是单选，把非点中项设为不选中
        int childCount = viewHolder.optionsLL.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View optionView = viewHolder.optionsLL.getChildAt(i);

            //如果当前没被选中，且不是多选，设为不选中
            if (view.getId() != optionView.getId() && curQuestionType != Question.TYPE_MULTIPLE) {
                viewHolder.optionsLL.getChildAt(i).setSelected(false);
                mAnswers[i] = "";
                //否则，被点击项状态反转
            } else if (view.getId() == optionView.getId()) {
                boolean isSelected = optionView.isSelected();
                optionView.setSelected(!isSelected);

                if (!isSelected) {
                    mAnswers[i] = GadgetUtil.getOptionChar(i);
                } else {
                    mAnswers[i] = "";
                }
            }
        }

        if (curQuestion.getType() == Question.TYPE_MULTIPLE) {
            dealWithAnswerText(curQuestion, false);
        } else {
            checkAndShowAnswer(curQuestion, true);
        }
    }

    /**
     * 校验答案，并控制确定按钮、答案状态按钮及答案解析  隐藏或显示
     *
     * @param question
     * @param showAnswer 是否显示答案
     */
    private void checkAndShowAnswer(Question question, boolean showAnswer) {

        if (question.selectedToString().length() == 0) {
            dealWithAnswerText(question, false);
            Toast.makeText(activityCtx, "请选择", Toast.LENGTH_SHORT).show();
            return;
        }

        if (question.selectedToString().equalsIgnoreCase(question.getAnswer())) {
            question.showAnswer = true;
            question.setState(Question.STATE3_RIGHT);
            //答对了，自动跳转到下一题
            dealWithAnswerText(question, showAnswer);
            //自动跳到下一题
            //goForward();
        } else {
            curQuestion.setState(Question.STATE3_WRONG);
            dealWithAnswerText(question, showAnswer);
        }

        if (showAnswer) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewHolder.contentSV.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 200);
        }
    }

    /**
     * 答案解析显示控制
     *
     * @param question
     * @param showAnswer
     */
    private void dealWithAnswerText(Question question, boolean showAnswer) {

        question.showAnswer = showAnswer;

        if (showAnswer) {
            viewHolder.ensureBtnTV.setVisibility(View.GONE);

            if (question.getState() == Question.STATE3_RIGHT) {
                viewHolder.answerBtnTV.setBackground(activityCtx.getResources().getDrawable(R.drawable.answer_rigth));
            } else if (question.getState() == Question.STATE3_WRONG) {
                viewHolder.answerBtnTV.setBackground(activityCtx.getResources().getDrawable(R.drawable.answer_error));
            }

            viewHolder.answerBtnTV.setVisibility(View.VISIBLE);
            viewHolder.answerLL.setVisibility(View.VISIBLE);
            viewHolder.answerTV.setText("参考答案：" + question.getAnswer());
            viewHolder.explainTV.setText("题目解析：" + (question.getExplain().isEmpty() ? "略。" : question.getExplain()));

        } else {
            //多选题，不显示答案就显示确定按钮
            if (question.getType() == Question.TYPE_MULTIPLE) {
                viewHolder.ensureBtnTV.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ensureBtnTV.setVisibility(View.GONE);
            }

            viewHolder.answerLL.setVisibility(View.GONE);
            viewHolder.answerTV.setText("");
            viewHolder.explainTV.setText("");
        }
    }


    /**
     * 切换答题卡显示或隐藏
     */
    public void switchNavCardShowHide() {
        //如果导航卡已经显示,则隐藏
        showNavCard(!isNavCardShowed);
    }

    ExerciseNavCardLvAdapter exerciseNavCardLvAdapter;

    /**
     * 显示或隐藏答题卡
     */
    public void showNavCard(boolean show) {
        if (show) {

//            SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
//            String name=preferences.getString("name", "defaultname");
//            String age=preferences.getString("age", "0");
            TestResult rs = getTestResult();
            viewHolder.navCardTitleTV.setText("");
//            viewHolder.navCardTitleTV.setVisibility(View.GONE);
            viewHolder.navCardTitleTV.setText(
                    "答对" + rs.answerRight + "题，答错"
                            + rs.answerWrong + "题，剩余"
                            + rs.remain + "题，得分:"
                            + rs.scoreGet + "/" + rs.scoreTotal);
            if(rs != null){

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("answerRight",rs.answerRight+"");
                editor.putString("answerWrong",rs.answerWrong+"");
                editor.putString("remain",rs.remain+"");
                editor.putString("scoreGet",rs.scoreGet+"");
                editor.putString("scoreTotal",rs.scoreTotal+"");
                editor.commit();
            }

            viewHolder.centerButtonLL.setSelected(true);
            viewHolder.navCardLL.setVisibility(View.VISIBLE);


            exerciseNavCardLvAdapter = new ExerciseNavCardLvAdapter(
                    activityCtx, ExerciseContentFragment.this,
                    data.getSingleChoiceQuestionList(),
                    data.getMultipleChoiceQuestionList(),
                    data.getTrueOrFalseQuestionList());


//            this.setListViewHeightBasedOnChildren(viewHolder.navCardLV); //同样，这行代码也去掉
            viewHolder.navCardLV.setAdapter(exerciseNavCardLvAdapter);
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


    @Override
    public void onResume() {
        //((ContentActivity) activityCtx).setSelectedFragment(this);

        if ((data == null || data.getStatus() != 200 ||
                (data.getSingleChoiceQuestionList().size() == 0 && data.getMultipleChoiceQuestionList().size() == 0 && data.getTrueOrFalseQuestionList().size() == 0))
                && !isLoading) {
            RefreshHolder.TEST_EXERCISE_CONTENT = RefreshHolder.REFRESH_NET;
            getData();
        }

        super.onResume();
    }

    //暂停时存储状态
    @Override
    public void onPause() {

        if (data != null && data.getStatus() == 200) {
//            data.setCurIndex(curIndex1);
            data.setCurType(curQuestionType);
            data.setCurIndexSingle(curIndexSingle);
            data.setCurIndexMultiple(curIndexMultiple);
            data.setCurIndexTrueOrFalse(curIndexTrueFalse);

            for (Question question : data.getSingleChoiceQuestionList()) {
                //按照用户的逻辑来看，做过的题应该展示出来,做错的不要显示出来，做对的可以显示出来 bug2解决办法
                if(question.getState() == Question.STATE3_RIGHT){
                    question.setShowAnswer(true);
                }else {
                    question.setShowAnswer(false);
                    if(question.getState() == Question.STATE3_WRONG){
                        question.clearSelected();
                    }
                }

            }

            for (Question question : data.getMultipleChoiceQuestionList()) {
                if(question.getState() == Question.STATE3_RIGHT){
                    question.setShowAnswer(true);
                }else {
                    question.setShowAnswer(false);
                    if(question.getState() == Question.STATE3_WRONG){
                        question.clearSelected();
                    }
                }
//                question.clearSelected();
//                question.setShowAnswer(false);
            }

            for (Question question : data.getTrueOrFalseQuestionList()) {
                if(question.getState() == Question.STATE3_RIGHT){
                    question.setShowAnswer(true);
                }else {
                    question.setShowAnswer(false);
                    if(question.getState() == Question.STATE3_WRONG){
                        question.clearSelected();
                    }
                }
//                question.clearSelected();
//                question.setShowAnswer(false);
            }

            new DBUtil(activityCtx).saveExerciseContent(bookID, chapterID, data);

            //提醒本地刷新知识重温列表
            RefreshHolder.TEST_EXERCISE_LIST = RefreshHolder.REFRESH_LOCAL;
        }


        super.onPause();
    }


    public class ViewHolder {
        // RelativeLayout tipRL;
        TextView tipTV;
        ProgressBar progressBar;

        public ViewPager mViewPager;

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

        RelativeLayout titleModelRL;
        TextView itemNumTV;//题号
        TextView itemTitleTV;//题目内容
        TextView itemTypeTV;//题型

        LinearLayout optionsLL;

        ImageView optionAIV;
        TextView optionATV;
        LinearLayout optionALL;

        ImageView optionBIV;
        TextView optionBTV;
        LinearLayout optionBLL;

        ImageView optionCIV;
        TextView optionCTV;
        LinearLayout optionCLL;

        ImageView optionDIV;
        TextView optionDTV;
        LinearLayout optionDLL;

        ImageView optionEIV;
        TextView optionETV;
        LinearLayout optionELL;

        ImageView optionFIV;
        TextView optionFTV;
        LinearLayout optionFLL;

        ImageView optionGIV;
        TextView optionGTV;
        LinearLayout optionGLL;


        ImageView optionTrueIV;
        TextView optionTrueTV;
        LinearLayout optionTrueLL;

        ImageView optionFalseIV;
        TextView optionFalseTV;
        LinearLayout optionFalseLL;

        TextView ensureBtnTV;

        LinearLayout answerLL;
        TextView answerBtnTV;
        TextView answerTV;
        TextView explainTV;

        LinearLayout navCardLL;
        TextView navCardTitleTV;
        ListView navCardLV;

        TextView flapperTV;

        View[] showGroup;

        ViewHolder(View view) {
            findViews(view);
        }

        /**
         * 初始化布局控件,比注解节省系统开销
         */
        private void findViews(View view) {
            mViewPager = (ViewPager)view.findViewById(R.id.viewPager);
            lastOneLL = (LinearLayout) view.findViewById(R.id.lastOne_ll);
            centerButtonLL = (LinearLayout) view.findViewById(R.id.centerButton_ll);
            nextOneLL = (LinearLayout) view.findViewById(R.id.nextOne_ll);

            lastOneIV = (ImageView) view.findViewById(R.id.lastOne_iv);
            centerButtonIV = (ImageView) view.findViewById(R.id.centerButton_iv);
            nextOneIV = (ImageView) view.findViewById(R.id.nextOne_iv);

            lastOneTV = (TextView) view.findViewById(R.id.lastOne_tv);
            centerButtonTV = (TextView) view.findViewById(R.id.centerButton_tv);
            nextOneTV = (TextView) view.findViewById(R.id.nextOne_tv);

            // tipRL = (RelativeLayout) view.findViewById(R.id.tip_rl);
            tipTV = (TextView) view.findViewById(R.id.tip_tv);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            contentLL = (LinearLayout) view.findViewById(R.id.content_ll);
            contentSV = (ScrollView) view.findViewById(R.id.content_sv);

            titleModelRL = (RelativeLayout) view.findViewById(R.id.titleModel_rl);
            itemNumTV = (TextView) view.findViewById(R.id.itemNum_tv);
            itemTitleTV = (TextView) view.findViewById(R.id.itemTitle_tv);
            itemTypeTV = (TextView) view.findViewById(R.id.itemType_tv);

            optionsLL = (LinearLayout) view.findViewById(R.id.options_ll);

            optionAIV = (ImageView) view.findViewById(R.id.optionA_iv);
            optionATV = (TextView) view.findViewById(R.id.optionA_tv);
            optionALL = (LinearLayout) view.findViewById(R.id.optionA_ll);

            optionBIV = (ImageView) view.findViewById(R.id.optionB_iv);
            optionBTV = (TextView) view.findViewById(R.id.optionB_tv);
            optionBLL = (LinearLayout) view.findViewById(R.id.optionB_ll);

            optionCIV = (ImageView) view.findViewById(R.id.optionC_iv);
            optionCTV = (TextView) view.findViewById(R.id.optionC_tv);
            optionCLL = (LinearLayout) view.findViewById(R.id.optionC_ll);

            optionDIV = (ImageView) view.findViewById(R.id.optionD_iv);
            optionDTV = (TextView) view.findViewById(R.id.optionD_tv);
            optionDLL = (LinearLayout) view.findViewById(R.id.optionD_ll);

            optionEIV = (ImageView) view.findViewById(R.id.optionE_iv);
            optionETV = (TextView) view.findViewById(R.id.optionE_tv);
            optionELL = (LinearLayout) view.findViewById(R.id.optionE_ll);

            optionFIV = (ImageView) view.findViewById(R.id.optionF_iv);
            optionFTV = (TextView) view.findViewById(R.id.optionF_tv);
            optionFLL = (LinearLayout) view.findViewById(R.id.optionF_ll);

            optionGIV = (ImageView) view.findViewById(R.id.optionG_iv);
            optionGTV = (TextView) view.findViewById(R.id.optionG_tv);
            optionGLL = (LinearLayout) view.findViewById(R.id.optionG_ll);

            optionTrueIV = (ImageView) view.findViewById(R.id.optionTrue_iv);
            optionTrueTV = (TextView) view.findViewById(R.id.optionTrue_tv);
            optionTrueLL = (LinearLayout) view.findViewById(R.id.optionTrue_ll);

            optionFalseIV = (ImageView) view.findViewById(R.id.optionFalse_iv);
            optionFalseTV = (TextView) view.findViewById(R.id.optionFalse_tv);
            optionFalseLL = (LinearLayout) view.findViewById(R.id.optionFalse_ll);

            ensureBtnTV = (TextView) view.findViewById(R.id.ensureBtn_tv);
            answerLL = (LinearLayout) view.findViewById(R.id.answer_ll);
            answerBtnTV = (TextView) view.findViewById(R.id.answerBtn_tv);
            answerTV = (TextView) view.findViewById(R.id.answer_tv);
            explainTV = (TextView) view.findViewById(R.id.explain_tv);

            navCardLL = (LinearLayout) view.findViewById(R.id.navCard_ll);
            navCardTitleTV = (TextView) view.findViewById(R.id.navCardTitle_tv);
            navCardLV = (ListView) view.findViewById(R.id.navCard_lv);
            flapperTV = (TextView) view.findViewById(R.id.flapper_tv);

            showGroup = new View[]{progressBar, tipTV, contentLL};

            navCardLL.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

    }
}



  /*
    1.创建动画对象，确定起始状态，结束状态
    2.设定动画时间，*.setDuration(longMillion);
    3.确定Interpolator
    4.开始动画
     */


//    // 1&2: 确定起始状态，结束状态
//    TranslateAnimation tAnim = new TranslateAnimation(0, 400, 0, 0);//横向位移400个单位
//    RotateAnimation rAnima = new RotateAnimation(0, 70);//顺时针旋转70度
//    ScaleAnimation sAnima = new ScaleAnimation(0, 5, 0, 5);//横向放大5倍，纵向放大5倍
//    AlphaAnimation aAnima = new AlphaAnimation(1.0f, 0.0f);//从全不透明变为全透明
//// 3: 确定持续时间
//tAnim.setDuration(2000);
//        rAnima.setDuration(2000);
//        sAnima.setDuration(2000);
//        aAnima.setDuration(2000);
//
//        // 4: 确定Interpolator
//        tAnim.setInterpolator(new AccelerateDecelerateInterpolator());
//
//        // 启动动画
//        translation.startAnimation(tAnim);
//        rotate.startAnimation(rAnima);
//        scale.startAnimation(sAnima);
//        alpha.startAnimation(aAnima);
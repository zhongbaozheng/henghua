package com.hengdian.henghua.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
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
import com.hengdian.henghua.adapter.ExamNavCardLvAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.model.contentDataModel.Rs_KS_ExamCommit;
import com.hengdian.henghua.model.contentDataModel.Rs_Questions_NotGroup;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.GadgetUtil;
import com.hengdian.henghua.utils.RefreshHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 试题界面
 */
public class ExamContentFragment extends BackHandledFragment implements View.OnClickListener {
    //private SpannableString SpannableStr;
    ContentActivity activity;
    public ViewHolder viewHolder;

    //答题卡显示状态,true=已打开
    public boolean isNavCardShowed = false;

    private LinearLayout[] mOptionLL;
    private TextView[] mOptionTV;
    private String[] mAnswers; //答案选中项

    int mOptionLlLength;

    private Rs_Questions_NotGroup data;
    private Rs_KS_ExamCommit examCommitResult;
    public List<Question> singleList = new ArrayList<>();
    public List<Question> multipleList = new ArrayList<>();
    public List<Question> trueOrFalseList = new ArrayList<>();

    public int sizeSingle = 0;
    public int sizeMultiple = 0;
    public int sizeTrueFalse = 0;

    //记录当前题号
    public int curIndexSingle = 0;
    public int curIndexMultiple = 0;
    public int curIndexTrueFalse = 0;
    public int curIndex1 = 0;

    private Question curQuestion;
    public int curQuestionType = Question.TYPE_SINGLE;//当前题型，0单选，1多选，2判断

    int optionIndex = 0; //选项开始位置
    int optionLength = 0;//选项长度

    private CountDownTimer countDownTimer;

    private long timeRemain = 0; //剩余时间
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /**
     * 时间置零
     *
     * @param timeTotal
     * @return 异常返回-1
     */
    private long fixTime(long timeTotal) {

        try {
            return sdf.parse("00:00:00").getTime() + timeTotal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //提示时间快结束的标志
    boolean hasNoticeTimeRemain = false;

    /**
     * 考试倒计时开始
     * <p>
     * 最简单的倒计时类，实现了官方的CountDownTimer类
     * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
     * 有onTick，onFinish、cancel和start方法
     *
     * @param time
     */
    private void startTimeCountDown(long time) {
        if (time == 0) {
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeRemain = millisUntilFinished;
                activity.viewHolder.titleBarTextTV.setText(sdf.format(fixTime(timeRemain)));

                if (timeRemain <= 300000 && !hasNoticeTimeRemain) { //5*60*1000  //300000
                    hasNoticeTimeRemain = true;
                    ToastUtil.toastMsgLong("还剩不到5分钟，加油！");
                }
            }

            @Override
            public void onFinish() {
                activity.viewHolder.titleBarTextTV.setText("00:00:00");
                timeRemain = 0;
//                examFinish();

                mHandler.sendEmptyMessageDelayed(Constant.HandlerFlag.EXAM_COMMIT, 3000);

            }
        };
        countDownTimer.start();
    }

    /**
     * 考试倒计时取消（暂停）
     */

    private void stopTimeCountDown() {
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    public boolean onBackPressed() {
        exitWithCheck();

        return true;
    }

    AlertDialog mDialog;

    private void exitWithCheck() {
        if (isAllDone()) {
            exit();
            return;

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            //点击对话框外部区域不起作用。按返回键也不起作用
//            builder.setCancelable(false);
//            //点击对话框外部区域不起作用。按返回键还起作用
//            builder.setCanceledOnTouchOutside(false); //??

            mDialog = builder.create();

            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        startTimeCountDown(timeRemain);
//                    }
                    return false;
                }
            });

            mDialog.setTitle("退出提示");
            mDialog.setMessage("考试结果还没提交呢，是否退出？");
            mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出考试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtil.toastMsgShort("记得回来考试哦");
                            //stopTimeCountDown();

                            dialog.cancel();
                            mDialog = null;
                            mHandler.sendEmptyMessageDelayed(Constant.HandlerFlag.CLOSE_CURRENT_WINDOW, 1000);
                        }
                    }
            );

            mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "继续考试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //startTimeCountDown(timeRemain);
                            //如果在创建AlertDialog的时候调用了setOnCancelListener
                            // 则mCancelMessage变量有作用，否则dismiss和cancel等同
                            dialog.dismiss();
                        }
                    }
            );

            mDialog.show();

        }
    }

    private void examFinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
        builder.setCancelable(false);

        builder.setTitle("提交考试")
                .setMessage("考试结束。成功提交考试结果后，分数将在课程列表中显示。")
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mHandler.sendEmptyMessageDelayed(Constant.HandlerFlag.EXAM_COMMIT, 2000);
                                dialog.dismiss();
                            }
                        }
                ).show();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void exit() {
        RefreshHolder.TEST_EXAM_LIST = RefreshHolder.REFRESH_NET;
        ((ContentActivity) activityCtx).setSelectedFragment(null);

//
//
//        //返回，刷新上级列表
//        Intent intent = new Intent();
//        intent.putExtra("result", "refresh");
//
//        ((ContentActivity) activityCtx).setResult(Constant.RequestResultCode.FROM_EXAM_CONTENT, intent);
        ((ContentActivity) activityCtx).finish();
    }

    private void examCommitWithCheck() {
        if (!isAllAnswered()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
//            builder.setCancelable(false);

            builder.setTitle("提交提示")
                    .setMessage("题目还没做完呢，提交考试结果将结束考试，是否继续提交？")
                    .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHandler.sendEmptyMessage(Constant.HandlerFlag.EXAM_COMMIT);
                                    dialog.cancel();
                                }
                            }
                    )

                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //如果在创建AlertDialog的时候调用了setOnCancelListener则mCancelMessage变量有作用，否则dismiss和cancel等同
                                    dialog.dismiss();
                                }
                            }
                    )
                    .show();
        } else {
            mHandler.sendEmptyMessage(Constant.HandlerFlag.EXAM_COMMIT);
        }
    }

    private void examCommit(final ExamResult examResult) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i(LOGTAG.FRAG_EXAM_CONTENT, "提交考试结果，答案：" + examResult.answer + ",分数：" + examResult.score);
                    examCommitResult = DataRequestUtil.ks_examCommit(bookID, examResult.answer, examResult.score, MyApplication.getAccountInfo().getTokenID());
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_EXAM_CONTENT, "提交考试结果异常：" + e.getMessage());
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(Constant.HandlerFlag.EXAM_COMMIT_ON_RESULT);
            }
        }).start();
    }


    class ExamResult {
        String answer = "";
        int score = 0;

        public ExamResult(String answer, int score) {
            this.answer = answer;
            this.score = score;
        }
    }

    /**
     * @return
     */
    private ExamResult getExamResult() {
        String answer = "";
        int score = 0;
        if (data != null && data.getQuestionList().size() > 0) {

            List<Question> questionList = data.getQuestionList();
            for (int i = 0; i < questionList.size(); i++) {
                Question question = questionList.get(i);
                answer += question.selectedToString();

                if (i < questionList.size() - 1) {
                    answer += ",";
                }

                if (question.getAnswer().equalsIgnoreCase(question.selectedToString())) {
                    score += question.getScore();
                }
            }
        }

        return new ExamResult(answer, score);
    }

    /**
     * 检查是否全部做过
     *
     * @return
     */
    private boolean isAllAnswered() {

        //  没有试题算做完了
        if (data != null && data.getQuestionList().size() == 0) {
            return true;
        }

        //有没做完的就没做完
        List<Question> questionList = data.getQuestionList();
        for (int i = 0; i < questionList.size(); i++) {
            if (questionList.get(i).getState() < Question.STATE2_ANSWERED) {
                return false;
            }

        }

        return true;
    }


    /**
     * 检查是否已经提交答案了
     *
     * @return
     */
    private boolean isAllDone() {
        //如果没有试题内容或者有试题，提交结果为成功，就全搞定了
        if (data == null
                || data.getQuestionList().size() == 0
                || (examCommitResult != null && examCommitResult.getStatus() == 200)) {
            return true;
        } else {
            return false;
        }
    }

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

        activity.viewHolder.titleBarTextTV.setText("00:00:00");
        activity.viewHolder.titleBarTextTV.setVisibility(View.VISIBLE);
        activity.viewHolder.commitTV.setVisibility(View.VISIBLE);
        activity.viewHolder.questionTypeChooserLO.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置底部按钮
     */
    private void initBottomButton() {
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
            viewHolder = new ViewHolder(rootView, false);
        }

        activity.viewHolder.backIV.setOnClickListener(this);
        activity.viewHolder.commitTV.setOnClickListener(this);

        viewHolder.flapperTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (data != null && timeRemain <= 0 && data.getQuestionList().size() > 0) {
                    ToastUtil.toastMsgShort("考试结束，不能做题了哦");
                    return true;
                }

                return false;
            }
        });


//        viewHolder.contentSV.setOnTouchListener( new View.OnTouchListener() {
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

        //选项布局
        mOptionLL = new LinearLayout[]{viewHolder.optionALL,
                viewHolder.optionBLL, viewHolder.optionCLL, viewHolder.optionDLL, viewHolder.optionELL
                , viewHolder.optionFLL, viewHolder.optionGLL, viewHolder.optionTrueLL, viewHolder.optionFalseLL};

        mOptionLlLength = mOptionLL.length;
//        //选项图标
//        mOptionIV = new ImageView[]{viewHolder.optionAIV, viewHolder.optionBIV, viewHolder.optionCIV
//                , viewHolder.optionDIV, viewHolder.optionEIV, viewHolder.optionFalseIV, viewHolder.optionGIV
//                , viewHolder.optionTrueIV, viewHolder.optionFalseIV};
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

                        RefreshHolder.TEST_EXAM_CONTENT = RefreshHolder.REFRESH_NET;
                        return;

                    }

                    curIndex1 = data.getCurIndex();
                    curIndexSingle = data.getCurIndexSingle();
                    curIndexMultiple = data.getCurIndexMultiple();
                    curIndexTrueFalse = data.getCurIndexTrueOrFalse();

                    singleList.clear();
                    multipleList.clear();
                    trueOrFalseList.clear();

                    if (curIndex1 > 0) {
                        ToastUtil.toastMsgShort("欢迎继续考试");
                    }

                    List<Question> questionTotal = data.getQuestionList();

                    for (int i = 0; i < questionTotal.size(); i++) {
                        Question question = questionTotal.get(i);
                        int type = question.getType();

                        switch (type) {
                            case Question.TYPE_SINGLE:
                                singleList.add(question);
                                break;
                            case Question.TYPE_MULTIPLE:
                                multipleList.add(question);
                                break;
                            case Question.TYPE_TRUE_FALSE:
                                trueOrFalseList.add(question);
                                break;
                        }
                    }

                    sizeSingle = singleList.size();
                    sizeMultiple = multipleList.size();
                    sizeTrueFalse = trueOrFalseList.size();

                    //设置题目
                    dealWithIndex(curIndex1);

                    //关闭可能存在的计时
                    stopTimeCountDown();

                    if (questionTotal.size() > 0) {
                        timeRemain = data.getTimeRemain();

                        if (timeRemain > 1000) {
                            startTimeCountDown(timeRemain);
                        } else {
                            examFinish();
                        }
                    } else {
                        timeRemain = 0;
                        stopTimeCountDown();
                    }

                    break;

                case Constant.HandlerFlag.EXAM_COMMIT:

                    if (data == null || data.getQuestionList().size() == 0) {
                        ToastUtil.toastMsgShort("没有需要提交的内容");
                        return;
                    }

                    if (examCommitResult != null && examCommitResult.getStatus() == 200) {
                        ToastUtil.toastMsgShort("已经提交过了");
                        return;
                    }
                    ExamResult examResult = getExamResult();
                    examCommit(examResult);
                    stopTimeCountDown();
                    break;

                case Constant.HandlerFlag.EXAM_COMMIT_ON_RESULT:
                    if (examCommitResult != null && examCommitResult.getStatus() == 200) {
                        timeRemain = 0;

                        AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
                        builder.setTitle("提交成功")
                                .setMessage("提交成功！分数稍后将在课程列表中显示。")
                                .setPositiveButton("退出考试界面", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mHandler.sendEmptyMessageDelayed(Constant.HandlerFlag.CLOSE_CURRENT_WINDOW, 1000);
                                                dialog.dismiss();
                                            }
                                        }
                                ).show();
                    } else {
                        examCommitFailedNotice();
                    }

                    break;


                case Constant.HandlerFlag.CLOSE_CURRENT_WINDOW:

                    exit();
                    break;

            }
        }
    };


    /**
     * 考试结果提交对话框
     */
    public void examCommitFailedNotice() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
        builder.setTitle("提交提示")
                .setMessage("考试结果提交失败，请重新提交")

                .setPositiveButton("提交", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mHandler.sendEmptyMessage(Constant.HandlerFlag.EXAM_COMMIT);
                                dialog.cancel();
                            }
                        }
                )
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //如果在创建AlertDialog的时候调用了setOnCancelListener则mCancelMessage变量有作用，否则dismiss和cancel等同
                                dialog.dismiss();
                            }
                        }
                )
                .show();
    }


    /**
     * 题号索引处理
     *
     * @param index 当前题号
     */
    public void dealWithIndex(int index) {

        //判断题型范围
        if (index < sizeSingle) { //单选

            curIndexSingle = index;
            setContent(index, curIndexSingle, singleList);

        } else if (index < sizeSingle + sizeMultiple) {   //多选

            curIndexMultiple = index - sizeSingle;
            setContent(index, curIndexMultiple, multipleList);

        } else { //判断

            curIndexTrueFalse = index - sizeSingle - sizeMultiple;
            setContent(index, curIndexTrueFalse, trueOrFalseList);

        }

    }


    /**
     * 设置题目内容显示
     *
     * @param index
     * @param listIndex
     * @param listData
     */
    private void setContent(int index, int listIndex, List<Question> listData) {
        viewHolder.contentSV.fullScroll(ScrollView.FOCUS_UP);

        //无数据
        if (listData == null || listData.size() == 0) {

            viewHolder.tipTV.setText("没有内容...");
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);

            //隐藏题目内容布局
            viewHolder.itemNumTV.setText("");
            viewHolder.itemTitleTV.setText("");
            viewHolder.itemTitleTV.setText("");

            for (int i = 0; i < mOptionLL.length; i++) {
                mOptionLL[i].setVisibility(View.INVISIBLE);
                mOptionTV[i].setText("");
            }

            viewHolder.ensureBtnTV.setVisibility(View.INVISIBLE);

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
        mAnswers = curQuestion.getSelected();


        //题号
        String questionNum = GadgetUtil.formatItemNum(index + 1); //题号跟脚标差别
//        String questionNum = GadgetUtil.formatItemNum(curQuestion.getID()); //题号跟脚标差别
        viewHolder.itemNumTV.setText(questionNum);
        viewHolder.itemTypeTV.setText(curQuestion.getQuestionTypeName());
        viewHolder.itemTitleTV.setText(curQuestion.getQuestionContent() + " (" + curQuestion.getScore() + "分)");
        viewHolder.titleModelRL.setVisibility(View.VISIBLE);

        int state = curQuestion.getState();

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

                    //if (state < Question.STATE2_ANSWERED) {
                    mOptionLL[i].setClickable(true);
//                    } else {
//                        mOptionLL[i].setClickable(false);
//                    }
                } else if (i == mOptionLL.length - 1) {
                    //倒1
                    mOptionTV[i].setText(options[1]);
                    mOptionLL[i].setVisibility(View.VISIBLE);

                    // if (state < Question.STATE2_ANSWERED) {
                    mOptionLL[i].setClickable(true);
//                    } else {
//                        mOptionLL[i].setClickable(false);
//                    }
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

//                    if (state < Question.STATE2_ANSWERED) {
                    mOptionLL[i].setClickable(true);
//                    } else {
//                        mOptionLL[i].setClickable(false);
//                    }
                } else {
                    //隐藏多余的
                    mOptionLL[i].setVisibility(View.GONE);
                }
            }
        }

        //设置选项的选中状态
        String[] selected = curQuestion.getSelected();
        for (int i = 0; i < selected.length; i++) {
            if (!selected[i].isEmpty()) {
                mOptionLL[i].setSelected(true);
            } else {
                mOptionLL[i].setSelected(false);
            }
        }

        //设置确定按钮点击和样式
//        if (state < Question.STATE2_ANSWERED) {
        viewHolder.ensureBtnTV.setBackgroundResource(R.drawable.blue_button_slt);
        //viewHolder.ensureBtnTV.setVisibility(View.VISIBLE);
        viewHolder.ensureBtnTV.setVisibility(View.INVISIBLE);
        // viewHolder.ensureBtnTV.setClickable(true);
        viewHolder.answerLL.setVisibility(View.GONE);
//        } else {
//            viewHolder.ensureBtnTV.setBackgroundResource(R.drawable.button_bg_0);
//            viewHolder.ensureBtnTV.setClickable(false);
//            //考试模式隐藏答案
//            viewHolder.answerLL.setVisibility(View.GONE);
//        }

        //如果不是考试模式并且已经做过，显示答案
        if (state >= Question.STATE2_ANSWERED) {
            checkAndShowAnswer(curQuestion, false);
        }
    }

    /**
     * 禁止选项点击
     */
    private void optionsClickForbidden() {
        for (int i = 0; i < optionLength; i++) {
            mOptionLL[optionIndex].setClickable(false);
            optionIndex++;
        }

        viewHolder.ensureBtnTV.setBackgroundResource(R.drawable.button_bg_0);
        viewHolder.ensureBtnTV.setClickable(false);
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

                LogUtil.i(LOGTAG.FRAG_EXAM_CONTENT, "get test questions...");

                try {
                    if (data == null || RefreshHolder.TEST_EXAM_CONTENT == RefreshHolder.REFRESH_LOCAL) {
                        data = new DBUtil(activityCtx).getExamContent(bookID);
                     }

                    if ((data == null || data.getStatus() != 200 || data.getQuestionList().size() == 0) && NetUtil.isNetworkActive(activityCtx)) {
                        data = DataRequestUtil.ks_getQuestionsOfBook(bookID, MyApplication.getAccountInfo().getTokenID());
                    }
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_EXAM_CONTENT, "get test questions exception", e);
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);
            }
        }).start();
    }


    @Override
    public void onClick(View view) {

        if (examListAdapter != null) {
            examListAdapter.notifyDataSetChanged();
        }

        switch (view.getId()) {

            case R.id.lastOne_ll:  //上一题

                backForward();

                break;

            case R.id.nextOne_ll://下一题

                goForward();

                break;

            case R.id.centerButton_ll:
                if (data != null && data.getQuestionList().size() > 0) {
                    switchNavCardShowHide();
                }
                break;

            case R.id.titleBarBack_iv:

                onBackPressed();

                break;

            case R.id.commit_tv:

                examCommitWithCheck();

                break;

            default:
                //按下的不是以上按钮,则按下了内容页的按钮
                dealWithOptions(view);
        }

    }

    private void backForward() {
        viewHolder.lastOneLL.setSelected(false);
        //当前为第一题
        if (curIndex1 < 1) {
            ToastUtil.toastMsgShort("已到开头");
        } else {
            curIndex1--;
            //根据上下一题调整进度
            dealWithIndex(curIndex1);
        }
    }


    private void goForward() {
        viewHolder.navCardLL.setSelected(false);
        //最后一题
        if (curIndex1 >= sizeSingle + sizeMultiple + sizeTrueFalse - 1) {
            ToastUtil.toastMsgShort("已到结尾");

        } else { //不是最后一题

            curIndex1++;
            dealWithIndex(curIndex1);

        }
    }


    /**
     * 测试方法
     */
    private void showSelected() {
        String selectedStr = null;

        if (!Constant.FragTag.TEST_EXERCISE_CHAPTER.equals(fromTag)) {
            selectedStr = data.getQuestionList().get(curIndex1).selectedToString();
        } else {
            if (curQuestionType == Question.TYPE_SINGLE) {
                selectedStr = singleList.get(curIndexSingle).selectedToString();
            } else if (curQuestionType == Question.TYPE_MULTIPLE) {
                selectedStr = multipleList.get(curIndexMultiple).selectedToString();
            } else if (curQuestionType == Question.TYPE_TRUE_FALSE) {
                selectedStr = trueOrFalseList.get(curIndexTrueFalse).selectedToString();
            }
        }
        ToastUtil.toastMsgShort(selectedStr);
    }


    /**
     * 选项处理
     *
     * @param view
     */
    private void dealWithOptions(View view) {
        dealWithSelection(view);

//        switch (view.getId()) {
//
//            case R.id.ensureBtn_tv: //确定
//
//                if (curQuestion.selectedToString().length() == 0) {
//                    Toast.makeText(activityCtx, "请选择", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //optionsClickForbidden(); //禁止点击
//                curQuestion.setState(Question.STATE2_ANSWERED);
//                checkAndShowAnswer(curQuestion, false);
//
//                break;
//
//            default:
//
//                dealWithSelection(view);
//        }
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

        curQuestion.setState(Question.STATE2_ANSWERED);
        checkAndShowAnswer(curQuestion, false);
//        curQuestion.setState(Question.STATE1_DOING);
        //showSelected();
    }


    /**
     * 校验答案，并控制确定按钮、答案状态按钮及答案解析  隐藏或显示
     *
     * @param question   对应的问题封装体
     * @param showAnswer 是否显示答案
     */
    private void checkAndShowAnswer(Question question, boolean showAnswer) {

        String selection = question.selectedToString();
        String answer = question.getAnswer();
        if (selection.isEmpty()) {
            question.setState(Question.STATE0_DEFAULT);
        } else if (selection.equalsIgnoreCase(answer)) {
            viewHolder.answerBtnTV.setBackground(activityCtx.getResources().getDrawable(R.drawable.answer_rigth));
            question.setState(Question.STATE3_RIGHT);
        } else {
            viewHolder.answerBtnTV.setBackground(activityCtx.getResources().getDrawable(R.drawable.answer_error));
            question.setState(Question.STATE3_WRONG);
        }

        if (question.getState() >= Question.STATE2_ANSWERED && showAnswer) {
            // viewHolder.ensureBtnTV.setVisibility(View.GONE);
            viewHolder.answerLL.setVisibility(View.VISIBLE);

            viewHolder.answerTV.setText("参考答案：" + answer);
            viewHolder.explainTV.setText("题目解析：" + (question.getExplain().isEmpty() ? "略。" : question.getExplain()));
        } else {
            //viewHolder.ensureBtnTV.setVisibility(View.VISIBLE);
            viewHolder.answerLL.setVisibility(View.GONE);
        }
    }


    /**
     * 切换答题卡显示或隐藏
     */
    public void switchNavCardShowHide() {
        //如果导航卡已经显示,则隐藏
        showNavCard(!isNavCardShowed);
    }

    ExamNavCardLvAdapter examListAdapter;

    /**
     * 显示或隐藏答题卡
     */
    public void showNavCard(boolean show) {
        if (show) {
            viewHolder.centerButtonLL.setSelected(true);
            viewHolder.navCardLL.setVisibility(View.VISIBLE);

            if (examListAdapter == null) {

                examListAdapter = new ExamNavCardLvAdapter(
                        activityCtx,
                        singleList,
                        multipleList,
                        trueOrFalseList);

            }

//            this.setListViewHeightBasedOnChildren(viewHolder.navCardLV); 去掉这行代码
            viewHolder.navCardLV.setAdapter(examListAdapter);
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
        ((ContentActivity) activityCtx).setSelectedFragment(this);

        if ((data == null || data.getStatus() != 200) && !isLoading ) {
            getData();
        }

        if (timeRemain > 0) {
            startTimeCountDown(timeRemain);
        }
        super.onResume();

    }

    //暂停时存储状态
    @Override
    public void onPause() {

//        stopTimeCountDown();
//
        saveDataToDb();

        super.onPause();
    }


    private void saveDataToDb() {

        if (data != null && data.getStatus() == 200) {
            data.setTimeRemain(timeRemain);
            data.setCurIndex(curIndex1);
            data.setCurIndexSingle(curIndexSingle);
            data.setCurIndexMultiple(curIndexMultiple);
            data.setCurIndexTrueOrFalse(curIndexTrueFalse);

            new DBUtil(activityCtx).saveExamContent(bookID, data);

            RefreshHolder.TEST_EXAM_LIST = RefreshHolder.REFRESH_NET;
        }

    }

    public static class ViewHolder extends ViewViewHolder {
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
        ListView navCardLV;

        TextView flapperTV;

        View[] showGroup;

        ViewHolder(View view, boolean refind) {
            super(view, refind);
        }

        /**
         * 初始化布局控件,比注解节省系统开销
         */
        protected void findViews() {
            lastOneLL = $(R.id.lastOne_ll);
            centerButtonLL = $(R.id.centerButton_ll);
            nextOneLL = $(R.id.nextOne_ll);

            lastOneIV = $(R.id.lastOne_iv);
            centerButtonIV = $(R.id.centerButton_iv);
            nextOneIV = $(R.id.nextOne_iv);

            lastOneTV = $(R.id.lastOne_tv);
            centerButtonTV = $(R.id.centerButton_tv);
            nextOneTV = $(R.id.nextOne_tv);

            // tipRL = $(R.id.tip_rl);
            tipTV = $(R.id.tip_tv);
            progressBar = $(R.id.progressBar);

            contentLL = $(R.id.content_ll);
            contentSV = $(R.id.content_sv);

            titleModelRL = $(R.id.titleModel_rl);
            itemNumTV = $(R.id.itemNum_tv);
            itemTitleTV = $(R.id.itemTitle_tv);
            itemTypeTV = $(R.id.itemType_tv);

            optionsLL = $(R.id.options_ll);

            optionAIV = $(R.id.optionA_iv);
            optionATV = $(R.id.optionA_tv);
            optionALL = $(R.id.optionA_ll);

            optionBIV = $(R.id.optionB_iv);
            optionBTV = $(R.id.optionB_tv);
            optionBLL = $(R.id.optionB_ll);

            optionCIV = $(R.id.optionC_iv);
            optionCTV = $(R.id.optionC_tv);
            optionCLL = $(R.id.optionC_ll);

            optionDIV = $(R.id.optionD_iv);
            optionDTV = $(R.id.optionD_tv);
            optionDLL = $(R.id.optionD_ll);

            optionEIV = $(R.id.optionE_iv);
            optionETV = $(R.id.optionE_tv);
            optionELL = $(R.id.optionE_ll);

            optionFIV = $(R.id.optionF_iv);
            optionFTV = $(R.id.optionF_tv);
            optionFLL = $(R.id.optionF_ll);

            optionGIV = $(R.id.optionG_iv);
            optionGTV = $(R.id.optionG_tv);
            optionGLL = $(R.id.optionG_ll);

            optionTrueIV = $(R.id.optionTrue_iv);
            optionTrueTV = $(R.id.optionTrue_tv);
            optionTrueLL = $(R.id.optionTrue_ll);

            optionFalseIV = $(R.id.optionFalse_iv);
            optionFalseTV = $(R.id.optionFalse_tv);
            optionFalseLL = $(R.id.optionFalse_ll);

            ensureBtnTV = $(R.id.ensureBtn_tv);
            answerLL = $(R.id.answer_ll);
            answerBtnTV = $(R.id.answerBtn_tv);
            answerTV = $(R.id.answer_tv);
            explainTV = $(R.id.explain_tv);

            navCardLL = $(R.id.navCard_ll);
            navCardLV = $(R.id.navCard_lv);
            flapperTV = $(R.id.flapper_tv);


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
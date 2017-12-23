package com.hengdian.henghua.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
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
import com.hengdian.henghua.adapter.StageNavCardLvAdapter;
import com.hengdian.henghua.adapter.StageScrollAdapter;
import com.hengdian.henghua.androidUtil.DBUtil;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.OtsUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.model.contentDataModel.Rs_Questions_NotGroup;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.utils.GadgetUtil;
import com.hengdian.henghua.utils.RefreshHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 闯关模式试题界面
 */
public class StageContentFragment extends BaseFragment implements View.OnClickListener {
    ContentActivity activity;
    public ViewHolder viewHolder;

    //答题卡显示状态,true=已打开
    public boolean isNavCardShowed = false;

    private LinearLayout[] mOptionLL;
    private TextView[] mOptionTV;
    private String[] mAnswers; //答案选中项

    int mOptionLlLength;

    private Rs_Questions_NotGroup data;
    public List<Question> singleList;
    public List<Question> multipleList;
    public List<Question> trueOrFalseList;

    public List<Question> mQuestionList = new ArrayList<>();
    public List<View> mListView = new ArrayList<>();

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

    private StageScrollAdapter mAdapter;

    PagerHolder pagerHolder;


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

        activity.viewHolder.titleBarTextTV.setText("闯关模式");
        activity.viewHolder.titleBarTextTV.setVisibility(View.VISIBLE);
        activity.viewHolder.commitTV.setVisibility(View.GONE);
        activity.viewHolder.questionTypeChooserLO.setVisibility(View.GONE);
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

        rootView = inflater.inflate(R.layout.fr_stage_content, null);

        if (viewHolder == null) {
            viewHolder = new ViewHolder(rootView);
        }

        activity.viewHolder.backIV.setOnClickListener(this);
        activity.viewHolder.commitTV.setOnClickListener(this);

//        viewHolder.flapperTV.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                return false;
//            }
//        });

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


//        //选项布局
//        mOptionLL = new LinearLayout[]{viewHolder.optionALL,
//                viewHolder.optionBLL, viewHolder.optionCLL, viewHolder.optionDLL, viewHolder.optionELL
//                , viewHolder.optionFLL, viewHolder.optionGLL, viewHolder.optionTrueLL, viewHolder.optionFalseLL};
//
//        mOptionLlLength = mOptionLL.length;
//
//        //选项文字
//        mOptionTV = new TextView[]{viewHolder.optionATV, viewHolder.optionBTV,
//                viewHolder.optionCTV, viewHolder.optionDTV, viewHolder.optionETV, viewHolder.optionFalseTV
//                , viewHolder.optionGTV, viewHolder.optionTrueTV, viewHolder.optionFalseTV};


        initTopBar();
        initBottomButton();

//        for (int i = 0; i < mOptionLL.length; i++) {
//            mOptionLL[i].setOnClickListener(this);
//        }
//
//        viewHolder.ensureBtnTV.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void initFragmentData() {
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

                        RefreshHolder.TEST_STAGE_CONTENT = RefreshHolder.REFRESH_NET;
                        return;

                    }

                    curIndex1 = data.getCurIndex();
                    curIndexSingle = data.getCurIndexSingle();
                    curIndexMultiple = data.getCurIndexMultiple();
                    curIndexTrueFalse = data.getCurIndexTrueOrFalse();

                    List<Question> questionTotal = data.getQuestionList();
                    mQuestionList.clear();
                    mQuestionList.addAll(questionTotal);
                    mListView.clear();
                    for(int i=0;i<mQuestionList.size();i++){
                        View view = View.inflate(activityCtx,R.layout.stage_scroll_pager,null);
                        mListView.add(view);
                    }
                    Log.e("mListView",mListView.size()+"");
                    singleList = new ArrayList<>();
                    multipleList = new ArrayList<>();
                    trueOrFalseList = new ArrayList<>();

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

                    //
                    mAdapter = new StageScrollAdapter(activityCtx,mListView,mQuestionList);
                    viewHolder.mViewPager.setAdapter(mAdapter);

                    viewHolder.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        int current = 0;
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            //设置当前
                            pagerHolder = new PagerHolder(mListView.get(position));
                            setPagerViewsClick();
                            dealWithIndex(curIndex1);

                        }

                        @Override
                        public void onPageSelected(int position) {

                            Log.e("selectPosition",position+"");
                            Log.e("current",current+"");
                            if(position>current){
                                //右滑
                                current = position;
                                //右滑动就设置检测当前的监听，左滑就不用,可以回溯上一题
                                //设置currentIndex
                                if (curQuestion.getState() < Question.STATE2_ANSWERED) {
                                    ToastUtil.toastMsgShort("请先解答当前试题");
                                }else {
                                    curIndex1 = position;
                                    dealWithIndex(curIndex1);
                                    if(isNavCardShowed){
                                        stageNavCardLvAdapter.notifyDataSetChanged();
                                        stageNavCardLvAdapter.stageNavCardGvAdapter.notifyDataSetChanged();
                                    }
                                }

                            }else if(position<current){
                                //左滑
                                current = position;
                                curIndex1 = position;
                                dealWithIndex(curIndex1);
                                if(isNavCardShowed){
                                    stageNavCardLvAdapter.notifyDataSetChanged();
                                    stageNavCardLvAdapter.stageNavCardGvAdapter.notifyDataSetChanged();
                                }
                            }

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                            Log.e("state",state+"");

                        }
                    });


                    //设置题目
                    //初始化第一次加载的数据
                    pagerHolder = new PagerHolder(mListView.get(curIndex1));
                    setPagerViewsClick();
                    dealWithIndex(curIndex1);

                    break;
            }
        }
    };


    /**
     * 初始化布局
     */
    private void setPagerViewsClick(){
        //选项布局
        mOptionLL = new LinearLayout[]{pagerHolder.optionALL,
                pagerHolder.optionBLL, pagerHolder.optionCLL, pagerHolder.optionDLL, pagerHolder.optionELL
                , pagerHolder.optionFLL, pagerHolder.optionGLL, pagerHolder.optionTrueLL, pagerHolder.optionFalseLL};

        mOptionLlLength = mOptionLL.length;

        //选项文字
        mOptionTV = new TextView[]{pagerHolder.optionATV, pagerHolder.optionBTV,
                pagerHolder.optionCTV, pagerHolder.optionDTV, pagerHolder.optionETV, pagerHolder.optionFalseTV
                , pagerHolder.optionGTV, pagerHolder.optionTrueTV, pagerHolder.optionFalseTV};

        for (int i = 0; i < mOptionLL.length; i++) {

            mOptionLL[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //处理选项点击
                    dealWithSelection(v);
                }
            });
        }

        pagerHolder.ensureBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理确定按钮
                if (curQuestion.selectedToString().length() == 0) {
                    Toast.makeText(activityCtx, "请选择", Toast.LENGTH_SHORT).show();
                    return;
                }

                //optionsClickForbidden(); //禁止点击
                curQuestion.setState(Question.STATE2_ANSWERED);
                checkAndShowAnswer(curQuestion, true);
            }
        });
    }



    /**
     * 内容设置
     * @param listIndex
     */
    private void setCurrentQuestion(int listIndex){

        String questionNum = GadgetUtil.formatItemNum(listIndex + 1); //题号跟脚标差别
        pagerHolder.itemNumTV.setText(questionNum);
        pagerHolder.itemTypeTV.setText(curQuestion.getQuestionTypeName());
        pagerHolder.itemTitleTV.setText(curQuestion.getQuestionContent() + " (" + curQuestion.getScore() + "分)");
        pagerHolder.titleModelRL.setVisibility(View.VISIBLE);

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

                    if (state < Question.STATE2_ANSWERED) {
                        mOptionLL[i].setClickable(true);
                    } else {
                        mOptionLL[i].setClickable(false);
                    }
                } else if (i == mOptionLL.length - 1) {
                    //倒1
                    mOptionTV[i].setText(options[1]);
                    mOptionLL[i].setVisibility(View.VISIBLE);

                    if (state < Question.STATE2_ANSWERED) {
                        mOptionLL[i].setClickable(true);
                    } else {
                        mOptionLL[i].setClickable(false);
                    }
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

                    if (state < Question.STATE2_ANSWERED) {
                        mOptionLL[i].setClickable(true);
                    } else {
                        mOptionLL[i].setClickable(false);
                    }
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


        //如果不是考试模式并且已经做过，显示答案
        if (state >= Question.STATE2_ANSWERED) {
            optionsClickForbidden();
            showAnswer(curQuestion, true);
        } else {
            showAnswer(curQuestion, false);
        }

    }


    /**
     * 题号索引处理
     *
     * @param index 当前题号
     */
    public void dealWithIndex(int index) {

        if (data != null && index >= data.getQuestionList().size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
            builder.setTitle("恭喜你，闯关成功！")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    activity.onBackPressed();
                                }
                            }
                    )
                    .show();
            return;
        }

        setContent(index,mQuestionList);

//        //判断题型范围
//        if (index < sizeSingle) { //单选
//
//            curIndexSingle = index;
//            setContent(index, curIndexSingle, singleList);
//
//        } else if (index < sizeSingle + sizeMultiple) {   //多选
//
//            curIndexMultiple = index - sizeSingle;
//            setContent(index, curIndexMultiple, multipleList);
//
//        } else { //判断
//
//            if (data != null && index >= data.getQuestionList().size()) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);
//                builder.setTitle("恭喜你，闯关成功！")
//                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        activity.onBackPressed();
//                                    }
//                                }
//                        )
//                        .show();
//                return;
//            }
//
//            curIndexTrueFalse = index - sizeSingle - sizeMultiple;
//            setContent(index, curIndexTrueFalse, trueOrFalseList);
//
//        }

    }


    /**
     * 设置题目内容显示
     *
     * @param index
     * @param listData
     */
    private void setContent(int index, List<Question> listData) {
        viewHolder.contentSV.fullScroll(ScrollView.FOCUS_UP);

        //无数据
        if (listData == null || listData.size() == 0) {

            viewHolder.tipTV.setText("没有内容...");
            OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_TEXT_TIP);

//            //隐藏题目内容布局
//            viewHolder.itemNumTV.setText("");
//            viewHolder.itemTitleTV.setText("");
//            viewHolder.itemTitleTV.setText("");
//
//            for (int i = 0; i < mOptionLL.length; i++) {
//                mOptionLL[i].setVisibility(View.INVISIBLE);
//                mOptionTV[i].setText("");
//            }

            return;
        }

        OtsUtil.windowShow(viewHolder.showGroup, OtsUtil.SHOW_CONTENT_LAYOUT);


        /**
         * 重置上一个题目为非选中
         */
        if (curQuestion != null) {
            curQuestion.isShow = false;
        }

        curQuestion = listData.get(index);
        curQuestion.isShow = true;
        curQuestionType = curQuestion.getType();
        mAnswers = curQuestion.getSelected();
        viewHolder.mViewPager.setCurrentItem(index);
        setCurrentQuestion(index);


//        //题号
//        String questionNum = GadgetUtil.formatItemNum(index + 1); //题号跟脚标差别
//        viewHolder.itemNumTV.setText(questionNum);
//        viewHolder.itemTypeTV.setText(curQuestion.getQuestionTypeName());
//        viewHolder.itemTitleTV.setText(curQuestion.getQuestionContent() + " (" + curQuestion.getScore() + "分)");
//        viewHolder.titleModelRL.setVisibility(View.VISIBLE);
//
//        int state = curQuestion.getState();
//
//        String[] options = curQuestion.getOptions();
//        //显示对应数量选项
//        for (int i = 0; i < mOptionLL.length; i++) {
//            //初始化
//            mOptionLL[i].setSelected(false);
//
//            if (curQuestionType == Question.TYPE_TRUE_FALSE) {
//                optionIndex = mOptionLlLength - 2;
//                optionLength = 2;
//
//                //设置判断题选项
//                if (i == mOptionLlLength - 2) {
//
//                    //对,倒2
//                    mOptionTV[i].setText(options[0]);
//                    mOptionLL[i].setVisibility(View.VISIBLE);
//
//                    if (state < Question.STATE2_ANSWERED) {
//                        mOptionLL[i].setClickable(true);
//                    } else {
//                        mOptionLL[i].setClickable(false);
//                    }
//                } else if (i == mOptionLL.length - 1) {
//                    //倒1
//                    mOptionTV[i].setText(options[1]);
//                    mOptionLL[i].setVisibility(View.VISIBLE);
//
//                    if (state < Question.STATE2_ANSWERED) {
//                        mOptionLL[i].setClickable(true);
//                    } else {
//                        mOptionLL[i].setClickable(false);
//                    }
//                } else {
//                    //隐藏多余的
//                    mOptionLL[i].setVisibility(View.GONE);
//                }
//
//            } else {//不是判断题
//                optionIndex = 0;
//                optionLength = options.length;
//
//                if (i < options.length) {
//                    mOptionTV[i].setText(options[i]);
//                    mOptionLL[i].setVisibility(View.VISIBLE);
//
//                    if (state < Question.STATE2_ANSWERED) {
//                        mOptionLL[i].setClickable(true);
//                    } else {
//                        mOptionLL[i].setClickable(false);
//                    }
//                } else {
//                    //隐藏多余的
//                    mOptionLL[i].setVisibility(View.GONE);
//                }
//            }
//        }
//
//        //设置选项的选中状态
//        String[] selected = curQuestion.getSelected();
//        for (int i = 0; i < selected.length; i++) {
//            if (!selected[i].isEmpty()) {
//                mOptionLL[i].setSelected(true);
//            } else {
//                mOptionLL[i].setSelected(false);
//            }
//        }
//
//
//        //如果不是考试模式并且已经做过，显示答案
//        if (state >= Question.STATE2_ANSWERED) {
//            optionsClickForbidden();
//            showAnswer(curQuestion, true);
//        } else {
//            showAnswer(curQuestion, false);
//        }
    }

    /**
     * 禁止选项点击
     */
    private void optionsClickForbidden() {
        for (int i = 0; i < optionLength; i++) {
            mOptionLL[optionIndex].setClickable(false);
            optionIndex++;
        }

        pagerHolder.ensureBtnTV.setClickable(false);
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

                LogUtil.i(LOGTAG.FRAG_STAGE_CONTENT, "获取闯关试题数据");

                try {
                    data = DataRequestUtil.cg_getQuestionsOfChapter(chapterID, MyApplication.getAccountInfo().getTokenID());
                } catch (Exception e) {
                    LogUtil.e(LOGTAG.FRAG_STAGE_CONTENT, "获取闯关试题数据");
                    e.printStackTrace();
                }

                isLoading = false;
                mHandler.sendEmptyMessage(Constant.HandlerFlag.GET_DATA_ON_RESULT);

            }
        }).start();
    }


    @Override
    public void onClick(View view) {

        if (stageNavCardLvAdapter != null) {
            stageNavCardLvAdapter.notifyDataSetChanged();
        }

        switch (view.getId()) {

            case R.id.lastOne_ll:

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

//                Intent intent = new Intent();
//                intent.putExtra("result", "refresh");
//
//                ((ContentActivity) activityCtx).setResult(2, intent);
                ((ContentActivity) activityCtx).finish();
                break;

            default:
                //按下的不是以上按钮,则按下了内容页的按钮
//                dealWithOptions(view);
        }


    }


    private void backForward() {
        //当前为第一题
        if (curIndex1 < 1) {
            ToastUtil.toastMsgShort("已到开头");
        } else {
            viewHolder.mViewPager.arrowScroll(1);
//            curIndex1--;
//            dealWithIndex(curIndex1);
        }
    }


    private void goForward() {
        if (curQuestion.getState() < Question.STATE2_ANSWERED) {
            ToastUtil.toastMsgShort("请先解答当前试题");
            return;
        }

        //最后一题
        if (curIndex1 >= sizeSingle + sizeMultiple + sizeTrueFalse - 1) {
            ToastUtil.toastMsgShort("已到结尾");

        } else { //不是最后一题

            viewHolder.mViewPager.arrowScroll(2);

//            curIndex1++;
//            dealWithIndex(curIndex1);

        }
    }

    /**
     * 选项处理
     *
     * @param view
     */
//    private void dealWithOptions(View view) {
//
//        switch (view.getId()) {
//
//            case R.id.ensureBtn_tv: //确定
//
//                if (curQuestion.selectedToString().length() == 0) {
//                    Toast.makeText(activityCtx, "请选择", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                optionsClickForbidden(); //禁止点击
//                curQuestion.setState(Question.STATE2_ANSWERED);
//                checkAndShowAnswer(curQuestion, true);
//
//                break;
//
//            default:
//
//                dealWithSelection(view);
//        }
//    }

    /**
     * 选项处理，并保存
     *
     * @param view
     */
    private void dealWithSelection(View view) {
        //如果是单选，把非点中项设为不选中
        int childCount = pagerHolder.optionsLL.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View optionView = pagerHolder.optionsLL.getChildAt(i);

            //如果当前没被选中，且不是多选，设为不选中
            if (view.getId() != optionView.getId() && curQuestionType != Question.TYPE_MULTIPLE) {
                pagerHolder.optionsLL.getChildAt(i).setSelected(false);
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

        if (curQuestionType != Question.TYPE_MULTIPLE) {
            optionsClickForbidden(); //禁止点击
            curQuestion.setState(Question.STATE2_ANSWERED);
            checkAndShowAnswer(curQuestion, true);
        }

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
        if (selection.equalsIgnoreCase(answer)) {
            question.setState(Question.STATE3_RIGHT);
        } else {
            question.setState(Question.STATE3_WRONG);

            AlertDialog.Builder builder = new AlertDialog.Builder(activityCtx);

            //点击对话框外部区域不起作用。按返回键也不起作用
            builder.setCancelable(false);
//            //点击对话框外部区域不起作用。按返回键还起作用
//            builder.setCanceledOnTouchOutside(false); //??

            builder.setMessage("闯关失败！本次闯关到达：" + curIndex1 + "/" + data.getQuestionList().size())
                    .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
//                                    transaction.remove(StageContentFragment.this);
//                                    transaction.add(R.id.container_fl,new StageContentFragment());
//                                    transaction.commit();
                                    getData();
//                                    stageNavCardLvAdapter.notifyDataSetChanged();
//                                    stageNavCardLvAdapter.stageNavCardGvAdapter.notifyDataSetChanged();
                                    dialog.cancel();
                                }
                            }
                    )
                    .setNegativeButton("结束游戏", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    activity.onBackPressed();
                                }
                            }
                    )
                    .show();
        }

        showAnswer(question, showAnswer);

        if (showAnswer) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewHolder.contentSV.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 200);
        }


//        //自动跳到下一题
//        if (curQuestion.getState() == Question.STATE3_RIGHT) {
//            curIndex1++;
//            dealWithIndex(curIndex1);
//        }
    }


    private void showAnswer(Question question, boolean showAnswer) {
        if (showAnswer) {
            pagerHolder.ensureBtnTV.setVisibility(View.GONE);

            if (question.getState() == Question.STATE3_RIGHT) {
                pagerHolder.answerBtnTV.setBackground(activityCtx.getResources().getDrawable(R.drawable.answer_rigth));
            } else if (question.getState() == Question.STATE3_WRONG) {
                pagerHolder.answerBtnTV.setBackground(activityCtx.getResources().getDrawable(R.drawable.answer_error));
            }

            pagerHolder.answerBtnTV.setVisibility(View.VISIBLE);
            pagerHolder.answerLL.setVisibility(View.VISIBLE);
            pagerHolder.answerTV.setText("参考答案：" + question.getAnswer());
            pagerHolder.explainTV.setText("题目解析：" + (question.getExplain().isEmpty() ? "略。" : question.getExplain()));

        } else {
            //多选题，不显示答案就显示确定按钮
            if (question.getType() == Question.TYPE_MULTIPLE) {
                pagerHolder.ensureBtnTV.setVisibility(View.VISIBLE);
                pagerHolder.ensureBtnTV.setClickable(true);
            } else {
                pagerHolder.ensureBtnTV.setVisibility(View.GONE);
            }

            pagerHolder.answerLL.setVisibility(View.GONE);
            pagerHolder.answerTV.setText("");
            pagerHolder.explainTV.setText("");
        }
    }

    /**
     * 切换答题卡显示或隐藏
     */
    public void switchNavCardShowHide() {
        //如果导航卡已经显示,则隐藏
        showNavCard(!isNavCardShowed);
    }

    StageNavCardLvAdapter stageNavCardLvAdapter;

    /**
     * 显示或隐藏答题卡
     */
    public void showNavCard(boolean show) {
        if (show) {
            viewHolder.centerButtonLL.setSelected(true);
            viewHolder.navCardLL.setVisibility(View.VISIBLE);

            if (stageNavCardLvAdapter == null) {

                stageNavCardLvAdapter = new StageNavCardLvAdapter(
                        activityCtx,
                        singleList,
                        multipleList,
                        trueOrFalseList);

            }

//            this.setListViewHeightBasedOnChildren(viewHolder.navCardLV);
            viewHolder.navCardLV.setAdapter(stageNavCardLvAdapter);
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

        if ((data == null || data.getStatus() != 200 || data.getQuestionList().size() == 0) && !isLoading && NetUtil.isNetworkActive(activityCtx)) {
           // RefreshHolder.TEST_STAGE_CONTENT = RefreshHolder.REFRESH_NET;
            getData();
        }

        super.onResume();

    }

    //暂停时存储状态
    @Override
    public void onPause() {

        saveStatusToDB();

        super.onPause();
    }

    public static final int STATE_PASSED = 100;
    public static final int STATE_UNLOCKED = 50;

    private void saveStatusToDB() {

        if (data != null && data.getStatus() == 200) {
            if (curIndex1 == data.getQuestionList().size() - 1 || data.getQuestionList().size() == 0) {
                new DBUtil(activityCtx).saveStageContent(bookID, chapterID, 100, STATE_PASSED);
            } else {
                new DBUtil(activityCtx).saveStageContent(bookID, chapterID, 100, STATE_UNLOCKED);
            }
            //提醒本地刷新知识重温列表
            RefreshHolder.TEST_STAGE_LIST = RefreshHolder.REFRESH_LOCAL;
        }

    }


    public class PagerHolder{
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

        public PagerHolder(View v){
            findviews(v);
        }

        private void findviews(View view){
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
        }

    }


    public static class ViewHolder {
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

        ViewPager mViewPager;

//        RelativeLayout titleModelRL;
//        TextView itemNumTV;//题号
//        TextView itemTitleTV;//题目内容
//        TextView itemTypeTV;//题型
//
//        LinearLayout optionsLL;
//
//        ImageView optionAIV;
//        TextView optionATV;
//        LinearLayout optionALL;
//
//        ImageView optionBIV;
//        TextView optionBTV;
//        LinearLayout optionBLL;
//
//        ImageView optionCIV;
//        TextView optionCTV;
//        LinearLayout optionCLL;
//
//        ImageView optionDIV;
//        TextView optionDTV;
//        LinearLayout optionDLL;
//
//        ImageView optionEIV;
//        TextView optionETV;
//        LinearLayout optionELL;
//
//        ImageView optionFIV;
//        TextView optionFTV;
//        LinearLayout optionFLL;
//
//        ImageView optionGIV;
//        TextView optionGTV;
//        LinearLayout optionGLL;
//
//
//        ImageView optionTrueIV;
//        TextView optionTrueTV;
//        LinearLayout optionTrueLL;
//
//        ImageView optionFalseIV;
//        TextView optionFalseTV;
//        LinearLayout optionFalseLL;
//
//        TextView ensureBtnTV;
//
//        LinearLayout answerLL;
//        TextView answerBtnTV;
//        TextView answerTV;
//        TextView explainTV;

        LinearLayout navCardLL;
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

//            titleModelRL = (RelativeLayout) view.findViewById(R.id.titleModel_rl);
//            itemNumTV = (TextView) view.findViewById(R.id.itemNum_tv);
//            itemTitleTV = (TextView) view.findViewById(R.id.itemTitle_tv);
//            itemTypeTV = (TextView) view.findViewById(R.id.itemType_tv);
//
//            optionsLL = (LinearLayout) view.findViewById(R.id.options_ll);
//
//            optionAIV = (ImageView) view.findViewById(R.id.optionA_iv);
//            optionATV = (TextView) view.findViewById(R.id.optionA_tv);
//            optionALL = (LinearLayout) view.findViewById(R.id.optionA_ll);
//
//            optionBIV = (ImageView) view.findViewById(R.id.optionB_iv);
//            optionBTV = (TextView) view.findViewById(R.id.optionB_tv);
//            optionBLL = (LinearLayout) view.findViewById(R.id.optionB_ll);
//
//            optionCIV = (ImageView) view.findViewById(R.id.optionC_iv);
//            optionCTV = (TextView) view.findViewById(R.id.optionC_tv);
//            optionCLL = (LinearLayout) view.findViewById(R.id.optionC_ll);
//
//            optionDIV = (ImageView) view.findViewById(R.id.optionD_iv);
//            optionDTV = (TextView) view.findViewById(R.id.optionD_tv);
//            optionDLL = (LinearLayout) view.findViewById(R.id.optionD_ll);
//
//            optionEIV = (ImageView) view.findViewById(R.id.optionE_iv);
//            optionETV = (TextView) view.findViewById(R.id.optionE_tv);
//            optionELL = (LinearLayout) view.findViewById(R.id.optionE_ll);
//
//            optionFIV = (ImageView) view.findViewById(R.id.optionF_iv);
//            optionFTV = (TextView) view.findViewById(R.id.optionF_tv);
//            optionFLL = (LinearLayout) view.findViewById(R.id.optionF_ll);
//
//            optionGIV = (ImageView) view.findViewById(R.id.optionG_iv);
//            optionGTV = (TextView) view.findViewById(R.id.optionG_tv);
//            optionGLL = (LinearLayout) view.findViewById(R.id.optionG_ll);
//
//            optionTrueIV = (ImageView) view.findViewById(R.id.optionTrue_iv);
//            optionTrueTV = (TextView) view.findViewById(R.id.optionTrue_tv);
//            optionTrueLL = (LinearLayout) view.findViewById(R.id.optionTrue_ll);
//
//            optionFalseIV = (ImageView) view.findViewById(R.id.optionFalse_iv);
//            optionFalseTV = (TextView) view.findViewById(R.id.optionFalse_tv);
//            optionFalseLL = (LinearLayout) view.findViewById(R.id.optionFalse_ll);
//
//            ensureBtnTV = (TextView) view.findViewById(R.id.ensureBtn_tv);
//            answerLL = (LinearLayout) view.findViewById(R.id.answer_ll);
//            answerBtnTV = (TextView) view.findViewById(R.id.answerBtn_tv);
//            answerTV = (TextView) view.findViewById(R.id.answer_tv);
//            explainTV = (TextView) view.findViewById(R.id.explain_tv);

            navCardLL = (LinearLayout) view.findViewById(R.id.navCard_ll);
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
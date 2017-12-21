//package com.hengdian.henghua.adapter;
//
//import android.content.Context;
//import android.support.v4.view.PagerAdapter;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.hengdian.henghua.R;
//import com.hengdian.henghua.model.contentDataModel.Question;
//import com.hengdian.henghua.utils.GadgetUtil;
//
//import java.util.List;
//
///**
// * Created by micro on 2017/12/20.
// */
//
//public class ExercisePagerScrollAdapter extends PagerAdapter {
//
//    private List<View> mSingleListView;
//    private List<View> mMultiListView;
//    private List<View> mTrueOrFalseListView;
//    //
//    private List<Question> mSingleQuestionList;
//    private List<Question> mMultiQuestionList;
//    private List<Question> mTrueOrFalseQuestionList;
//    private Context mContext;
//    public ExecriseHolder viewHolder;
//
//    private View currentView;
//
//    private int TYPE;
//
//    public ExercisePagerScrollAdapter(Context context,List<Question> singleQuestionList,List<View> singleListView,
//                                      List<Question> multiQuestionList,List<View> mutiListView,
//                                      List<Question> trueOrFalseQuestionList,List<View> trueOrFalseListView,int type){
//        mContext = context;
//        mSingleListView = singleListView;
//        mMultiListView = mutiListView;
//        mTrueOrFalseListView = trueOrFalseListView;
//        mSingleQuestionList = singleQuestionList;
//        mMultiQuestionList = multiQuestionList;
//        mTrueOrFalseQuestionList = trueOrFalseQuestionList;
//        TYPE = type;
//    }
//
//    @Override
//    public int getCount() {
//
//        if(TYPE == Question.TYPE_SINGLE){
//            return mSingleQuestionList.size();
//        }
//        if(TYPE == Question.TYPE_SINGLE){
//            return mMultiQuestionList.size();
//        }
//        if(TYPE == Question.TYPE_SINGLE){
//            return mTrueOrFalseQuestionList.size();
//        }
//        return 0;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//
//
//        if(TYPE == Question.TYPE_SINGLE){
//            //获取mSingleListView
//            container.addView(mSingleListView.get(position),0);
//            //开始操作，装配
//            currentView = mSingleListView.get(position);
//            viewHolder = new ExecriseHolder(currentView);
//
//            return mSingleListView.get(position);
//        }
//        if(TYPE == Question.TYPE_MULTIPLE){
//            //获取mMultiListView
//            container.addView(mSingleListView.get(position),0);
//            viewHolder = new ExecriseHolder(mMultiListView.get(position));
//            return mMultiListView.get(position);
//        }
//        if(TYPE == Question.TYPE_TRUE_FALSE){
//            //获取mTrueOrfalseListView
//            container.addView(mSingleListView.get(position),0);
//            viewHolder = new ExecriseHolder(mTrueOrFalseListView.get(position));
//            return mTrueOrFalseQuestionList.get(position);
//        }
//        return null;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }
//
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//
//        if(TYPE == Question.TYPE_SINGLE){
//            container.removeView(mSingleListView.get(position));
//        }
//        if(TYPE == Question.TYPE_SINGLE){
//            container.removeView(mMultiListView.get(position));
//        }
//        if(TYPE == Question.TYPE_SINGLE){
//            container.removeView(mTrueOrFalseListView.get(position));
//        }
//    }
//
//
//
//
//
//    /**
//     * 答案解析显示控制
//     *
//     * @param question
//     * @param showAnswer
//     */
//    private void dealWithAnswerText(Question question, boolean showAnswer) {
//
//        question.showAnswer = showAnswer;
//
//        if (showAnswer) {
//            viewHolder.ensureBtnTV.setVisibility(View.GONE);
//
//            if (question.getState() == Question.STATE3_RIGHT) {
//                viewHolder.answerBtnTV.setBackground(mContext.getResources().getDrawable(R.drawable.answer_rigth));
//            } else if (question.getState() == Question.STATE3_WRONG) {
//                viewHolder.answerBtnTV.setBackground(mContext.getResources().getDrawable(R.drawable.answer_error));
//            }
//
//            viewHolder.answerBtnTV.setVisibility(View.VISIBLE);
//            viewHolder.answerLL.setVisibility(View.VISIBLE);
//            viewHolder.answerTV.setText("参考答案：" + question.getAnswer());
//            viewHolder.explainTV.setText("题目解析：" + (question.getExplain().isEmpty() ? "略。" : question.getExplain()));
//
//        } else {
//            //多选题，不显示答案就显示确定按钮
//            if (question.getType() == Question.TYPE_MULTIPLE) {
//                viewHolder.ensureBtnTV.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.ensureBtnTV.setVisibility(View.GONE);
//            }
//
//            viewHolder.answerLL.setVisibility(View.GONE);
//            viewHolder.answerTV.setText("");
//            viewHolder.explainTV.setText("");
//        }
//    }
//
//
//    /**
//     * 校验答案，并控制确定按钮、答案状态按钮及答案解析  隐藏或显示
//     *
//     * @param question
//     * @param showAnswer 是否显示答案
//     */
//    private void checkAndShowAnswer(Question question, boolean showAnswer) {
//
//        if (question.selectedToString().length() == 0) {
//            dealWithAnswerText(question, false);
//            Toast.makeText(activityCtx, "请选择", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (question.selectedToString().equalsIgnoreCase(question.getAnswer())) {
//            question.showAnswer = true;
//            question.setState(Question.STATE3_RIGHT);
//            //答对了，自动跳转到下一题
//            dealWithAnswerText(question, showAnswer);
//            //自动跳到下一题
//            //goForward();
//        } else {
////            curQuestion.setState(Question.STATE3_WRONG);
//            dealWithAnswerText(question, showAnswer);
//        }
//
//        if (showAnswer) {
////            mHandler.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    viewHolder.contentSV.fullScroll(ScrollView.FOCUS_DOWN);
////                }
////            }, 200);
//        }
//    }
//
//
//    /**
//     * 选项处理，并保存
//     *
//     * @param view
//     */
//    private void dealWithSelection(View view) {
//        //如果是单选，把非点中项设为不选中
//        int childCount = viewHolder.optionsLL.getChildCount();
//
//        for (int i = 0; i < childCount; i++) {
//            View optionView = viewHolder.optionsLL.getChildAt(i);
//
//            //如果当前没被选中，且不是多选，设为不选中
//            if (view.getId() != optionView.getId() && TYPE != Question.TYPE_MULTIPLE) {
//                viewHolder.optionsLL.getChildAt(i).setSelected(false);
//                mAnswers[i] = "";
//                //否则，被点击项状态反转
//            } else if (view.getId() == optionView.getId()) {
//                boolean isSelected = optionView.isSelected();
//                optionView.setSelected(!isSelected);
//
//                if (!isSelected) {
////                    mAnswers[i] = GadgetUtil.getOptionChar(i);
//                } else {
////                    mAnswers[i] = "";
//                }
//            }
//        }
//
//        if (curQuestion.getType() == Question.TYPE_MULTIPLE) {
//            dealWithAnswerText(curQuestion, false);
//        } else {
//            checkAndShowAnswer(curQuestion, true);
//        }
//    }
//
//    private void operate(){
//
//    }
//
//    class ExecriseHolder{
//
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
//
//        public ExecriseHolder(View v){
//           findviews(v);
//        }
//
//        private void findviews(View view){
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
//        }
//    }
//
//}

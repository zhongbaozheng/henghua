package com.hengdian.henghua.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.fragment.ExerciseContentFragment;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.GadgetUtil;

import java.util.List;

/**
 * Created by admin on 2017-01-20.
 */

public class ExerciseNavCardGvAdapter extends BaseAdapter {
    public Context ctx;
    public List<Question> mQuestionList;
    private ExerciseContentFragment exerciseContentFrag;
    private int questionIndex = 0;


    public ExerciseNavCardGvAdapter(Context ctx, List<Question> questionList) {
        this.ctx = ctx;
        this.mQuestionList = questionList;
    }

    @Override
    public int getCount() {
        return mQuestionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mQuestionList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.nav_card_gv_item, null);
            convertView.setTag(viewHolder = new ViewHolder(convertView, false));
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //找到试题Fragment
        //android.support.v4.app.Fragment
        if (exerciseContentFrag == null) {
            exerciseContentFrag = (ExerciseContentFragment) ((ContentActivity) ctx).
                    getSupportFragmentManager().findFragmentByTag(Constant.FragTag.TEST_EXERCISE_CONTENT);
        }

        Question question = mQuestionList.get(i);
        int questionState = question.getState();
        int questionType = question.getType();

        if (questionType == Question.TYPE_SINGLE) {
            questionIndex = i + 1;
        } else if (questionType == Question.TYPE_MULTIPLE) {
//            questionIndex = i + exerciseContentFrag.sizeSingle + 1;
            questionIndex = i + 1;
        } else if (questionType == Question.TYPE_TRUE_FALSE) {
            questionIndex = i + 1;
//            questionIndex = i + exerciseContentFrag.sizeSingle + exerciseContentFrag.sizeMultiple + 1;
        }

        String questionNum = GadgetUtil.formatItemNum(questionIndex);
        if (questionState == Question.STATE0_DEFAULT) {
            viewHolder.navCardNum.setTextColor(Color.BLACK);
        } else if (questionState == Question.STATE1_DOING) {
            viewHolder.navCardNum.setTextColor(Color.BLUE);
        } else if (questionState == Question.STATE2_ANSWERED) {
            viewHolder.navCardNum.setTextColor(Color.GRAY);
        } else if (questionState == Question.STATE3_WRONG) {
            viewHolder.navCardNum.setTextColor(Color.RED);
        } else if (questionState == Question.STATE3_RIGHT) {
            viewHolder.navCardNum.setTextColor(Color.GREEN);
        }

        if (question.isShow) {
            viewHolder.navCardNum.setBackgroundResource(R.color.app_main);
        }

        viewHolder.navCardNum.setText(questionNum);

        final int index = i;
        viewHolder.navCardNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (exerciseContentFrag == null) {
                    return;
                }

                //设置答题卡 状态
                exerciseContentFrag.showNavCard(false);
                //回顾试题按钮状态
                exerciseContentFrag.viewHolder.centerButtonLL.setSelected(false);

                //设置当前的题号，并刷新
                switch (mQuestionList.get(index).getType()) {
                    case Question.TYPE_SINGLE:
//                        exerciseContentFrag.curIndex1 = index;
                        exerciseContentFrag.curQuestionType = Question.TYPE_SINGLE;
                        exerciseContentFrag.curIndexSingle = index;

                        break;

                    case Question.TYPE_MULTIPLE:
//                        exerciseContentFrag.curIndex1 = index + exerciseContentFrag.sizeSingle;

                        exerciseContentFrag.curQuestionType = Question.TYPE_MULTIPLE;
                        exerciseContentFrag.curIndexMultiple = index;
                        break;

                    case Question.TYPE_TRUE_FALSE:
//                        exerciseContentFrag.curIndex1 = index + exerciseContentFrag.sizeSingle + exerciseContentFrag.sizeMultiple;

                        exerciseContentFrag.curQuestionType = Question.TYPE_TRUE_FALSE;
                        exerciseContentFrag.curIndexTrueFalse = index;

                        break;
                }

                exerciseContentFrag.dealWithIndex();

            }
        });
        return convertView;
    }

    static class ViewHolder extends ViewViewHolder {

        TextView navCardNum;

        public ViewHolder(View view, boolean refind) {
            super(view, refind);
        }


        protected void findViews() {
            navCardNum = $(R.id.navCard_num_tv);
        }
    }

}

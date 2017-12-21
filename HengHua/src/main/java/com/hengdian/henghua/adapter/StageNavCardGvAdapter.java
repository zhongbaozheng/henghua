package com.hengdian.henghua.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.fragment.StageContentFragment;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.GadgetUtil;

import java.util.List;

/**
 * Created by admin on 2017-01-20.
 */

public class StageNavCardGvAdapter extends BaseAdapter {
    public Context ctx;
    public List<Question> mQuestionList;
    private StageContentFragment stageContentFrag;
    private int questionIndex = 0;


    public StageNavCardGvAdapter(Context ctx, List<Question> questionList) {
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
        if (stageContentFrag == null) {
            stageContentFrag = (StageContentFragment) ((ContentActivity) ctx).
                    getSupportFragmentManager().findFragmentByTag(Constant.FragTag.TEST_STAGE_CONTENT);
        }

        Question question = mQuestionList.get(i);
        int questionState = question.getState();
        int questionType = question.getType();

        if (questionType == Question.TYPE_SINGLE) {
            questionIndex = i + 1;
        } else if (questionType == Question.TYPE_MULTIPLE) {
            questionIndex = i + stageContentFrag.sizeSingle + 1;
        } else if (questionType == Question.TYPE_TRUE_FALSE) {
            questionIndex = i + stageContentFrag.sizeSingle + stageContentFrag.sizeMultiple + 1;
        }


        String questionNum = GadgetUtil.formatItemNum(questionIndex);
        if (questionState == Question.STATE0_DEFAULT) {
            viewHolder.navCardNum.setTextColor(Color.GRAY);

        } else if (questionState >= Question.STATE2_ANSWERED) {
            viewHolder.navCardNum.setTextColor(Color.DKGRAY);
        }

        if (question.isShow) {
            viewHolder.navCardNum.setBackgroundResource(R.color.app_main);
        }

        viewHolder.navCardNum.setText(questionNum);

        final int index = i;
        viewHolder.navCardNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stageContentFrag == null) {
                    return;
                }

                int indexTmp = 0;
                //设置当前的题号，并刷新
                switch (mQuestionList.get(index).getType()) {
                    case Question.TYPE_SINGLE:
                        indexTmp = stageContentFrag.curIndex1;
                        break;

                    case Question.TYPE_MULTIPLE:
                        indexTmp = stageContentFrag.curIndex1 - stageContentFrag.sizeSingle;
                        break;

                    case Question.TYPE_TRUE_FALSE:
                        indexTmp = stageContentFrag.curIndex1 - stageContentFrag.sizeSingle - stageContentFrag.sizeMultiple;
                        break;
                }

                int tmpIndex = index > 0 ? index - 1 : 0;
                if (mQuestionList.get(tmpIndex).getState() < Question.STATE2_ANSWERED && index != 0) {
                    ToastUtil.toastMsgShort("请先解答前面的试题");
                    return;
                }

                //设置答题卡 状态
                stageContentFrag.showNavCard(false);
                //回顾试题按钮状态
                stageContentFrag.viewHolder.centerButtonLL.setSelected(false);

                //设置当前的题号，并刷新
                switch (mQuestionList.get(index).getType()) {
                    case Question.TYPE_SINGLE:
                        stageContentFrag.curIndex1 = index;
                        break;

                    case Question.TYPE_MULTIPLE:
                        stageContentFrag.curIndex1 = index + stageContentFrag.sizeSingle;
                        break;

                    case Question.TYPE_TRUE_FALSE:
                        stageContentFrag.curIndex1 = index + stageContentFrag.sizeSingle + stageContentFrag.sizeMultiple;
                        break;
                }

                stageContentFrag.dealWithIndex(stageContentFrag.curIndex1);

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

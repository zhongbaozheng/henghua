package com.hengdian.henghua.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.model.contentDataModel.Book;
import com.hengdian.henghua.utils.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by Anderok on 2017/2/15.
 */
public class ExamListAdapter extends SimpleAdapter {
    Context context;
    List<? extends Map<String, ?>> data;

    public static final String[] key = {"bookNameTV", "countDoTV", "countTotalTV", "examStatusBtnTV"};
    public static final int[] viewId = {R.id.bookName_tv, R.id.countDo_tv, R.id.countTotal_tv, R.id.examStatusBtn_tv,};

    View.OnClickListener listenerDo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();

            String bookID = data.get(position).get(Book.KEY_BOOK_ID) + "";

            Intent intent = new Intent();
            intent.setClass(context, ContentActivity.class);
            //传递当前教材的id
            Bundle bundle = new Bundle();
            bundle.putString(Constant.Jump.FROM_TAG, Constant.FragTag.TEST_EXAM);
            bundle.putString(Constant.Jump.BOOK_ID, bookID);

            intent.putExtras(bundle);
            ((MainActivity) context).startActivityForResult(intent, Constant.RequestResultCode.FROM_TEST_EXAM);

        }
    };

    View.OnClickListener listenerDone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToastUtil.toastMsgShort("已经考过了哦！");

        }
    };

    public static final String START = "去考试吧";
    public static final String CONTINUE = "继续考试";
    public static final String DONE = "已经考过";


    /**
     * 根据状态获取按钮文字
     *
     * @param statusFlag
     * @return
     */
    public static String getBtnText(int statusFlag) {
        if (statusFlag == Book.STATE1_CONTINUE) {
            return CONTINUE;
        } else if (statusFlag == Book.STATE2_ALL_DONE) {
            return DONE;
        } else {
            return START;
        }
    }

    public static final String SCORE_KEY = "成绩: ";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        convertView = super.getView(position, convertView, parent);
        if(convertView.getTag()==null){
            viewHolder = new ViewHolder(convertView,false);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        viewHolder.examStatusBtnTV.setTag(position);

        if (((String) data.get(position).get(key[3])).indexOf(SCORE_KEY) >= 0) {
            viewHolder.examStatusBtnTV.setBackgroundResource(R.drawable.button_bg_0);
            viewHolder.examStatusBtnTV.setOnClickListener(listenerDone);
//            examStatusBtnTV.setTextColor(context.getResources().getColor(R.color.app_main));
        } else {
            viewHolder.examStatusBtnTV.setOnClickListener(listenerDo);
            viewHolder.examStatusBtnTV.setBackgroundResource(R.drawable.blue_button_slt);
        }

        return convertView;

    }


    public ExamListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] key, int[] id) {
        super(context, data, resource, key, id);
        this.data = data;
        this.context = context;
    }


    static class ViewHolder extends ViewViewHolder {

//        RelativeLayout itemRootRL;
//        RelativeLayout itemRL;
//
//        TextView bookNameTV;
//        TextView countDoTV;
//        TextView countTotalTV;

        //TextView manTimeTV;
        TextView examStatusBtnTV;
        //TextView scoreTV;


        public ViewHolder(View view, boolean refind) {
            super(view, refind);
        }

        protected void findViews() {
//            itemRootRL = $(R.id.item_root_rl);
//            itemRL = $(R.id.item_rl);
//
//            bookNameTV = $(R.id.bookName_tv);
//            countDoTV = $(R.id.countDo_tv);
//            countTotalTV = $(R.id.countTotal_tv);

            //manTimeTV = $(R.id.manTime_tv);
            examStatusBtnTV = $(R.id.examStatusBtn_tv);
            //scoreTV = $(R.id.score_tv);
        }
    }
}

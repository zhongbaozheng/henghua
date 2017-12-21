package com.hengdian.henghua.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.model.contentDataModel.Chapter;
import com.hengdian.henghua.utils.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by Anderok on 2017/2/15.
 */
public class StageListAdapter extends SimpleAdapter {
    Context ctx;
    List<? extends Map<String, ?>> data;

    public static final String[] key = {"stageNumTv", "stageTitleTv"};
    public static final int[] viewId = {R.id.stage_num_tv, R.id.stage_title_tv};

    public static final String STATUS_KEY = "status";
    public static final String STATUS_LOCKED = "locked";
    public static final String STATUS_UNLOCKED = "unlocked";
    public static final String STATUS_PASSED = "passed";


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();

            if ((Integer) data.get(position).get(STATUS_KEY) == Chapter.STATE0_DEFAULT) {
                ToastUtil.toastMsgShort("请先通过前面的关卡哦！");
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString(Constant.Jump.FROM_TAG, Constant.FragTag.TEST_STAGE);
            bundle.putString(Constant.Jump.BOOK_ID, (String) data.get(position).get(Constant.Jump.BOOK_ID));
            bundle.putString(Constant.Jump.CHAPTER_ID, (String) data.get(position).get(Constant.Jump.CHAPTER_ID));

            LogUtil.i("StageListAdapter", "bookID:" + data.get(position).get(Constant.Jump.BOOK_ID) + ",chapterID:" + data.get(position).get(Constant.Jump.CHAPTER_ID));

            Intent intent = new Intent();
            intent.setClass(ctx, ContentActivity.class);
            intent.putExtras(bundle);

            ((MainActivity) ctx).startActivityForResult(intent, Constant.RequestResultCode.FROM_TEST_STAGE);
        }
    };


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

        viewHolder.stageBgRL.setTag(position);

        switch ((Integer) data.get(position).get(STATUS_KEY)) {
            case Chapter.STATE0_DEFAULT:
                viewHolder.stageBgRL.setBackgroundResource(R.drawable.test_brkth_locked);
                viewHolder.stageNumTv.setTextColor(Color.parseColor("#c9c8c8"));
                viewHolder.stageTitleTv.setTextColor(Color.parseColor("#c9c8c8"));

                break;
            case Chapter.STATE1_UNLOCKED:
                viewHolder.stageBgRL.setBackgroundResource(R.drawable.test_brkth_unlocked);
                viewHolder.stageNumTv.setTextColor(Color.parseColor("#e7e6e6"));
                viewHolder.stageTitleTv.setTextColor(Color.parseColor("#e7e6e6"));
                break;
            case Chapter.STATE2_PASSED:
                viewHolder.stageBgRL.setBackgroundResource(R.drawable.test_brkth_done);
                viewHolder.stageNumTv.setTextColor(Color.parseColor("#ffffff"));
                viewHolder.stageTitleTv.setTextColor(Color.parseColor("#ffffff"));
                break;
        }
        return convertView;

    }

    public StageListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] key, int[] id) {
        super(context, data, resource, key, id);
        this.data = data;
        this.ctx = context;
    }


    class ViewHolder extends ViewViewHolder {

        // LinearLayout stageRootLL;
        RelativeLayout stageBgRL;
        TextView stageNumTv;
        TextView stageTitleTv;


        public ViewHolder(View view, boolean refind) {
            super(view, refind);
        }

        protected void findViews() {

//          stageRootLL = $(R.id.stage_root_ll);
            stageBgRL = $(R.id.stage_bg_rl);
            stageBgRL.setOnClickListener(listener);

            stageNumTv = $(R.id.stage_num_tv);
            stageTitleTv = $(R.id.stage_title_tv);
        }
    }
}

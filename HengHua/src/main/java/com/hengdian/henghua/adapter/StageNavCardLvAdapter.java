package com.hengdian.henghua.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.widget.MyGridView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017-01-20.
 */

public class StageNavCardLvAdapter extends BaseAdapter {
    public Context ctx;
    public List<Question> singleList;
    public List<Question> multipleList;
    public List<Question> trueOrFalseList;
    public List<List> mList;
    public StageNavCardGvAdapter stageNavCardGvAdapter;


    public StageNavCardLvAdapter(Context ctx, List<Question> singleList, List<Question> multipleList, List<Question> trueOrFalseList) {
        this.ctx = ctx;
        this.singleList = singleList;
        this.multipleList = multipleList;
        this.trueOrFalseList = trueOrFalseList;
        //list数据 长度
        mList = new ArrayList<List>();
        if (singleList != null) {
            mList.add(singleList);
        }
        if (multipleList != null) {
            mList.add(multipleList);
        }
        if (trueOrFalseList != null) {
            mList.add(trueOrFalseList);
        }

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public List<String> getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(ctx, R.layout.nav_card_lv_item, null);
        ViewHolder viewHolder = new ViewHolder(convertView, false);

        if (position == 0) {
            if (singleList == null || singleList.size() == 0) {
                viewHolder.questionTypeTitle.setVisibility(View.GONE);
            } else {
                viewHolder.questionTypeTitle.setVisibility(View.VISIBLE);
                viewHolder.questionTypeTitle.setText("单选题");

                stageNavCardGvAdapter = new StageNavCardGvAdapter(ctx, singleList);
                setGridViewHeightBasedOnChildren(viewHolder.navCardGridView);
                viewHolder.navCardGridView.setAdapter(stageNavCardGvAdapter);
            }
        }

        if (position == 1) {
            if (multipleList == null || multipleList.size() == 0) {
                viewHolder.questionTypeTitle.setVisibility(View.GONE);
            } else {
                viewHolder.questionTypeTitle.setVisibility(View.VISIBLE);
                viewHolder.questionTypeTitle.setText("多选题");

                StageNavCardGvAdapter stageNavCardGvAdapter = new StageNavCardGvAdapter(ctx, multipleList);
                setGridViewHeightBasedOnChildren(viewHolder.navCardGridView);
                viewHolder.navCardGridView.setAdapter(stageNavCardGvAdapter);
            }
        }
        if (position == 2) {
            if (trueOrFalseList == null || trueOrFalseList.size() == 0) {
                viewHolder.questionTypeTitle.setVisibility(View.GONE);
            } else {
                viewHolder.questionTypeTitle.setVisibility(View.VISIBLE);
                viewHolder.questionTypeTitle.setText("判断题");

                StageNavCardGvAdapter stageNavCardGvAdapter = new StageNavCardGvAdapter(ctx, trueOrFalseList);
//                setGridViewHeightBasedOnChildren(viewHolder.navCardGridView); //去掉这行代码
                viewHolder.navCardGridView.setAdapter(stageNavCardGvAdapter);
            }
        }

        return convertView;
    }

    /**
     * 计算 子（gridView）的宽高
     *
     * @param gridView
     */

    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int rows;
        int columns = 0;
        int horizontalBorderHeight = 0;
        Class<?> clazz = gridView.getClass();
        try {
            // 利用反射，取得每行显示的个数
            Field column = clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns = (Integer) column.get(gridView);
            // 利用反射，取得横向分割线高度
            Field horizontalSpacing = clazz
                    .getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        // 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
        if (listAdapter.getCount() % columns > 0) {
            rows = listAdapter.getCount() / columns + 1;
        } else {
            rows = listAdapter.getCount() / columns;
        }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + horizontalBorderHeight * (rows - 1);// 最后加上分割线总高度
        gridView.setLayoutParams(params);
    }

    static class ViewHolder extends ViewViewHolder {

        TextView questionTypeTitle;
        MyGridView navCardGridView;

        public ViewHolder(View view, boolean refind) {
            super(view, refind);
        }

        protected void findViews() {
            questionTypeTitle = $(R.id.navCardQuestionType_tv);
            navCardGridView = $(R.id.questionIndex_gv);
            navCardGridView.setClickable(true);
        }
    }
}

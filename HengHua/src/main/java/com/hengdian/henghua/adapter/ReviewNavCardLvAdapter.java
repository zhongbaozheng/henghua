package com.hengdian.henghua.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.model.contentDataModel.ChapterContent;
import com.hengdian.henghua.widget.MyGridView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017-01-20.
 */

public class ReviewNavCardLvAdapter extends BaseAdapter {
    public Context ctx;
    public List<ChapterContent> contentList;
    public List<List> mList;


    public ReviewNavCardLvAdapter(Context ctx, List<ChapterContent> contentList) {
        this.ctx = ctx;
        this.contentList = contentList;
        //list数据 长度，以后可以改，我认为这样的逻辑不太好
        mList = new ArrayList<List>();
        if (contentList != null) {
            mList.clear();
            mList.add(contentList);
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

    public ReviewNavCardGvAdapter reviewNavCardGvAdapter;
    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(ctx, R.layout.nav_card_lv_item, null);
        viewHolder = new ViewHolder(convertView);
        if (position == 0) {
            if (contentList == null || contentList.size() == 0) {
                viewHolder.questionTypeTitle.setVisibility(View.GONE);
            } else {
                reviewNavCardGvAdapter = new ReviewNavCardGvAdapter(ctx, contentList);
                viewHolder.questionTypeTitle.setVisibility(View.VISIBLE);
                viewHolder.questionTypeTitle.setText("内容编号");
//                setGridViewHeightBasedOnChildren(viewHolder.navCardGridView); //这行代码也去掉
                viewHolder.navCardGridView.setAdapter(reviewNavCardGvAdapter);
            }
        }



        return convertView;
    }

    /**
     * 计算 子（gridView）的宽高
     *
     * @param gridView
     */

    public void setGridViewHeightBasedOnChildren(GridView gridView) {
        // 获取GridView对应的Adapter
//        ListAdapter listAdapter = gridView.getAdapter();
        ListAdapter listAdapter = viewHolder.navCardGridView.getAdapter();
//        Log.e("ListAdapter",listAdapter.isEmpty()+"");
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
//        Log.e("listAdapter.getCount()",listAdapter.getCount()+"");
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


    public class ViewHolder {

        TextView questionTypeTitle ;
        MyGridView navCardGridView;

        public ViewHolder(View view) {
            findViews(view);
        }


        protected void findViews(View view) {
            questionTypeTitle = (TextView) view.findViewById(R.id.navCardQuestionType_tv);
            navCardGridView = (MyGridView)view.findViewById (R.id.questionIndex_gv);
            navCardGridView.setClickable(true);
        }
    }


//    static class ViewHolder extends ViewViewHolder {
//
//        TextView questionTypeTitle ;
//        MyGridView navCardGridView;
//
//        public ViewHolder(View view, boolean refind) {
//            super(view, refind);
//        }
//
//
//        protected void findViews() {
//            questionTypeTitle = $(R.id.navCardQuestionType_tv);
//            navCardGridView = $(R.id.questionIndex_gv);
//            navCardGridView.setClickable(true);
//        }
//    }

}

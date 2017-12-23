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
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.fragment.ExerciseContentFragment;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.widget.MyGridView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017-01-20.
 */

public class ExerciseNavCardLvAdapter extends BaseAdapter {
    public Context ctx;
    private ExerciseContentFragment fragment;
    public List<Question> singleList;
    public List<Question> multipleList;
    public List<Question> trueOrFalseList;
    public List<List> mList;
    public ExerciseNavCardGvAdapter exerciseNavCardGvAdapter;


    public ExerciseNavCardLvAdapter(Context ctx, ExerciseContentFragment fragment, List<Question> singleList, List<Question> multipleList, List<Question> trueOrFalseList) {
        this.ctx = ctx;
        this.fragment = fragment;
        this.singleList = singleList;
        this.multipleList = multipleList;
        this.trueOrFalseList = trueOrFalseList;
        //list数据 长度
        mList = new ArrayList<List>();
        if (fragment.curQuestionType == Question.TYPE_SINGLE && singleList != null) {
            mList.add(singleList);
        } else if (fragment.curQuestionType == Question.TYPE_MULTIPLE && multipleList != null) {
            mList.add(multipleList);
        } else if (fragment.curQuestionType == Question.TYPE_TRUE_FALSE && trueOrFalseList != null) {
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

            switch (fragment.curQuestionType) {
                case Question.TYPE_SINGLE:

                    if (singleList == null || singleList.size() == 0) {
                        viewHolder.questionTypeTitle.setVisibility(View.GONE);
                    } else {
                        viewHolder.questionTypeTitle.setText("单选题");
                        viewHolder.questionTypeTitle.setVisibility(View.VISIBLE);

                         exerciseNavCardGvAdapter = new ExerciseNavCardGvAdapter(ctx, singleList);
//                        setGridViewHeightBasedOnChildren(viewHolder.navCardGridView); //去掉这一行代码
                        viewHolder.navCardGridView.setAdapter(exerciseNavCardGvAdapter);
                    }

                    break;
                case Question.TYPE_MULTIPLE:
                    if (multipleList == null || multipleList.size() == 0) {
                        viewHolder.questionTypeTitle.setVisibility(View.GONE);
                    } else {
                        viewHolder.questionTypeTitle.setText("多选题");
                        viewHolder.questionTypeTitle.setVisibility(View.VISIBLE);

                        exerciseNavCardGvAdapter = new ExerciseNavCardGvAdapter(ctx, multipleList);
                        setGridViewHeightBasedOnChildren(viewHolder.navCardGridView);
                        viewHolder.navCardGridView.setAdapter(exerciseNavCardGvAdapter);
                    }

                    break;
                case Question.TYPE_TRUE_FALSE:
                    if (trueOrFalseList == null || trueOrFalseList.size() == 0) {
                        viewHolder.questionTypeTitle.setVisibility(View.GONE);
                    } else {
                        viewHolder.questionTypeTitle.setText("判断题");
                        viewHolder.questionTypeTitle.setVisibility(View.VISIBLE);

                        exerciseNavCardGvAdapter = new ExerciseNavCardGvAdapter(ctx, trueOrFalseList);
                        setGridViewHeightBasedOnChildren(viewHolder.navCardGridView);
                        viewHolder.navCardGridView.setAdapter(exerciseNavCardGvAdapter);
                    }

                    break;
            }


        }


//        if (position == 0) {
//            if (singleList == null || singleList.size() == 0) {
//                questionTypeTitle.setVisibility(View.GONE);
//            } else {
//                questionTypeTitle.setVisibility(View.VISIBLE);
//                questionTypeTitle.setText("单选题");
//
//                ExerciseNavCardGvAdapter exerciseNavCardGvAdapter = new ExerciseNavCardGvAdapter(ctx, singleList);
//                setGridViewHeightBasedOnChildren(navCardGridView);
//                navCardGridView.setAdapter(exerciseNavCardGvAdapter);
//            }
//        }
//
//        if (position == 1) {
//            if (multipleList == null || multipleList.size() == 0) {
//                questionTypeTitle.setVisibility(View.GONE);
//            } else {
//                questionTypeTitle.setVisibility(View.VISIBLE);
//                questionTypeTitle.setText("多选题");
//
//                ExerciseNavCardGvAdapter exerciseNavCardGvAdapter = new ExerciseNavCardGvAdapter(ctx, multipleList);
//                setGridViewHeightBasedOnChildren(navCardGridView);
//                navCardGridView.setAdapter(exerciseNavCardGvAdapter);
//            }
//        }
//        if (position == 2) {
//            if (trueOrFalseList == null || trueOrFalseList.size() == 0) {
//                questionTypeTitle.setVisibility(View.GONE);
//            } else {
//                questionTypeTitle.setVisibility(View.VISIBLE);
//                questionTypeTitle.setText("判断题");
//
//                ExerciseNavCardGvAdapter exerciseNavCardGvAdapter = new ExerciseNavCardGvAdapter(ctx, trueOrFalseList);
//                setGridViewHeightBasedOnChildren(navCardGridView);
//                navCardGridView.setAdapter(exerciseNavCardGvAdapter);
//            }
//        }

//        int w = View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.AT_MOST);
//        int h = View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.AT_MOST);
////        ll_what.measure(w, h);
////        int width = ll_what.getMeasuredWidth();
////        ll_what.setPadding(UIUtil.dip2px(width) / 4 - UIUtil.dip2px(5), 0, 0, 0);
//
//        navCardGridView.measure(w, h);
//        int measuredHeight = view.getMeasuredHeight();
//        int measuredWidth = view.getMeasuredWidth();


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

        TextView questionTypeTitle ;
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

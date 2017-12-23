package com.hengdian.henghua.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hengdian.henghua.model.contentDataModel.Question;

import java.util.List;

/**
 * Created by micro on 2017/12/22.
 */

public class StageScrollAdapter extends PagerAdapter {

    private List<Question> mQuestionList;
    private Context mContext;

    private List<View>  mListView;

    public StageScrollAdapter(Context context,
                                      List<View> listView,
                                      List<Question> questionList){
        mContext = context;
        mListView = listView;
        mQuestionList = questionList;
    }


    @Override
    public int getCount() {

        return mQuestionList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(mListView.get(position),0);
        return mListView.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(mListView.get(position));
    }
}

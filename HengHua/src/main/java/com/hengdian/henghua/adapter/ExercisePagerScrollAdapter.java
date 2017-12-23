package com.hengdian.henghua.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hengdian.henghua.R;
import com.hengdian.henghua.model.contentDataModel.Question;
import com.hengdian.henghua.utils.GadgetUtil;

import java.util.List;

/**
 * Created by micro on 2017/12/20.
 */

public class ExercisePagerScrollAdapter extends PagerAdapter{

    private List<Question> mQuestionList;
    private Context mContext;

    private int TYPE;

    private List<View>  mListView;

    public ExercisePagerScrollAdapter(Context context,
                                      List<View> listView,
                                      List<Question> questionList,int type){
        mContext = context;
        mListView = listView;
        mQuestionList = questionList;
        TYPE = type;
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

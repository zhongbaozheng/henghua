package com.hengdian.henghua.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.model.contentDataModel.ChapterContent;
import com.hengdian.henghua.model.contentDataModel.Question;

import java.util.List;

/**
 * Created by micro on 2017/12/19.
 */

public class PagerScrollAdapter extends PagerAdapter {

    private Context mContext;
    private List<View>  mListView;
    private List<ChapterContent> mChapterContentList;

    public PagerScrollAdapter(Context context, List<ChapterContent> chapterContentList,List<View> listView){
        mContext = context;
        mChapterContentList = chapterContentList;
        mListView = listView;
    }

    @Override
    public int getCount() {
        return mChapterContentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //移除view
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(mListView.get(position));
    }
    //初始化view
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListView.get(position),0);
         TextView item_tv = (TextView)container.findViewById(R.id.itemNum_tv);
        item_tv.setText(""+(position+1));
        TextView itemTitleTV = (TextView) container.findViewById(R.id.itemTitle_tv);
        itemTitleTV.setText(mChapterContentList.get(position).getContentTile());
        TextView contentTV = (TextView) container.findViewById(R.id.content_tv);
        contentTV.setText(mChapterContentList.get(position).getContent());
        return mListView.get(position);

    }
}

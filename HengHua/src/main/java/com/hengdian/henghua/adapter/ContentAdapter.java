package com.hengdian.henghua.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.hengdian.henghua.model.contentDataModel.ChapterContent;

import java.util.List;

/**
 * Created by micro on 2017/12/12.
 */

public class ContentAdapter extends PagerAdapter {

    private List<ChapterContent> chapterContentList;
    private Context mContext;

    public ContentAdapter(List<ChapterContent> chapterContentList,Context context) {
        this.chapterContentList = chapterContentList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }

    @Override
    public Object instantiateItem(View container, int position) {
//        int pos = position % showViews.size();
//        View view = showViews.get(pos);
//        ViewGroup parent = (ViewGroup) view.getParent();
//        if (parent != null) {
//            parent.removeView(view);
//        }
//        ((ViewPager) container).addView(view);
//        return view;
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}

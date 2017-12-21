package com.hengdian.henghua.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.fragment.ReviewContentFragment;
import com.hengdian.henghua.model.contentDataModel.ChapterContent;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.GadgetUtil;

import java.util.List;

/**
 * Created by admin on 2017-01-20.
 */

public class ReviewNavCardGvAdapter extends BaseAdapter {
    public Context ctx;
    public List<ChapterContent> contentList;
    private ReviewContentFragment reviewContentFrag;

    public ReviewNavCardGvAdapter(Context ctx, List<ChapterContent> contentList) {
        this.ctx = ctx;
        this.contentList = contentList;
    }

    @Override
    public int getCount() {
//        Log.e("ReviewNavCardGvAdapter",contentList.size()+"");
        return contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return contentList.get(position);
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
            viewHolder = new ViewHolder(convertView,false);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //找到对应Fragment
        //android.support.v4.app.Fragment
        if (reviewContentFrag == null) {
            reviewContentFrag = (ReviewContentFragment) ((ContentActivity) ctx).
                    getSupportFragmentManager().findFragmentByTag(Constant.FragTag.REVIEW_CONTENT);
        }

        ChapterContent content = contentList.get(i);
        int contentState = content.getState();


        if (contentState == ChapterContent.STATE0_DEFAULT) {
            viewHolder.navCardNum.setTextColor(Color.BLACK);
        } else if (contentState == ChapterContent.STATE1_READING) {
            viewHolder.navCardNum.setTextColor(Color.GRAY);
        } else if (contentState == ChapterContent.STATE2_HAS_READ) {
            viewHolder.navCardNum.setTextColor(Color.LTGRAY);
        }

        if (content.isShow) {
            viewHolder.navCardNum.setBackgroundResource(R.color.app_main);
        }

//        String contentNum = GadgetUtil.formatItemNum(content.getContentID());
        String contentNum = GadgetUtil.formatItemNum(i + 1);
        viewHolder.navCardNum.setText(contentNum);

        final int index = i;
        viewHolder.navCardNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reviewContentFrag == null) {
                    return;
                }

                Log.e("index",index+"");
                Log.e("curIndex1", reviewContentFrag.curIndex1+"");
//                reviewContentFrag.curIndex1 = 0;


                reviewContentFrag.curIndex1 = index;

                //设置导航卡 状态
                reviewContentFrag.showNavCard(false);
                //设置导航卡按钮状态
                reviewContentFrag.viewHolder.centerButtonLL.setSelected(false);

                reviewContentFrag.setViewData(reviewContentFrag.data.getChapterContentList(), reviewContentFrag.curIndex1);
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

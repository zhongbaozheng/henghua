package com.hengdian.henghua.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.ViewViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by Anderok on 2017/2/15.
 */
public class CourseListAdapter extends SimpleAdapter {
    Context context;
    List<? extends Map<String, ?>> data;

    static View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToastUtil.toastMsgShort("敬请期待！");
            //点击移除
//            data.remove((int) v.getTag());
//            notifyDataSetChanged();
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
        viewHolder.courseListItemRL.setTag(position);

        return convertView;
    }


    public CourseListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] key, int[] id) {
        super(context, data, resource, key, id);
        this.data = data;
        this.context = context;
    }


    static class ViewHolder extends ViewViewHolder {

        LinearLayout courseListItemRL;

//        TextView dateTV;
//        TextView yearTV;
//
//        ImageView statusIV;
//
//        LinearLayout courseDetailsLL;
//
//        TextView courseTitleTV;
//        TextView courseDescTV;
//        TextView buyStatusTV;
//        TextView priceTV;

        public ViewHolder(View view, boolean refind) {
            super(view, refind);
        }

        protected void findViews() {
            courseListItemRL = $(R.id.courseListItem_rl);
            courseListItemRL.setOnClickListener(listener);

//            dateTV = $(R.id.date_tv);
//            yearTV = $(R.id.year_tv);
//
//            statusIV = $(R.id.status_iv);
//
//            courseDetailsLL = $(R.id.courseDetails_ll);
//
//            courseTitleTV = $(R.id.courseTitle_tv);
//            courseDescTV = $(R.id.courseDesc_tv);
//            buyStatusTV = $(R.id.buyStatus_tv);
//            priceTV = $(R.id.price_tv);
        }

    }
}

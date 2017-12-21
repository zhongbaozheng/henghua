package com.hengdian.henghua.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.model.viewBean.ItemBean;
import com.hengdian.henghua.viewUtils.ItemDataUtils;

import java.util.List;


public class LeftItemAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ItemBean> mItemBeans;

    public LeftItemAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mItemBeans = ItemDataUtils.getLeftMenuItemBeans();
    }

    public LeftItemAdapter(Context context, List<ItemBean> mItemBeans) {
        mInflater = LayoutInflater.from(context);
        this.mItemBeans = mItemBeans;
    }

    public void setItems(List<ItemBean> mItemBeans) {
        this.mItemBeans = mItemBeans;
        notifyDataSetChanged();
    }

    public void updateItemBean(int index, ItemBean itemBean) {
        mItemBeans.remove(index);
        mItemBeans.add(index, itemBean);
        notifyDataSetChanged();
    }

    public void updateItemBean(int index, String title, boolean isTagVisible) {

        mItemBeans.get(index).setTitle(title);
        mItemBeans.get(index).setTagVisible(isTagVisible);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItemBeans != null ? mItemBeans.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mItemBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.left_menu_item_layout, null);
            viewHolder = new ViewHolder(convertView,false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ItemBean itemBean = mItemBeans.get(position);

        viewHolder.itemTV.setText(itemBean.getTitle());
        viewHolder.itemIV.setImageResource(itemBean.getImg());
        if (itemBean.isTagVisible()) {
            int[] locationOnScreen = new int[2];
            viewHolder.itemIV.getLocationInWindow(locationOnScreen);
            viewHolder.itemBtnIV.setVisibility(View.VISIBLE);

        }else {
            viewHolder.leftMenuItemRL.setClickable(false);
            viewHolder.itemBtnIV.setVisibility(View.INVISIBLE);
        }



        return convertView;
    }


    static class ViewHolder extends ViewViewHolder {

        RelativeLayout leftMenuItemRL;
        ImageView itemIV;
        TextView itemTV;
        ImageView itemBtnIV;

        public ViewHolder(View view, boolean refind) {
            super(view, refind);
        }

        protected void findViews() {
            leftMenuItemRL = $(R.id.left_menu_item_ro);
            itemIV = $(R.id.item_iv);
            itemTV = $(R.id.item_tv);
            itemBtnIV = $(R.id.item_ib);
        }
    }
}

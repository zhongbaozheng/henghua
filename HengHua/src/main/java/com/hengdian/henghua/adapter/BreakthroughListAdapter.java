package com.hengdian.henghua.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.MainActivity;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.fragment.BreakthroughListFragment;
import com.hengdian.henghua.fragment.StageListFragment;
import com.hengdian.henghua.fragment.TestFragment;
import com.hengdian.henghua.utils.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by Anderok on 2017/2/15.
 */
public class BreakthroughListAdapter extends SimpleAdapter {
    Context context;
    List<? extends Map<String, ?>> data;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            LogUtil.i("BreakthroughListAdapter", "跳到关卡！" + v.getTag());

            //找到TestFragment对象进行子Fragment控制
            List<Fragment> fragList = ((MainActivity) context).getSupportFragmentManager().getFragments();
            loop:
            for (int i = 0; i < fragList.size(); i++) {
//
                if (!(fragList.get(i) instanceof TestFragment)) {
                    continue loop;
                }

                FragmentManager childFragManager = fragList.get(i).getChildFragmentManager();
                //FragmentTransaction fragTrans = childFragManager.beginTransaction();
//
                BreakthroughListFragment brkthFrag = (BreakthroughListFragment) childFragManager.findFragmentByTag(Constant.FragTag.TEST_BREAKTHROUGH);
                if (brkthFrag == null) {
                    return;
                }

                FragmentManager childFragManager2 = brkthFrag.getChildFragmentManager();
                FragmentTransaction fragTrans2 = childFragManager2.beginTransaction();
                //fragTrans2.setCustomAnimations(R.anim.enter, R.anim.exit); //fragment动画

                StageListFragment stageListFrag = (StageListFragment) childFragManager2.findFragmentByTag(Constant.FragTag.TEST_STAGE);
                //如果没有闯关模式关卡子fragment,则创建
                if (stageListFrag == null) {
                    stageListFrag = new StageListFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.Jump.FROM_TAG, Constant.FragTag.TEST_STAGE);
                    bundle.putString(Constant.Jump.BOOK_ID, (String) data.get((Integer) v.getTag()).get(Constant.Jump.BOOK_ID));

                    stageListFrag.setArguments(bundle);

                    fragTrans2.add(R.id.subContainer_fl, stageListFrag, Constant.FragTag.TEST_STAGE);
                    fragTrans2.addToBackStack(Constant.FragTag.TEST_STAGE);

                } else {
                    fragTrans2.show(stageListFrag);
                }

                ((MainActivity) context).setSelectedFragment(stageListFrag);
//                    fragTrans2.commit();
                fragTrans2.commitAllowingStateLoss();
            }

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

        viewHolder.itemRootRL.setTag(position);

        return convertView;

    }


    public BreakthroughListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] key, int[] id) {
        super(context, data, resource, key, id);
        this.data = data;
        this.context = context;
    }

   class ViewHolder extends ViewViewHolder {

        RelativeLayout itemRootRL;
        RelativeLayout itemRL;
        //
//        ImageView iconIV;
//        TextView bookNameTV;
        ImageView editorIV;

        public ViewHolder(View view, boolean refind) {
            super(view, refind);
        }

        protected void findViews() {

          itemRootRL = $(R.id.item_root_rl);
            itemRootRL.setOnClickListener(listener);
//            itemRL = $(R.id.item_rl);

//            iconIV = $(R.id.icon_iv);
//            bookNameTV = $(R.id.bookName_tv);
            editorIV = $(R.id.editor_iv);
        }
    }
}

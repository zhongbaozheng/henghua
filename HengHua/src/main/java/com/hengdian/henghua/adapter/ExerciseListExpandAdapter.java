package com.hengdian.henghua.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.activity.ContentActivity;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.UIUtil;
import com.hengdian.henghua.androidUtil.ViewViewHolder;
import com.hengdian.henghua.fragment.ExerciseListFragment;
import com.hengdian.henghua.model.contentDataModel.BookWithChapters;
import com.hengdian.henghua.model.contentDataModel.Chapter;
import com.hengdian.henghua.model.contentDataModel.Rs_CWXX_BooksWithChapters;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.widget.ProgressHorizontal;

/**
 * 练习模式ExpandListView适配器
 */

public class ExerciseListExpandAdapter extends BaseExpandableListAdapter {
    public ExerciseListFragment frag;
    public Context ctx;
    public LayoutInflater layoutInflater;
    private ProgressHorizontal progressHorizontal;

    public Rs_CWXX_BooksWithChapters data;

    public ViewHolder viewHolder;


    //传入数据
    public ExerciseListExpandAdapter(ExerciseListFragment frag, Context ctx, Rs_CWXX_BooksWithChapters data) {
        this.data = data;
        this.frag = frag;
        this.ctx = ctx;

        this.layoutInflater = LayoutInflater.from(ctx);
    }


    @Override
    public int getGroupCount() {
        return data.getBookList().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.getBookList().get(groupPosition).getChapterList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.getBookList().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.getBookList().get(groupPosition).getChapterList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.book_chapter_list_view_item, null);
            viewHolder = new ViewHolder(convertView, false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //配置自定义进度条
        //水平可递增进度条的自定义控件
        progressHorizontal = new ProgressHorizontal(ctx);
//        //让百分比的文字可以显示方法
//        viewHolder.pr_bar.setProgressTextVisible(true);
//        //百分比的文字需要定义成白色
//        progressHorizontal.setProgressTextColor(Color.WHITE);
//        //百分比的文字显示大小
//        progressHorizontal.setProgressTextSize(UIUtils.dip2px(18));
        //定义自定义控件的蓝色前景图片
        progressHorizontal.setProgressResource(R.mipmap.progress_normal);
        //定义其灰色背景图片
        progressHorizontal.setProgressBackgroundResource(R.mipmap.progress_bg);
        //将自定义的进度条放置在帧布局上显示
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        //设置进度 所以章节进度之和

        //书本总长度
        int bookTotal = data.getBookList().get(groupPosition).getTestTotal();
        //书本已读总进度
        int bookAchieved = data.getBookList().get(groupPosition).getTestAchieved();

        progressHorizontal.setProgress((float) bookAchieved / bookTotal);

        viewHolder.progressTV.setText(bookAchieved + "/" + bookTotal);
        viewHolder.progressFL.addView(progressHorizontal, layoutParams);

        //设置每行的标题
        viewHolder.chapterNameTV.setText(data.getBookList().get(groupPosition).getBookName());
        //viewHolder.tv_chapter_name.setTextSize(18);

//        viewHolder.iv_editor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ctx != null) {
//                    Log.i("START", "====come======");
//                    Intent intent = new Intent();
//                    intent.setClass(ctx, ContentActivity.class);
//                    //传递当前教材的id
//                    Bundle bundle = new Bundle();
//                    bundle.putString("ID", mStatusAndBook.getBookList().get(groupPosition).getBookID() + "");
//                    bundle.putString("TYPE", "REVIEW_BOOK");
//                    bundle.putString("FROM","REVIEW");
//                    bundle.putString("BOOKNAME",mStatusAndBook.getBookList().get(groupPosition).getBookName());
//                    intent.putExtras(bundle);
//                    ctx.startActivity(intent);
//                }
//            }
//        });


        //点击的是当前的条目，设置图片
        //点击的是当前的条目，设置图片
        if (isExpanded || getChildrenCount(groupPosition) < 1) {
            //设置展开
            viewHolder.showIV.setImageResource(R.drawable.ext_false_smaller);
        } else {
            //设置收起图标
            viewHolder.showIV.setImageResource(R.drawable.ext_true_smaller);

        }

//        viewHolder.editorIV.setImageResource(R.drawable.ext_point);
        viewHolder.editorIV.setVisibility(View.GONE);

//        if (getChildrenCount(groupPosition) < 1) {
//            convertView.setOnClickListener(CommonListener.emptyToastListener);
//        }else{
//            convertView.setOnClickListener(null);
//        }

        return convertView;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        LogUtil.i("ExerciseListExpandAdapter", "章节视图");

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.book_chapter_list_view_item, null);
            viewHolder = new ViewHolder(convertView, false);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //配置自定义进度条
        //水平可递增进度条的自定义控件
        progressHorizontal = new ProgressHorizontal(ctx);
        progressHorizontal.setProgressResource(R.mipmap.progress_normal);
        //定义其灰色背景图片
        progressHorizontal.setProgressBackgroundResource(R.mipmap.progress_bg);
        //将自定义的进度条放置在帧布局上显示
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );


        Chapter chapter = data.getBookList().get(groupPosition).getChapterList().get(childPosition);


        int chapterTotal = chapter.getTestTotal();
        int chapterAchieved = chapter.getTestAchieved();

        viewHolder.progressTV.setText(chapterAchieved + "/" + chapterTotal);

        progressHorizontal.setProgress((float) chapterAchieved / chapterTotal);
        viewHolder.progressFL.addView(progressHorizontal, layoutParams);

        //设置每行的标题
        viewHolder.chapterNameTV.setText(chapter.getChapterName());
        //字体大小
        viewHolder.chapterNameTV.setTextSize(15);
        viewHolder.itemRootLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.i(LOGTAG.FRAG_EXERCISE_LIST, "从章节打开  内容Activity");


                BookWithChapters book = data.getBookList().get(groupPosition);
                Chapter chapter = book.getChapterList().get(childPosition);

                Intent intent = new Intent();
                intent.setClass(ctx, ContentActivity.class);
                //传递当前教材的id
                Bundle bundle = new Bundle();
                bundle.putString(Constant.Jump.FROM_TAG, Constant.FragTag.TEST_EXERCISE_CHAPTER);
                bundle.putString(Constant.Jump.CHAPTER_ID, chapter.getChapterID() + "");
                bundle.putString(Constant.Jump.CHAPTER_NAME, chapter.getChapterName());
                bundle.putString(Constant.Jump.BOOK_ID, book.getBookID() + "");
                bundle.putString(Constant.Jump.BOOK_NAME, book.getBookName());
                intent.putExtras(bundle);

                //((MainActivity)ctx).mHandler.obtainMessage(Constant.HandlerFlag.DO_TEST_CONTENT,bundle).sendToTarget();
                int REQUEST_CODE = 1;
                frag.startActivityForResult(intent, REQUEST_CODE);
//                    ((MainActivity)ctx).startActivity(intent);
            }


        });

        //        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//
//        layoutParams2.setMargins(UIUtil.dip2px(10), 0, 0, 0);
//        viewHolder.itemRootLL.setLayoutParams(layoutParams2);
        viewHolder.itemRootLL.setPadding(UIUtil.dip2px(5), 0, 0, 0);
        viewHolder.itemRootLL.setBackgroundResource(R.color.root_bg);

        viewHolder.showIV.setPadding(UIUtil.dip2px(13), UIUtil.dip2px(13), 0, UIUtil.dip2px(13));
        viewHolder.showIV.setImageResource(R.drawable.ext_point);

        viewHolder.editorIV.setPadding(UIUtil.dip2px(9), UIUtil.dip2px(9), UIUtil.dip2px(9), UIUtil.dip2px(9));
//        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        viewHolder.iv_editor.setLayoutParams(layoutParams1);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    static class ViewHolder extends ViewViewHolder {
        public LinearLayout itemRootLL;
        public ImageView showIV, editorIV;
        public TextView chapterNameTV, progressTV;
        public FrameLayout progressFL;

        ViewHolder(View view, boolean refind) {
            super(view, refind);
        }


        protected void findViews() {
            itemRootLL = $(R.id.itemRoot_ll);
            showIV = $(R.id.show_iv);
            editorIV = $(R.id.editor_iv);
            chapterNameTV = $(R.id.chapterName_tv);
            progressTV = $(R.id.progress_tv);
            progressFL = $(R.id.progress_fl);
        }
    }
}

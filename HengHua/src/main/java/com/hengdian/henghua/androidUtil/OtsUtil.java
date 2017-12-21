package com.hengdian.henghua.androidUtil;

import android.view.View;

import com.hengdian.henghua.activity.MainActivity;

/**
 * 不通用的特定工具
 * <p>
 * Created by Anderok on 2017/2/16.
 */

public class OtsUtil {
    public static final String JUST_HIDE_PROGRESS_BAR = "justHideProgressBar";
    public static final String SHOW_PROGRESS_BAR = "showProgressBar";
    public static final String SHOW_TEXT_TIP = "showTextTip";
    public static final String SHOW_CONTENT_LAYOUT = "showContentLayout";


    /**
     * 进度动画和消息提示,内容显示控制
     * viewID = new View[]{progressBar,tipTextView,contentView};
     *
     * @param viewID
     * @param showWhat
     * @param mainActivity 用于修正MainActivity遮罩
     */
    public static void windowShow(View[] viewID, String showWhat, MainActivity mainActivity) {
        if (mainActivity != null) {
            mainActivity.viewHolder.dragLayout.close();
        }

        windowShow(viewID, showWhat);
    }


    /**
     * 进度动画和消息提示,内容显示控制
     * viewID = new View[]{progressBar,tipTextView,contentView};
     *
     * @param viewID
     * @param showWhat
     */
    public static void windowShow(View[] viewID, String showWhat) {
        if (viewID == null || viewID.length != 3) {
            return;
        }

        switch (showWhat) {
            case JUST_HIDE_PROGRESS_BAR:
                viewID[0].setVisibility(View.INVISIBLE);
                break;
            case SHOW_PROGRESS_BAR:
                viewID[1].setVisibility(View.GONE);
                viewID[0].setVisibility(View.VISIBLE);
                viewID[2].setVisibility(View.VISIBLE);
                break;
            case SHOW_TEXT_TIP:
                viewID[0].setVisibility(View.GONE);
                viewID[2].setVisibility(View.GONE);
                viewID[1].setVisibility(View.VISIBLE);
                break;
            case SHOW_CONTENT_LAYOUT:
                viewID[0].setVisibility(View.GONE);
                viewID[1].setVisibility(View.GONE);
                viewID[2].setVisibility(View.VISIBLE);
                break;
        }
    }
}

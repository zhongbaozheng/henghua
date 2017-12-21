package com.hengdian.henghua.utils;

/**
 * 内容刷新标识保持类
 *
 * Created by Anderok on 2017/2/23.
 */
public class RefreshHolder {

    public static final int REFRESH_LOCAL = 0x01; //本地刷新
    public static final int REFRESH_NET = 0x02; //网络刷新

    public static int REVIEW_LIST = REFRESH_LOCAL; //知识重温教材章节列表
    public static int REVIEW_CONTENT = REFRESH_LOCAL; //知识重温章节内容

    public static int TEST_EXERCISE_LIST = REFRESH_LOCAL; //练习模式教材章节列表
    public static int TEST_EXERCISE_CONTENT = REFRESH_LOCAL; //练习模式内容

    public static int TEST_BREAKTHROUGH_LIST = REFRESH_LOCAL; //闯关教材列表
    public static int TEST_STAGE_LIST = REFRESH_LOCAL; //闯关章节列表（关卡）
    public static int TEST_STAGE_CONTENT = REFRESH_LOCAL; //闯关内容

    public static int TEST_EXAM_LIST = REFRESH_LOCAL; //考试教材列表
    public static int TEST_EXAM_CONTENT = REFRESH_LOCAL; //考试内容

    public static int COURSE_LIST = REFRESH_LOCAL; //课程列表
    public static int COURSE_VIDEO_LIST = REFRESH_LOCAL; //视频列表
}

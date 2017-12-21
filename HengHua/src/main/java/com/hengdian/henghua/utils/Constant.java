package com.hengdian.henghua.utils;

/**
 * 常量类
 * <p>
 * Created by Anderok on 2017/1/24.
 */

public class Constant {
    /**
     * activity标志
     */
    public static class ViewFlag {
        public static final int SPLASH_ACTIVITY = 0x0000;
        public static final int MAIN_ACTIVITY = 0x0001;
        public static final int LOGIN_ACTIVITY = 0x0002;
        public static final int PWD_ACTIVITY = 0x0003;
        public static final int CONTENT_ACTIVITY = 0x0004;
    }

    public static class Jump {
        public static final String FROM_TAG = "fromTag";
        public static final String BOOK_ID = "bookID";
        public static final String BOOK_NAME = "bookName";
        public static final String CHAPTER_ID = "chapterID";
        public static final String CHAPTER_NAME = "chapterNAme";
    }

    /**
     * 用于标识来源，或用作fragment标识
     */
    public static class FragTag {
        public static final String REVIEW = "review";
        public static final String REVIEW_CHAPTER = "reviewChapter";
        //        public static final String REVIEW_BOOK = "reviewBook";
        public static final String REVIEW_CONTENT = "reviewContent";

        public static final String TEST = "test";
        public static final String TEST_EXERCISE_CHAPTER = "testExerciseChapter";
        //        public static final String TEST_EXERCISE_BOOK = "testExerciseBook";
        public static final String TEST_EXERCISE_CONTENT = "testExerciseContent";
        public static final String TEST_BREAKTHROUGH = "testBreakthrough";
        public static final String TEST_STAGE = "testStage";
        public static final String TEST_STAGE_CONTENT = "testStageContent";
        public static final String TEST_EXAM = "testExam";
        public static final String TEST_EXAM_CONTENT = "testExamContent";
//        public static final String TEST_CONTENT_EXERCISE = "testContentExercise";
//        public static final String TEST_CONTENT_STAGE = "testContentSts";
//        public static final String TEST_CONTENT_eXAM = "testContent";

        public static final String COURSE = "course";
    }

    /**
     * 消息标志
     */
    public class HandlerFlag {

        public static final int LOGIN_IN = 0x0012;
        public static final int REQUEST_DATA = 0x0013;
        public static final int REQUEST_DATA_ON_RESULT = 0x0014;
        public static final int LOGINING = 0x0015;

        public static final int EXAM_COMMIT = 0x0016;
        public static final int EXAM_COMMIT_ON_RESULT = 0x0017;

        public static final int RESET_BACK_PRESSED_COUNTER = 0x0018;

        public static final int CLOSE_CURRENT_WINDOW = 0x0019;
//        public static final int EXAM_EXIT_WITH_CHECK = 0x0020;



        //public static final int TO_MAIN_ACTIVITY = 0x0013;

        //public static final int NETWORK_NONE = 0x0000;
        public static final int LOGIN_ON_RESULT = 0x0001;
        public static final int LOGIN_SUCCESS = 0x0002;
        //  public static final int USERNAME_OR_PWD_EMPTY = 0x0003;

        public static final int OPEN_LOGIN_ACTIVITY = 0x0004;

        public static final int CLOSE_LEFT_MENU = 0x0005;

//        public static final int OLD_OR_NEM_PWD_CANNOT_BE_EMPTY = 0x0006;
//        public static final int OLD_NEM_PWD_CANNOT_BE_SAME = 0x0007;
//        public static final int NEM_PWD_CHECK_FAILED = 0x0008;

        public static final int UPDATE_PWD_ON_RESULT = 0x0009;
        public static final int UPDATE_PWD_SUCCESS = 0x0010;
        // public static final int UPDATE_PWD_FAILED = 0x0011;


        //数据请求类
        public static final int GET_DATA_ON_RESULT = 0x5000;
        public static final int GET_COURSE_LIST_ON_RESULT = 0x5001;


        public static final int NETWORK_AVAILABLE = 0x2000;
        public static final int NETWORK_INVALID = 0x2001;

        public static final int DO_TEST_CONTENT = 0x0090;
        public static final int DO_REVIEW_CONTENT = 0x0091;
    }




    public static class RequestResultCode {
        //当Activity结束时resultCode将归还在onActivityResult()中，一般为RESULT_CANCELED , RESULT_OK该值默认为-1。
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:operation succeeded. 默认值是-1

        //        public static final int FROM_REVIEW_BOOK = 0x001;
        public static final int FROM_REVIEW_CHAPTER = 0x002;
        public static final int FROM_REVIEW_CONTENT = 0x003;//来自知识重温阅读页面

        //        public static final int FROM_TEST_EXERCISE_BOOK = 0x004;
        public static final int FROM_TEST_EXERCISE_CHAPTER = 0x005;

        public static final int FROM_TEST_BREAKTHROUGH = 0x006;
        public static final int FROM_TEST_STAGE = 0x007;
        public static final int FROM_TEST_EXAM = 0x008;

        public static final int FROM_EXAM_CONTENT = 0x009;//来自在线测试做题页面

        public static final int FROM_COURSE = 0x00a;//来自知识串讲课程列表
        public static final int FROM_COURSE_VIDEO = 0x00b;//来自知识串讲课程视频列表
    }


}

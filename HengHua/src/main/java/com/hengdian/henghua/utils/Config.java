package com.hengdian.henghua.utils;

public class Config {

    public static final String CHARSET_HTTP = "UTF-8";

    public static final String ROOT_URL = "http://a002.nscc-gz.cn:10393/henghua_app/";
//  public static final String ROOT_URL = "http://10.5.78.20:9090/";

    //启动画面延时，毫秒
    public static final int LAUNCH_DELAY = 3000;

    public static class FunctionalURL {

        /**
         * 学生模块，登录
         */
        public static final String STUDENT_LOGIN = ROOT_URL
                + "/student/StudentLogin";

        /**
         * 学生模块，注销登录
         */
        public static final String STUDENT_LOGOUT = ROOT_URL
                + "/student/ClearTokenId";

        /**
         * 学生模块，根据用户名和旧密码，修改密码
         */
        public static final String STUDENT_UPDATE_PWD = ROOT_URL
                + "/student/UpdatePasswordByUserName";

        /**
         * 知识重温、学习模式，获取所有教材，含章节
         */
        public static final String CWXX_ALL_BOOKS_WHIT_CHAPTER = ROOT_URL
                + "/review/FindAllTextBookAndChapter";

        /**
         * 知识重温，根据章节ID获取该章节所有内容
         */
        public static final String CW_CONTENT_OF_CHAPTER = ROOT_URL
                + "/review/FindChapterContentByChapterId";

        /**
         * 废弃，学习模式，根据章节id和试题类型查找该章节所有试题
         */
        public static final String XX_QUESTIONS_OF_CHAPTER_BY_TYPE = ROOT_URL
                + "/online_test/FindTestsByChapterIdAndTestsType";

        /**
         * 学习模式，根据章节id查找该章节所有试题，结果已按按题型分类
         */
        public static final String XX_QUESTIONS_OF_CHAPTER_GROUP_BY_TYPE = ROOT_URL
                + "/online_test/FindTestsByChapterId2";

        /**
         * 闯关模式，获取所有教材
         */
        public static final String CG_ALL_BOOKS = ROOT_URL
                + "/review/FindAllTextBook";

        /**
         * 闯关模式，根据教材ID获取该教材所有章节(关卡)
         */
        public static final String CG_CHAPTERS_OF_BOOK = ROOT_URL
                + "/review/FindChapterByTextBookId";

        /**
         * 闯关模式，根据章节id查找该章节所有试题
         */
        public static final String CG_QUESTIONS_OF_CHAPTER = ROOT_URL
                + "/online_test/FindTestsByChapterId";

        /**
         * 考试模式，获取所有考试教材（含考过的和没考过的）
         */
        public static final String KS_ALL_BOOKS_WITH_SCORE = ROOT_URL
                + "/review/FindAllTextBook2";

        /**
         * 考试模式，根据教材id查找相应的考试试题
         */
        public static final String KS_QUESTIONS_OF_BOOK = ROOT_URL
                + "/online_test/FindTestsByTextBookId";

        /**
         * 考试模式，提交整套考试结果（答案和分数）
         */
        public static final String KS_EXAM_COMMIT = ROOT_URL
                + "/online_test/ExamCommit";

        /**
         * 视频，获取所有课程（含购买状态）
         */
        public static final String SP_ALL_COURSE_WITH_BUY_STATUS = ROOT_URL
                + "/video/FindAllCourse2";

        /**
         * 视频，获取某一课程所有视频
         */
        public static final String SP_VEDIO_OF_COURSE = ROOT_URL
                + "/video/FindCourseDetailsByCourseId";

        /**
         * 视频，获取所有课程，废弃
         */
        public static final String SP_ALL_COURSE = ROOT_URL
                + "/video/FindAllCourse";
    }
}

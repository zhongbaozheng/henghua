package com.hengdian.henghua.androidUtil;

/**
 * 数据库索引构造工具类,不用了
 * <p>
 * Created by Anderok on 2017/1/23.
 */

public class DBIndexResolver {

    private static final String BOOK_ID = "%B";
    private static final String CHAPTER_ID = "%C";


    //知识重温，书本（含章节）
    private static final String REVIEW_BOOK_CHAPTER = "R";
    //知识重温#BookID#ChapChapterID
    private static final String REVIEW_CONTENT = "R#%B#%C";

    //在线测试#练习模式，书本（含章节）
    private static final String TEST_EXERCISE_BOOK_CHAPTER = "T#1";
    //在线测试#练习模式#BookID#ChapChapterID
    private static final String TEST_EXERCISE_CONTENT = "T#1#%B#%C";

    //在线测试#闯关模式，书本（不含章节）
    private static final String TEST_BREAKTHROUGH_BOOK = "T#2";
    //在线测试#闯关模式#BookID(章节列表)
    private static final String TEST_BREAKTHROUGH_CHAPTER = "T#2#%B";
    //在线测试#闯关模式#BookID#ChapChapterID(练习题内容)
    private static final String TEST_BREAKTHROUGH_CONTENT = "T#2#%B#%C";

    //在线测试#考试模式，书本（不含章节）
    private static final String TEST_EXAM_BOOK = "T#3";
    //在线测试#考试模式#BookID，试题内容
    private static final String TEST_EXAM_CONTENT = "T#3#%B";
    //在线测试#考试模式#BookID#考试结果
    private static final String TEST_EXAM_RESULT = "T#3#%B#%R";
    //在线测试#考试模式#BookID#考试结果历史
    private static final String TEST_EXAM_RESULT_HISTORY = "T#3#%B#%R";

    //知识串讲，课程（含购买状态）
    private static final String COURSE_COURSE = "C";
    //知识串讲#CouresID，视频列表
    private static final String COURSE_VIDEO = "C#%B";

    public static String reviewBookChapterList() {
        return REVIEW_BOOK_CHAPTER;
    }

    public static String reviewContent(String bookID, String chapterID) {
        return REVIEW_CONTENT.replace(BOOK_ID, bookID).replace(CHAPTER_ID, chapterID);
    }

    public static String exerciseBookChapterList() {
        return TEST_EXERCISE_BOOK_CHAPTER;
    }

    public static String exerciseContent(String bookID, String chapterID) {
        return TEST_EXERCISE_CONTENT.replace(BOOK_ID, bookID).replace(CHAPTER_ID, chapterID);
    }

    public static String breakthroughBookList() {
        return TEST_BREAKTHROUGH_BOOK;
    }

    public static String breakthroughChapterList(String bookID) {
        return TEST_BREAKTHROUGH_CHAPTER.replace(BOOK_ID, bookID);
    }

    public static String breakthroughContent(String bookID, String chapterID) {
        return TEST_BREAKTHROUGH_CONTENT.replace(BOOK_ID, bookID).replace(CHAPTER_ID, chapterID);
    }

    public static String examBookList() {
        return TEST_EXAM_BOOK;
    }

    public static String examContent(String bookID) {
        return TEST_EXAM_CONTENT.replace(BOOK_ID, bookID);
    }

    public static String examResult(String bookID) {
        return TEST_EXAM_RESULT.replace(BOOK_ID, bookID);
    }

    public static String examResultHistory(String bookID) {
        return TEST_EXAM_RESULT_HISTORY.replace(BOOK_ID, bookID);
    }

    public static String courseList() {
        return COURSE_COURSE;
    }

    public static String courseVideoList(String courseID) {
        return COURSE_VIDEO.replace(BOOK_ID, courseID);
    }


//    public static void main(String[] args) {
//        String bookID = "QQQQQ";
//        String chapterID = "CCCC";
//
//        System.out.print(reviewContent(bookID,chapterID));
//    }
}
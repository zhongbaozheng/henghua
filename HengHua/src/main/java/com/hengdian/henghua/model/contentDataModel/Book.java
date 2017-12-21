package com.hengdian.henghua.model.contentDataModel;

/**
 * 教材，不含章节
 *
 * @author Anderok
 */
public class Book {
    public static int STATE0_DEFAULT = 0x0000;
    public static int STATE1_CONTINUE = 0x0001;//继续
    public static int STATE2_ALL_DONE = 0x0002;// 已看完
    public static int STATE_FAVORITE = 0x1001;// 收藏
    public static int STATE_DROPPED = 0x1003;// 丢弃

    public static String KEY_BOOK_ID = "bookID";
    public static String KEY_BOOK_NAME = "bookName";


    /**
     * 教材ID
     */
    private int bookID;
    /**
     * 教程名称
     */
    private String bookName;


    /**
     * 知识重温内容总数
     */
    private int reviewTotal = 0;
    /**
     * 知识重温处理过的内容总数
     */
    private int reviewAchieved = 0;

    /**
     * 考试题目总数
     */
    private int testTotal = 0;
    /**
     * 考试处理过的题目总数
     */
    private int testAchieved = 0;

    /**
     * 剩余时间，考试用
     */
    private long timeRemain = 0;

    /**
     * 状态
     */
    private int state = STATE0_DEFAULT;

    public Book() {

    }

    public Book(int bookID, String bookName) {
        this.bookID = bookID;
        this.bookName = bookName;
    }

    public Book(int bookID, String bookName,int reviewTotal,int testTotal) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.reviewTotal = reviewTotal;
        this.testTotal = testTotal;
    }

    public Book(int bookID, String bookName,int reviewTotal,int testTotal,long timeRemain) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.reviewTotal = reviewTotal;
        this.testTotal = testTotal;
        this.timeRemain = timeRemain;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getReviewAchieved() {
        return reviewAchieved;
    }

    public void setReviewAchieved(int reviewAchieved) {
        this.reviewAchieved = reviewAchieved;
    }

    public int getReviewTotal() {
        return reviewTotal;
    }

    public void setReviewTotal(int reviewTotal) {
        this.reviewTotal = reviewTotal;
    }

    public int getTestAchieved() {
        return testAchieved;
    }

    public void setTestAchieved(int testAchieved) {
        this.testAchieved = testAchieved;
    }

    public int getTestTotal() {
        return testTotal;
    }

    public void setTestTotal(int testTotal) {
        this.testTotal = testTotal;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTimeRemain() {
        return timeRemain;
    }

    public void setTimeRemain(long timeRemain) {
        this.timeRemain = timeRemain;
    }
}

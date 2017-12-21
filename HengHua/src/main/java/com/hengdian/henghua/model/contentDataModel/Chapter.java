package com.hengdian.henghua.model.contentDataModel;

/**
 * 章节
 *
 * @author Anderok
 */
public class Chapter {
    // 关卡状态标签
    public static final int STATE0_DEFAULT = 0x0000;
    public static final int STATE1_UNLOCKED = 0x0001;
    public static final int STATE2_PASSED = 0x0002;

    /**
     * 章节ID
     */
    private int chapterID;
    /**
     * 章节名称
     */
    private String chapterName;
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
     * 状态
     */
    private int state = STATE0_DEFAULT;
    /**
     * 最高分数，只在闯关模式有效
     */
    private int maxScore = 0;

    public Chapter() {
        super();
    }

    public Chapter(int chapterID, String chapterName) {
        this.chapterID = chapterID;
        this.chapterName = chapterName;
    }

    public Chapter(int chapterID, String chapterName,int reviewTotal,int testTotal) {
        this.chapterID = chapterID;
        this.chapterName = chapterName;
        this.reviewTotal = reviewTotal;
        this.testTotal = testTotal;
    }



    public int getChapterID() {
        return chapterID;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
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

    /**
     * 获取最高分，仅闯关有用
     */
    public int getMaxScore() {
        return maxScore;
    }

    /**
     * 设置最高分，仅闯关有用
     *
     * @param maxScore
     */
    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    /**
     * 设置最高分（如果设置的值比已存在的小，则不设置），仅闯关有用
     *
     * @param maxScore
     */
    public void addMaxScore(int maxScore) {
        this.maxScore = this.maxScore > maxScore ? this.maxScore : maxScore;
    }

}

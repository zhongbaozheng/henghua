package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

/**
 * 考试模式，提交考试结果
 *
 * @author Anderok
 */
public class Rs_KS_ExamCommit extends Result {

    /**
     * 教材ID
     */
    private String bookID;
    /**
     * 答案
     */
    private String answers;
    /**
     * 成绩
     */
    private int score;

    public Rs_KS_ExamCommit(int status, String bookID, String answers, int score) {
        super(status);
        this.bookID = bookID;
        this.answers = answers;
        this.score = score;
    }


    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    @Override
    public String getStatusMsg() {
        switch (super.getStatus()) {
            case 200:
                return "提交成功";
            case 0:
                return "请重新登录";
            case 1:
                return "提交失败";
            default:
                return super.getStatusMsg();
        }
    }
}

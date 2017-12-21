package com.hengdian.henghua.model;

/**
 * Created by Anderok on 2017/2/28.
 */

public class TestResult {
    public int answerRight = 0;
    public int answerWrong = 0;
    public int remain = 0;
    public int scoreGet = 0;
    public int scoreTotal = 0;

    public TestResult() {
    }

    public TestResult(int answerRight, int answerWrong, int remain, int scoreGet, int scoreTotal) {
        this.answerRight = answerRight;
        this.answerWrong = answerWrong;
        this.remain = remain;
        this.scoreGet = scoreGet;
        this.scoreTotal = scoreTotal;
    }
}

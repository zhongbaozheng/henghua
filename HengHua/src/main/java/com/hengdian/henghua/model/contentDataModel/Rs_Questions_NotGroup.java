package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 闯关、考试模式试题集合，不明确分组
 *
 * @author Anderok
 */
public class Rs_Questions_NotGroup extends Result {
    /**
     * 教材list集合
     */
    private List<Question> questionList;

    /**
     * 当前选中题型
     */
    private int curType = Question.TYPE_SINGLE; //0单选1多选2判断

    /**
     * 单选题当前位置
     */
    private int curIndexSingle = 0;
    /**
     * 多选题当前位置
     */
    private int curIndexMultiple = 0;
    /**
     * 判断当前位置
     */
    private int curIndexTrueOrFalse = 0;
    /**
     * 未作答
     */
    private int stateDefault;
    /**
     * 已查阅
     */
    private int stateDoing;
    /**
     * 已作答
     */
    private int stateAnswered;
    /**
     * 答对的
     */
    private int stateRight;
    /**
     * 答错的
     */
    private int stateWrong;

    /**
     * 考试时间
     */
    private long timeRemain; //60 * 60 * 1000=31000;

    public Rs_Questions_NotGroup(int status, List<Question> questionList,long timeRemain) {
        super(status);
        this.questionList = questionList;
        this.timeRemain = timeRemain;

        refreshCount();
    }

    public Rs_Questions_NotGroup(int status, List<Question> questionList) {
        super(status);
        this.questionList = questionList;

        refreshCount();
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public int getStateDefault() {
        return stateDefault;
    }

    public void setStateDefault(int stateDefault) {
        this.stateDefault = stateDefault;
    }

    public int getStateDoing() {
        return stateDoing;
    }

    public void setStateDoing(int stateDoing) {
        this.stateDoing = stateDoing;
    }

    public int getStateAnswered() {
        return stateAnswered;
    }

    public void setStateAnswered(int stateAnswered) {
        this.stateAnswered = stateAnswered;
    }

    public int getStateRight() {
        return stateRight;
    }

    public void setStateRight(int stateRight) {
        this.stateRight = stateRight;
    }

    public int getStateWrong() {
        return stateWrong;
    }

    public void setStateWrong(int stateWrong) {
        this.stateWrong = stateWrong;
    }

    public long getTimeRemain() {
        return timeRemain;
    }

    public void setTimeRemain(long timeRemain) {
        this.timeRemain = timeRemain;
    }

    public int getCurType() {
        return curType;
    }

    public void setCurType(int curType) {
        this.curType = curType;
    }

    public int getCurIndexSingle() {
        return curIndexSingle;
    }

    public void setCurIndexSingle(int curIndexSingle) {
        this.curIndexSingle = curIndexSingle;
    }

    public int getCurIndexMultiple() {
        return curIndexMultiple;
    }

    public void setCurIndexMultiple(int curIndexMultiple) {
        this.curIndexMultiple = curIndexMultiple;
    }

    public int getCurIndexTrueOrFalse() {
        return curIndexTrueOrFalse;
    }

    public void setCurIndexTrueOrFalse(int curIndexTrueOrFalse) {
        this.curIndexTrueOrFalse = curIndexTrueOrFalse;
    }


    public void refreshCount() {
        numTotal = 0;
        numAchieved = 0;

        stateDefault = 0;
        stateDoing = 0;
        stateAnswered = 0;
        stateRight = 0;
        stateWrong = 0;

        if (questionList != null) {
            numTotal = questionList.size();
            for (Question question : questionList) {
                // 如果已作答
                if (question.getState() >= Question.STATE2_ANSWERED) {
                    numAchieved++;
                }

                switch (question.getState()) {
                    case Question.STATE0_DEFAULT:
                        stateDefault++;
                        break;
                    case Question.STATE1_DOING:
                        stateDoing++;
                        break;
                    case Question.STATE2_ANSWERED:
                        stateAnswered++;
                        break;
                    case Question.STATE3_RIGHT:
                        stateRight++;
                        break;
                    case Question.STATE3_WRONG:
                        stateWrong++;
                        break;
                }
            }
        }
    }

    @Override
    public String getStatusMsg() {
        switch (super.getStatus()) {
            case 200:
                return "查询成功";
            case 0:
                return "请重新登录";
            case -1:
                return "已经考过了";
            default:
                return super.getStatusMsg();
        }
    }
}

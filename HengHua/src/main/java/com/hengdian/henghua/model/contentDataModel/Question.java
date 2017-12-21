package com.hengdian.henghua.model.contentDataModel;

/**
 * 试题模型，单选题、多选题、判断题通用
 */
public class Question {
    //试题状态标签
    public static final int STATE0_DEFAULT = 0x0000;//默认状态未处理
    public static final int STATE1_DOING = 0x0001;//答题中（未确定答案或未提交）

    public static final int STATE2_ANSWERED = 0x0002;//已答题（未对答案）
    public static final int STATE3_WRONG = 0x0003;//答错的
    public static final int STATE3_RIGHT = 0x0004;//答对的

    public static final int TYPE_SINGLE = 0;//单选
    public static final int TYPE_MULTIPLE = 1;//多选
    public static final int TYPE_TRUE_FALSE = 2;//判断

    /**
     * 试题ID
     */
    private int ID;
    /**
     * 试题类型
     */
    private int type;
    /**
     * 试题分数
     */
    private int score;
    /**
     * 试题描述内容
     */
    private String questionContent;
    /**
     * 试题选项
     */
    private String[] options;
    /**
     * 试题答案
     */
    private String answer;
    /**
     * 试题解释
     */
    private String explain;
    /**
     * 试题状态
     */
    private int state = STATE0_DEFAULT;

    public boolean showAnswer = false;

    /**
     * 选择的选项
     */
    private String[] selected = {"", "", "", "", "", "", "", "", ""};
    public boolean isShow = false;

    public Question() {
        super();
    }

    public Question(int iD, int type, int score, String questionContent, String[] options, String answer, String explain) {
        super();
        ID = iD;
        this.type = type;
        this.score = score;
        this.questionContent = questionContent;
        this.options = options;
        this.answer = answer;
        this.explain = explain;
    }

    public Question(int iD, int type, int score, String questionContent, String[] options, String answer, String explain, int state) {
        super();
        ID = iD;
        this.type = type;
        this.score = score;
        this.questionContent = questionContent;
        this.options = options;
        this.answer = answer;
        this.explain = explain;
        this.state = state;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setSelected(String[] selected) {
        this.selected = selected;
    }

    public String[] getSelected() {
        return selected;
    }


    public boolean isShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }

    /**
     * 清除选择
     */
    public void clearSelected() {
        for (int i = 0; i < selected.length; i++) {
            selected[i] = "";
        }
    }


    public static final String NAME_SINGLE = "单选题";
    public static final String NAME_MULTIPLE = "多选题";
    public static final String NAME_TRUE_FALSE = "判断题";

    public String getQuestionTypeName() {
        switch (type) {
            case Question.TYPE_SINGLE:
                return NAME_SINGLE;
            case Question.TYPE_MULTIPLE:
                return NAME_MULTIPLE;
            case Question.TYPE_TRUE_FALSE:
                return NAME_TRUE_FALSE;
            default:
                return "其 他";
        }
    }

    /**
     * 将选项数组直接拼接成字符串
     *
     * @return
     */
    public String selectedToString() {
        String selectedStr = "";
        for (int i = 0; i < selected.length; i++) {
            selectedStr += selected[i];
        }

        return selectedStr;
    }

}

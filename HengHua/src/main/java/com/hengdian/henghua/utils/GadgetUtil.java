package com.hengdian.henghua.utils;

import android.content.SharedPreferences;

import com.hengdian.henghua.model.TestResult;
import com.hengdian.henghua.model.contentDataModel.Question;

import java.util.List;

/**
 * Created by Anderok on 2017/1/8.
 * <p>
 * 小工具集合
 */

public class GadgetUtil {



    public static TestResult countResult(List<Question> questionList) {
        TestResult rs = new TestResult();

        if (questionList == null) {
            return rs;
        }

        for (Question question : questionList) {
            rs.scoreTotal += question.getScore();
            //如果回答过了
            if (!question.selectedToString().isEmpty()) {
                if (question.getState() == Question.STATE3_RIGHT) {
                    rs.answerRight++;
                    rs.scoreGet += question.getScore();
                } else if (question.getState() == Question.STATE3_WRONG) {
                    rs.answerWrong++;
                }
            } else {
                rs.remain++;
            }

        }
        return rs;
    }


    public static String formatItemNum(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return "" + num;
        }
    }


    /**
     * 将数字索引0、1...8转换成选项A、B、C...G,T、F,对、错，,其余返回""
     */
    public static String getOptionChar(int index) {
        //optionChar = optionChar.toUpperCase();
        switch (index) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "T";
            case 8:
                return "F";
            default:
                return "";
        }

    }

}









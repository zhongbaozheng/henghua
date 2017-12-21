package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 闯关模式，根据教材ID获取的章节内容，用作关卡
 *
 * @author Anderok
 */
public class Rs_CG_Chapters extends Result {

    /**
     * 章节list集合
     */
    private List<Chapter> chapterList;


    public Rs_CG_Chapters(int status, List<Chapter> chapterList) {
        super(status);
        this.chapterList = chapterList;

    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }



    @Override
    public String getStatusMsg() {
        switch (super.getStatus()) {
            case 200:
                return "查询成功";
            case 0:
                return "请重新登录";
            default:
                return super.getStatusMsg();
        }
    }
}

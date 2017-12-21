package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 知识重温，章节内容
 *
 * @author Anderok
 */
public class Rs_CW_ChapterContents extends Result {

    /**
     * 章节内容list集合
     */
    private List<ChapterContent> chapterContentList;


    public Rs_CW_ChapterContents(int status,
                                 List<ChapterContent> chapterContentList) {
        super(status);
        this.chapterContentList = chapterContentList;

    }

    public List<ChapterContent> getChapterContentList() {
        return chapterContentList;
    }

    public void setChapterContentList(List<ChapterContent> chapterContentList) {
        this.chapterContentList = chapterContentList;
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

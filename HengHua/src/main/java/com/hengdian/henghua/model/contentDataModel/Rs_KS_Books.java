package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 考试模式，教材，含成绩（-1未考）
 *
 * @author Anderok
 */
public class Rs_KS_Books extends Result {

    /**
     * 教材list集合
     */
    private List<BookWithScore> bookList;

    public boolean isTmpData = false;//是否本地缓存数据

    public Rs_KS_Books(int status, List<BookWithScore> bookList) {
        super(status);
        this.bookList = bookList;
    }


    public List<BookWithScore> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookWithScore> bookList) {
        this.bookList = bookList;
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

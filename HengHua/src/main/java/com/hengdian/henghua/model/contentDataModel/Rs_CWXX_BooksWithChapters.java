package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 知识重温和学习模式共用，教材，含章节
 *
 * @author Anderok
 */
public class Rs_CWXX_BooksWithChapters extends Result {

    /**
     * 章节内容list集合
     */
    private List<BookWithChapters> bookList;


    public Rs_CWXX_BooksWithChapters(int status, List<BookWithChapters> bookList) {
        super(status);
        this.bookList = bookList;
    }

    public List<BookWithChapters> getBookList() {
        return bookList;
    }


    public void setBookList(List<BookWithChapters> bookList) {
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

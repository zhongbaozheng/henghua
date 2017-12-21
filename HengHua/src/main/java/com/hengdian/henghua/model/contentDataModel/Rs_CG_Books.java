package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

import java.util.List;

/**
 * 闯关模式，教材，不含章节
 *
 * @author Anderok
 */
public class Rs_CG_Books extends Result {

    /**
     * 教材list集合
     */
    private List<Book> bookList;


    public Rs_CG_Books(int status, List<Book> bookList) {
        super(status);
        this.bookList = bookList;

    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
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

package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

/**
 * 学生登录结果，登录失败时token_id=null
 *
 * @author Anderok
 */
public class Rs_LogOut extends Result {


    public Rs_LogOut() {
        super();
    }

    public Rs_LogOut(int status) {
        super(status);
    }


    @Override
    public String getStatusMsg() {
        switch (super.getStatus()) {
            case 200:
                return "注销成功";
            case 0:
                return "注销失败";
            default:
                return super.getStatusMsg();
        }
    }

}

package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

/**
 * 学生修改密码结果
 *
 * @author Anderok
 */
public class Rs_UpdatePwd extends Result {

    /**
     * 用户名
     */
    private String userName;
    /**
     * MD5处理后的32位旧登录密码
     */
    private String oldPwd;
    /**
     * MD5处理后的32位新登录密码
     */
    private String newPwd;

    public Rs_UpdatePwd() {
        super();
    }

    public Rs_UpdatePwd(int status, String userName, String oldPwd,
                        String newPwd) {
        super(status);
        this.userName = userName;
        this.oldPwd = oldPwd;
        this.newPwd = newPwd;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }


    @Override
    public String getStatusMsg() {
        switch (super.getStatus()) {
            case 200:
                return "修改密码成功";
            case 0:
                return "查无此学生";
            case 1:
                return "旧密码不正确";
            case 2:
                return "修改密码失败";
            default:
                return super.getStatusMsg();
        }
    }
}

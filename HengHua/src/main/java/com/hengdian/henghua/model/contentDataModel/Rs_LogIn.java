package com.hengdian.henghua.model.contentDataModel;

import com.hengdian.henghua.utils.DataRequestUtil;

/**
 * 学生登录结果，登录失败时token_id=null
 *
 * @author Anderok
 */
public class Rs_LogIn extends Result {


    /**
     * 用户名（其实是账户）
     */
    private String userName = "";
    /**
     * 姓名
     */
    private String realName = "";
    /**
     * MD5处理后的32位登录密码
     */
    private String passWord = "";
    /**
     * 登录标识
     */
    private String token_id = "";

    public Rs_LogIn(int status, String userName, String realName, String passWord,
                    String token_id) {
        super(status);
        this.userName = userName;
        this.realName = realName;
        this.passWord = passWord;
        this.token_id = token_id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }


    @Override
    public String getStatusMsg() {
        switch (super.getStatus()) {
            case 200:
                return "登录成功";
            case 0:
                return "查无此学生";
            case 1:
                return "密码不正确";
            case 2:
                return "返回token失败";
            default:
                return super.getStatusMsg();
        }
    }

}

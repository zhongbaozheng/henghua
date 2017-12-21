package com.hengdian.henghua.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hengdian.henghua.androidUtil.SPFUtil;
import com.hengdian.henghua.utils.ZUtil;

/**
 * 登录账号信息
 * <p>
 * Created by Anderok on 2017/1/17.
 */

public class AccountInfo {
    //方便在SP取值的标签
    public static final String SP_ACCOUNT_INFO = "accountInfo";
    public static final String IS_FIRST_LOGIN = "isFirstLogin";
    public static final String ACCOUNT = "account";
    public static final String NAME = "realName";
    public static final String PWD = "pwd";
    public static final String TOKEN_ID = "tokenID";
    public static final String IS_REMEMBER_PWD = "isRememberPWD";
    public static final String IS_AUTO_LOGIN = "isAutoLogin";

    private boolean isFirstLogin = false; //是否第一次登录,决定点击眼睛是否显示密码
    private String account = ""; //账号
    private String name = ""; //姓名
    private String pwd = ""; //密码
    private String tokenID = ""; //登录标志
    private boolean isRememberPWD = true; //是否记住密码
    private boolean isAutoLogin = true; //是否自动登录

    /**
     * 创建对象并初始化值
     */
    public AccountInfo() {
        refreshData();
    }

    public AccountInfo(boolean isFirstLogin, String account, String name, String pwd, String tokenID, boolean isRememberPWD, boolean isAutoLogin) {
        this.isFirstLogin = isFirstLogin;
        this.account = account;
        this.name = name;
        this.pwd = pwd;
        this.tokenID = tokenID;
        this.isRememberPWD = isRememberPWD;
        this.isAutoLogin = isAutoLogin;
    }

    /**
     * 从sp中获取账号信息
     */
    public void refreshData() {
        SharedPreferences accountSP = SPFUtil.getSPOfApp(AccountInfo.SP_ACCOUNT_INFO, Context.MODE_PRIVATE);
        isFirstLogin = accountSP.getBoolean(IS_FIRST_LOGIN, true);
        account = accountSP.getString(ACCOUNT, "");
        name = accountSP.getString(NAME, "");
        pwd = accountSP.getString(PWD, "");
        tokenID = accountSP.getString(TOKEN_ID, "");
        isRememberPWD = accountSP.getBoolean(IS_REMEMBER_PWD, true);
        isAutoLogin = accountSP.getBoolean(IS_AUTO_LOGIN, true);

    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public boolean isRememberPWD() {
        return isRememberPWD;
    }

    public void setRememberPWD(boolean rememberPWD) {
        isRememberPWD = rememberPWD;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        isAutoLogin = autoLogin;
    }

    @Override
    public String toString() {
        return "isFirstLogin:" + isFirstLogin + ",account:" + account + ",name:" + name
                + ",pwd:" + pwd + "," + "tokenID:" + tokenID
                + ",isRememberPWD:" + isRememberPWD + ",isAutoLogin:" + isAutoLogin;

    }

    /**
     * 判断是否具备自动登录条件
     *
     * @return
     */
    public boolean canAutoLogIn() {
        if(ZUtil.isEmpty(account) || ZUtil.isEmpty(pwd) || !isAutoLogin){
            return false;
        }

        return true;
    }

}

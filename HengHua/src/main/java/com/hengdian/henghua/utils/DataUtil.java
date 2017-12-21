package com.hengdian.henghua.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hengdian.henghua.androidUtil.SPFUtil;
import com.hengdian.henghua.model.AccountInfo;

/**
 * Created by Anderok on 2017/1/20.
 */

public class DataUtil {


    /**
     * 保存账号信息到app SP 中
     */
    public static void saveAccountInfo(AccountInfo accountInfo) {
        SharedPreferences.Editor editor = SPFUtil.getAPPSPEditor(AccountInfo.SP_ACCOUNT_INFO, Context.MODE_PRIVATE);

        editor.putBoolean(AccountInfo.IS_FIRST_LOGIN, accountInfo.isFirstLogin());

        editor.putString(AccountInfo.ACCOUNT, accountInfo.getAccount());
        editor.putString(AccountInfo.NAME, accountInfo.getName());

        //如果不记住密码，清空登录口令
        if (accountInfo.isRememberPWD()) {
            editor.putString(AccountInfo.PWD, accountInfo.getPwd());
        } else {
            editor.putString(AccountInfo.PWD, "");
        }

        editor.putString(AccountInfo.TOKEN_ID, accountInfo.getTokenID());
        editor.putBoolean(AccountInfo.IS_REMEMBER_PWD, accountInfo.isRememberPWD());

        if(accountInfo.getAccount().isEmpty() || accountInfo.getPwd().isEmpty()){
            editor.putBoolean(AccountInfo.IS_AUTO_LOGIN, false);
        }else{
            editor.putBoolean(AccountInfo.IS_AUTO_LOGIN, accountInfo.isAutoLogin());
        }

        editor.commit();
    }


}

package com.hengdian.henghua.model;

/**
 * Created by Anderok on 2017/1/23.
 */
public class PwdUpdateInfo {
    private String oldPwd ="";
    private String newPwd1 ="";
    private String newPwd2 = "";
    private boolean isShowAble = true;
    private boolean isPwdActivityOpened=false;

    public PwdUpdateInfo(){

    }

    public PwdUpdateInfo(String oldPwd,String newPwd1,String newPwd2){
        this.oldPwd = oldPwd;
        this.newPwd1 = newPwd1;
        this.newPwd2 = newPwd2;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd1() {
        return newPwd1;
    }

    public void setNewPwd1(String newPwd1) {
        this.newPwd1 = newPwd1;
    }

    public void setNewPwd2(String newPwd2) {
        this.newPwd2 = newPwd2;
    }

    public String getNewPwd2() {
        return newPwd2;
    }

    public void setShowAble(boolean showAble) {
        isShowAble = showAble;
    }

    public boolean isShowAble() {
        return isShowAble;
    }

    public void setPwdActivityOpened(boolean pwdActivityOpened) {
        isPwdActivityOpened = pwdActivityOpened;
    }

    public boolean isPwdActivityOpened() {
        return isPwdActivityOpened;
    }

    @Override
    public String toString() {
        return "oldPwd="+oldPwd +",newPwd1="+newPwd1+",newPwd2="+newPwd2+",isShowAble="+isShowAble;
    }
}

package com.hengdian.henghua.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.androidUtil.ActivityViewHolder;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.UIUtil;
import com.hengdian.henghua.model.AccountInfo;
import com.hengdian.henghua.model.PwdUpdateInfo;
import com.hengdian.henghua.utils.AsyncDataUtil;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataUtil;

/**
 * Created by Anderok on 2017/1/15.
 */

public class UpdatePwdActivity extends BaseActivity {

    ViewHolder viewHolder;
    AccountInfo info = mApp.getAccountInfo();
    PwdUpdateInfo pwdInfo = mApp.getPwdUpdateInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MyApplication.getINSTANCE().addActivity(this);
        mApp.curActivityFlag = Constant.ViewFlag.PWD_ACTIVITY;

        if (!mApp.isLogInSuccess) {
            ToastUtil.toastMsgShort("请先登录！");

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UIUtil.switchActivity(UpdatePwdActivity.this, LoginActivity.class, true);
                }
            }, 2000);
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_pwd);

        viewHolder = new ViewHolder(this,false);

        mApp.setTransmitHandler(mHandler);

        UIUtil.initImmersiveTitleBar(this, null);

        viewHolder = new ViewHolder(this,false);
        //设置监听器
        initListener();
        initData();
        pwdInfo.setPwdActivityOpened(true);

    }

    private void initData() {
        viewHolder.pwdAccountTV.setText(info.getAccount());
        viewHolder.pwdOldET.setText(pwdInfo.getOldPwd());
        viewHolder.pwdNew1ET.setText(pwdInfo.getNewPwd1());
        viewHolder.pwdNew2ET.setText(pwdInfo.getNewPwd2());
    }


    private void initListener() {
        viewHolder.updatePwdShowHideIM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (pwdInfo.isShowAble()) {
                    pwdInfoShowHide(event, true);
                } else {
                    pwdInfoShowHide(event, false);
                }
                return false;
            }
        });

        viewHolder.pwdOldET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pwdInfo.setOldPwd(viewHolder.pwdOldET.getText().toString());
            }
        });

        viewHolder.pwdNew1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pwdInfo.setNewPwd1(viewHolder.pwdNew1ET.getText().toString());
            }
        });

        viewHolder.pwdNew2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pwdInfo.setNewPwd2(viewHolder.pwdNew2ET.getText().toString());

            }
        });

        viewHolder.enSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePwd();
            }
        });
    }

    private void pwdInfoShowHide(MotionEvent event, boolean isShow) {
        UIUtil.pwdShouwHide(viewHolder.pwdOldET, event, isShow);
        UIUtil.pwdShouwHide(viewHolder.pwdNew1ET, event, isShow);
        UIUtil.pwdShouwHide(viewHolder.pwdNew2ET, event, isShow);
    }

    private void updatePwd() {
        AsyncDataUtil.updatePwd();
    }



    private void backToMainActivity() {
        mApp.mainActHandler.sendEmptyMessage(Constant.HandlerFlag.CLOSE_LEFT_MENU);

        mApp.preActivityFlag = Constant.ViewFlag.PWD_ACTIVITY;

        //清空信息
        cleanPwdInfo();
        //如果要离开后返回不能显示就false,而且还有输入和显隐控制逻辑要补
        pwdInfo.setShowAble(true);
        //设置当前页面关闭标志
        pwdInfo.setPwdActivityOpened(false);

        toMainActivity();

    }

    private void toMainActivity() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UIUtil.switchActivity(UpdatePwdActivity.this, MainActivity.class, true);
            }
        }, 200);
    }

    private void delayToBackToMainActivity(long millisecond) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backToMainActivity();
            }
        }, millisecond);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //返回主页
            backToMainActivity();
        }
        return true;
    }

    private void cleanPwdInfo() {
        pwdInfo.setOldPwd("");
        pwdInfo.setNewPwd1("");
        pwdInfo.setNewPwd2("");
        pwdInfo.setShowAble(true);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.HandlerFlag.UPDATE_PWD_SUCCESS:
                    ToastUtil.toastMsgLong("密码修改成功!");
                    savePwd();
                    delayToBackToMainActivity(1500);
                    break;
            }
        }
    };


    private void savePwd() {
        mApp.getAccountInfo().setPwd(mApp.getPwdUpdateInfo().getNewPwd1());
        DataUtil.saveAccountInfo(info);
    }

    class ViewHolder extends ActivityViewHolder {
        TextView pwdAccountTV;
        EditText pwdOldET;
        EditText pwdNew1ET;
        EditText pwdNew2ET;
        ImageView updatePwdShowHideIM;
        TextView enSureBtn;


        ViewHolder(Activity activity,boolean refind) {
            super(activity, refind);
        }

       
        protected void findViews() {
            pwdAccountTV = $(R.id.pwd_account_tv);
            pwdOldET = $(R.id.pwd_old_et);
            pwdNew1ET = $(R.id.pwd_new1_et);
            pwdNew2ET = $(R.id.pwd_new2_et);
            updatePwdShowHideIM = $(R.id.update_pwd_show_hide_im);
            enSureBtn = $(R.id.pwd_btn_tv);
        }
    }


}

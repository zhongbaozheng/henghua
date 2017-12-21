package com.hengdian.henghua.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.androidUtil.ActivityViewHolder;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.MPermissionUtils;
import com.hengdian.henghua.androidUtil.MyApplication;
import com.hengdian.henghua.androidUtil.NetUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.UIUtil;
import com.hengdian.henghua.model.AccountInfo;
import com.hengdian.henghua.utils.AsyncDataUtil;
import com.hengdian.henghua.utils.Config;
import com.hengdian.henghua.utils.Constant;

//@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    viewHolder viewHolder;
    boolean isPwdFistChange = true;
    AccountInfo info;
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp.setTransmitHandler(mHandler);

        new Thread(new Runnable() {
            public void run() {
                checkPermission();
            }
        }).start();

        info = mApp.getAccountInfo();

        //如果APP启动过且已经登录
        if (mApp.isLaunched && mApp.isLogInSuccess && mApp.curActivityFlag != Constant.ViewFlag.MAIN_ACTIVITY) {

            LogUtil.d(TAG,"App isLaunched && isLogInSuccess, jump to main Activity.");

            // LogUtil.d(LOGTAG.ACT_LOGIN, "has logIn, so jump to MainActivity immediately.");

//            Intent intent = new Intent()
//                    .setAction(Intent.ACTION_MAIN)
//                    .addCategory(Intent.CATEGORY_LAUNCHER)
//                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .setClass(getApplication(), MainActivity.class);
//            startActivity(intent);

            UIUtil.switchActivity(this, MainActivity.class, true);
            return;

        } else {

            setContentView(R.layout.activity_login);
            mApp.curActivityFlag = Constant.ViewFlag.LOGIN_ACTIVITY;

            viewHolder = new viewHolder(this, false);

            UIUtil.initImmersiveTitleBar(this, null);
            initActionListener();

            //如果是从菜单打开的就不显示启动画面
            if (mApp.isLaunched && mApp.preActivityFlag == Constant.ViewFlag.MAIN_ACTIVITY) {
                hideSplash(0);
            }

            //如果是自动登录且网络可用，后台登录
            if (info.canAutoLogIn() && !mApp.isLogInSuccess
                    && mApp.preActivityFlag != Constant.ViewFlag.MAIN_ACTIVITY
                    && NetUtil.isNetworkActive(this)) {

                viewHolder.logInTipTV.setVisibility(View.VISIBLE);
                AsyncDataUtil.login();
                hideSplash(15000);
            } else {
                if (!NetUtil.isNetworkActive(this)) {
                    ToastUtil.toastMsgShort("没有可用的网络");

                    //开启网路设置弹窗
//                    mHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            NetUtil.setNetworkTip(LoginActivity.this);
//                        }
//                    }, 3000);
                }

                hideSplash(Config.LAUNCH_DELAY);
            }

            setDataOfView();
            mApp.isLaunched = true;//app已启动过
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApp.setTransmitHandler(mHandler);
    }




    private void hideSplash(int delayMillsec) {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                viewHolder.logInTipTV.setVisibility(View.INVISIBLE);
                viewHolder.splashTV.setVisibility(View.INVISIBLE);
            }
        }, delayMillsec);
    }


    /**
     * 给View填充数据，先执行initData()
     */
    private void setDataOfView() {
        viewHolder.loginAccountET.setText(info.getAccount());
        viewHolder.loginAccountET.setSelection(info.getAccount().length());//讲光标移至末尾
        viewHolder.loginPwdET.setText(info.getPwd());
        viewHolder.loginPwdET.setSelection(info.getPwd().length());
        viewHolder.rememberPwdCB.setChecked(info.isRememberPWD());
        viewHolder.autoLoginCB.setChecked(info.isAutoLogin());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LoginActivity.this.finish();
            return true;
        }
        return false;
    }

    /**
     * 更新账号显示信息并保存更新的信息
     */
    private void updateData() {
        setDataOfView();
    }

    /**
     * 设置各种动作监听，初始化view之后调用
     */
    public void initActionListener() {
        viewHolder.splashTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;//禁止点击穿透
            }
        });


        //账号输入框
        viewHolder.loginAccountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //到了登录界面，注销登录
                info.setTokenID("");
                //如果账号该改变，则清空密码，默认勾选自动登录
                if (!info.getAccount().equals(s.toString())) {
                    info.setFirstLogin(true);
                    info.setAccount(s.toString());
                    info.setPwd("");
                    info.setTokenID("");
                    info.setRememberPWD(true);
                    info.setAutoLogin(true);

                    setDataOfView();
                }

            }
        });
        //密码输入框
        viewHolder.loginPwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!info.getPwd().equals(input)) {
                    if (isPwdFistChange) {
                        isPwdFistChange = false;
                        if (info.getPwd().length() < input.length()) {
                            input = input.substring(info.getPwd().length(), input.length());
                        } else {
                            input = "";
                        }
                    }

                    info.setFirstLogin(true);
                    info.setPwd(input);
                    info.setTokenID("");

                    setDataOfView();
                }
            }
        });

        viewHolder.loginPwdET.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //登录
                    mApp.getAppHandler().sendEmptyMessage(Constant.HandlerFlag.LOGIN_IN);
                }

                return false;
            }
        });


        //密码显隐
        viewHolder.loginPwdShowHideIM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("isFirstLogin",info.isFirstLogin()+"");

                UIUtil.pwdShouwHide(viewHolder.loginPwdET, event, info.isFirstLogin());
                return false;//必须false,否则selector不生效
            }
        });

        //记住密码
        viewHolder.rememberPwdCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (info.isRememberPWD() != isChecked) {
                    info.setRememberPWD(isChecked);
                    if (!isChecked) {//如果不记住密码，取消自动登录
                        viewHolder.autoLoginCB.setChecked(false);
                        info.setAutoLogin(false);
                    }
                }
            }
        });

        //自动登录
        viewHolder.autoLoginCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (info.isAutoLogin() != isChecked) {

                    info.setAutoLogin(isChecked);

                    if (isChecked) {//如果自动登录，记住密码
                        viewHolder.rememberPwdCB.setChecked(true);
                        info.setRememberPWD(true);
                    }
                }
            }
        });

        //登录
        viewHolder.loginBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.appHandler.sendEmptyMessage(Constant.HandlerFlag.LOGIN_IN);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApp.preActivityFlag = Constant.ViewFlag.LOGIN_ACTIVITY;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //离开登录界面就不能再显示密码啦
        info.setFirstLogin(false);

        //如果不记住密码就清空密码
        if (!info.isRememberPWD()) {
            info.setPwd("");
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Constant.HandlerFlag.LOGINING:
                    viewHolder.logInTipTV.setVisibility(View.VISIBLE);
                    break;

                case Constant.HandlerFlag.LOGIN_SUCCESS:
                    //如果之前的界面是主界面，收起侧栏，刷新数据
                    if (mApp.preActivityFlag == Constant.ViewFlag.MAIN_ACTIVITY) {
                        mApp.mainActHandler.sendEmptyMessage(Constant.HandlerFlag.LOGIN_SUCCESS);
                    }
                    //关闭当前窗口跳转到主页
                    toMainActivity();
                    break;

                case Constant.HandlerFlag.LOGIN_ON_RESULT:
                    viewHolder.logInTipTV.setVisibility(View.INVISIBLE);
                    if (!mApp.isLogInSuccess) {
                        hideSplash(1500);
                    }

                    break;

            }
        }
    };

    private void toMainActivity() {
        //延时关闭当前窗口,等待侧边栏关闭
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mApp.preActivityFlag = Constant.ViewFlag.LOGIN_ACTIVITY;
                UIUtil.switchActivity(LoginActivity.this, MainActivity.class, true);
            }
        }, 1000);
    }

    /**
     * 权限检查
     */
    public void checkPermission() {
        String[] permissions = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_CONFIGURATION,

                Manifest.permission.READ_LOGS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.FACTORY_TEST,
                Manifest.permission.SET_DEBUG_APP

        };
        int requestCode = 1;
        MPermissionUtils.requestPermissionsResult(LoginActivity.this, requestCode, permissions
                , new MPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        //
                    }

                    @Override
                    public void onPermissionDenied() {
                        MPermissionUtils.showTipsDialog(LoginActivity.this);
                    }
                });
    }

    static class viewHolder extends ActivityViewHolder {
        EditText loginAccountET; //账号输入框
        EditText loginPwdET;//密码输入框
        ImageView loginPwdShowHideIM; //密码显隐按钮（眼睛）
        CheckBox rememberPwdCB; //记住密码
        CheckBox autoLoginCB;   //自动登录
        TextView loginBtnTV; //登录按钮

        FrameLayout loginFLO;
        LinearLayout loginLO;
        TextView splashTV;
        TextView logInTipTV;


        viewHolder(Activity activity, boolean refind) {
            super(activity, refind);
        }


        protected void findViews() {
            loginAccountET = $(R.id.login_account_et);
            loginPwdET = $(R.id.login_pwd_et);
            loginPwdShowHideIM = $(R.id.login_pwd_show_hide_im);
            rememberPwdCB = $(R.id.remember_pwd_cb);
            autoLoginCB = $(R.id.auto_login_cb);
            loginBtnTV = $(R.id.login_btn_tv);

            loginFLO = $(R.id.login_flo);
            loginLO = $(R.id.login_lo);
            splashTV = $(R.id.splash_tv);
            logInTipTV = $(R.id.logInTip_tv);
        }
    }
}

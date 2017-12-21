package com.hengdian.henghua.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.hengdian.henghua.R;
import com.hengdian.henghua.adapter.LeftItemAdapter;
import com.hengdian.henghua.androidUtil.ActivityViewHolder;
import com.hengdian.henghua.androidUtil.LOGTAG;
import com.hengdian.henghua.androidUtil.LogUtil;
import com.hengdian.henghua.androidUtil.ToastUtil;
import com.hengdian.henghua.androidUtil.UIUtil;
import com.hengdian.henghua.fragment.BackHandledFragment;
import com.hengdian.henghua.fragment.CourseFragment;
import com.hengdian.henghua.fragment.ReviewFragment;
import com.hengdian.henghua.fragment.TestFragment;
import com.hengdian.henghua.utils.Constant;
import com.hengdian.henghua.utils.DataRequestUtil;
import com.hengdian.henghua.widget.DragLayout;
import com.hengdian.henghua.widget.FragmentTabHost;
import com.nineoldandroids.view.ViewHelper;

//@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener, BackHandledFragment.BackPressedHandler {
    public ViewHolder viewHolder;
    private LeftItemAdapter leftItemAdapter;

    public FragmentTabHost tabHost;
//    public final String[] tabText = {"知识重温", "在线测试", "知识串讲"};
    public final String[] tabText = {"知识重温", "在线测试"};
    private int[] imageRes = new int[]{R.drawable.nav_tab_review_slt, R.drawable.nav_tab_test_slt, R.drawable.nav_tab_course_slt};
//    private Class[] fragments = new Class[]{ReviewFragment.class, TestFragment.class, CourseFragment.class};
    private Class[] fragments = new Class[]{ReviewFragment.class, TestFragment.class};

    public static int curTabIndex = 0;
    public static boolean isRootFrag = true;
    public static String curTestChildFragTag = "";//在线测试当前的子View标记

    private final String TAG = "MainActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!mApp.isLogInSuccess) {
            mApp.appHandler.sendEmptyMessage(Constant.HandlerFlag.LOGIN_IN);
        }

        setContentView(R.layout.activity_main);
        mApp.curActivityFlag = Constant.ViewFlag.MAIN_ACTIVITY;
        viewHolder = new ViewHolder(this, false);

        UIUtil.initImmersiveTitleBar(this, viewHolder.statusBarLO);

        mApp.setMainActHandler(mHandler, Constant.ViewFlag.MAIN_ACTIVITY);
        mApp.setTransmitHandler(mHandler);

        mApp.isLeftMenuClosed = true;

        initNavigationTab();
        initListener();
        initLeftMenuData();
    }


    private void initLeftMenuData() {
        leftItemAdapter = new LeftItemAdapter(this);
        leftItemAdapter.updateItemBean(0, "姓名：" + mApp.getAccountInfo().getName(), false);
        leftItemAdapter.updateItemBean(1, "账号：" + mApp.getAccountInfo().getAccount(), false);
        //初始化左侧菜单列表
        viewHolder.leftMenuLV.setAdapter(leftItemAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mApp.curActivityFlag = Constant.ViewFlag.MAIN_ACTIVITY;
        if (mApp.getPwdUpdateInfo().isPwdActivityOpened()) {
            UIUtil.switchActivity(MainActivity.this, UpdatePwdActivity.class, false);
        }

        if (!mApp.isLeftMenuClosed) {
            ViewHelper.setAlpha(viewHolder.topBarLO, 1);
            ViewHelper.setAlpha(viewHolder.mainRootLO, 1);
        }else{
            viewHolder.dragLayout.close();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG,"onKeyDown...");
        // 如果按下返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && !mApp.isLeftMenuClosed) {
            LogUtil.i(TAG,"dragLayout.close...");
            viewHolder.dragLayout.close();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onStop() {
        LogUtil.d(TAG,"onStop...");
        super.onStop();

        mApp.curActivityFlag = 0;
    }

    /**
     * 初始化listener
     */
    private void initListener() {
        LogUtil.i(TAG,"initListener...");

        viewHolder.mainShadowTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        viewHolder.mainTopFlapTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mApp.isLeftMenuClosed) {
                    closeLeftMenu();
                    return true;
                }
                return false;
            }
        });

        viewHolder.topBarLO.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return true;
            }
        });


        /**
         * DragLayout的事件监听
         */
        viewHolder.dragLayout.setDragStateListener(new DragLayout.OnDragStatusListener() {

            @Override
            public void onClose() {
                mApp.isLeftMenuClosed = true;
            }

            @Override
            public void onOpen() {
                mApp.isLeftMenuClosed = false;
            }

            @Override
            public void onDraging(float percent) {
                //通过拖拽实现主界面头部隐藏
                ViewHelper.setAlpha(viewHolder.topBarLO, 1.3f - percent);
                //通过拖拽实现主界面的遮罩
                ViewHelper.setAlpha(viewHolder.mainRootLO, 1.5f - percent);
            }
        });


        //设置点击主页用户头像弹出侧边栏
        viewHolder.usrIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLeftMenu();
            }
        });

        viewHolder.backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        viewHolder.leftMenuLV.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()) {
                    case R.id.left_menu_lv:
                        expressLeftMenuItemClick(position);
                        break;
                }
            }
        });

        //设置点击左侧菜单收起按钮监听
        viewHolder.leftMenuHideTVBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                closeLeftMenu();
            }
        });


    }

    public void expressLeftMenuItemClick(int position) {
        switch (position) {
            case 2:
                if (mApp.getAccountInfo().getAccount().isEmpty()) {
                    ToastUtil.toastMsgShort("请先登录!");
                } else {
                    UIUtil.switchActivity(MainActivity.this, UpdatePwdActivity.class, false);
                }
                break;
            case 3:
                UIUtil.switchActivity(MainActivity.this, LoginActivity.class, true);
                mApp.preActivityFlag = Constant.ViewFlag.MAIN_ACTIVITY;
                mApp.isLogInSuccess = false;
                break;
        }
    }

    /**
     * 初始化导航栏布局
     */
    private void initNavigationTab() {
        LogUtil.i(TAG,"initNavigationTab...");

        tabHost = (FragmentTabHost) super.findViewById(R.id.tab_host);
        // tabHost.setBackgroundColor(Color.argb(5, 5, 5, 5));
        tabHost.setup(this, getSupportFragmentManager(), R.id.container_fl);
        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.setOnTabChangedListener(this);

        for (int i = 0; i < tabText.length; i++) {

            View view = LayoutInflater.from(this).inflate(R.layout.nav_tab, null);

            //设置按钮图标
            ((ImageView) view.findViewById(R.id.nav_tab_iv)).setImageResource(imageRes[i]);
            //设置按钮文字
            ((TextView) view.findViewById(R.id.nav_tab_tv)).setText(tabText[i]);

            TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabText[i]).setIndicator(view);
            tabHost.addTab(tabSpec, fragments[i], null);
            tabHost.setTag(i);
        }
    }


    /**
     * 自动把getCurrentTabView下的所有子View的selected状态设为true
     *
     * @param tabId
     */
    @Override
    public void onTabChanged(String tabId) {

        curTabIndex = tabHost.getCurrentTab(); //获取当前tab的位置
        //View view = tabHost.getCurrentTabView(); //获取当前tab的view

        //根据底部导航栏点击位置更新标题
        viewHolder.titleBarTextTV.setText(tabText[curTabIndex]);
        if (curTabIndex == 1 && !isRootFrag) {
            setRootTopBar(false);
        } else {
            setRootTopBar(true);
        }

        //获取整个底部Tab的布局,另 可以通过tabWidget.getChildCount和tabWidget.getChildAt来获取某个子View
        TabWidget tabWidget = tabHost.getTabWidget();
    }


    public void closeLeftMenu() {
        viewHolder.dragLayout.close();
        ViewHelper.setAlpha(viewHolder.topBarLO, 1);
        //通过拖拽实现主界面的遮罩
        ViewHelper.setAlpha(viewHolder.mainRootLO, 1);

        mApp.isLeftMenuClosed = true;
    }

    public void openLeftMenu() {
        viewHolder.dragLayout.open();
        mApp.isLeftMenuClosed = false;
    }


    public void setRootTopBar(boolean isRoot) {

        if (isRoot) {
            viewHolder.backIV.setVisibility(View.INVISIBLE);
            viewHolder.titleBarTextTV.setText(tabText[curTabIndex]);
            viewHolder.usrIV.setVisibility(View.VISIBLE);
        } else {
            viewHolder.usrIV.setVisibility(View.INVISIBLE);
            viewHolder.backIV.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 子fragment 监听返回键按下
     */
    private BackHandledFragment mBackHandedFragment;

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    int backPressedCounter = 0;

    @Override
    public void onBackPressed() {
        //fragment 为空 或者 fragment onBack 返回false
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

                /*
                移到后台，不退出，
                false——代表只有当前activity是task根，指应用启动的第一个activity时，才有效;
                true——则忽略这个限制，任何activity都可以有效。
                说明：判断Activity是否是task根，Activity本身相关方法：isTaskRoot()*/
                //moveTaskToBack(false);

                if (backPressedCounter == 0) {
                    ToastUtil.toastMsgShort("再按一次退出");
                    mHandler.sendEmptyMessageDelayed(Constant.HandlerFlag.RESET_BACK_PRESSED_COUNTER, 1000);
                    backPressedCounter++;
                }else{

                    super.onBackPressed();
                    ToastUtil.toastCanCel();
                    LogUtil.i(TAG,"app exit...");
                    System.exit(0);//直接结束程序
                    //或者
                    //android.os.Process.killProcess(android.os.Process.myPid());
                }

            } else {
                LogUtil.i(TAG, "getSupportFragmentManager().popBackStack()");
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    boolean showExitTip = true;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.HandlerFlag.LOGIN_SUCCESS:
                    if (!mApp.isLeftMenuClosed) {
                        closeLeftMenu();
                    }
                    initLeftMenuData();
                    break;
                case Constant.HandlerFlag.OPEN_LOGIN_ACTIVITY:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            UIUtil.switchActivity(MainActivity.this, LoginActivity.class, true);
                        }
                    }, 1000);
                    break;
                case Constant.HandlerFlag.CLOSE_LEFT_MENU:

                    closeLeftMenu();

                    break;
                case Constant.HandlerFlag.RESET_BACK_PRESSED_COUNTER:
                    backPressedCounter = 0;
                    break;
            }
        }
    };

    public static class ViewHolder extends ActivityViewHolder {
        //顶部控件
        protected LinearLayout topBarLO;
        public LinearLayout statusBarLO;
        public RelativeLayout titleBarRO;
        public ImageView usrIV;
        public ImageView backIV;
        public TextView titleBarTextTV;
        public TextView commitTV;

        public LinearLayout questionTypeChooserLO;
        public TextView singleChoiceTV;
        public TextView multipleChoiceTV;
        public TextView trueFalseTV;

        public FrameLayout containerFL;

        public DragLayout dragLayout;

        RelativeLayout leftMenuRootRO;
        ListView leftMenuLV;
        TextView leftMenuHideTVBtn;

        RelativeLayout mainRootLO;
        TextView mainShadowTV;
        TextView mainTopFlapTV;

        ViewHolder(Activity activity, boolean refind) {
            super(activity, refind);

        }

        @Override
        protected void findViews() {
            topBarLO = $(R.id.top_bar_lo);
            statusBarLO = $(R.id.status_bar_lo);
            titleBarRO = $(R.id.title_bar_ro);

            usrIV = $(R.id.titleBarUsr_iv);
            backIV = $(R.id.titleBarBack_iv);
            titleBarTextTV = $(R.id.titleBarText_tv);
            commitTV = $(R.id.commit_tv);

            questionTypeChooserLO = $(R.id.titleBar_questionType_chooser_lo);
            singleChoiceTV = $(R.id.single_choice_tv);
            multipleChoiceTV = $(R.id.multiple_choice_tv);
            trueFalseTV = $(R.id.true_false_tv);

            containerFL = $(R.id.container_fl);

            dragLayout = $(R.id.root_dl);

            leftMenuRootRO = $(R.id.left_menu_root_ro);
            leftMenuLV = $(R.id.left_menu_lv);
            leftMenuHideTVBtn = $(R.id.leftmenu_hide_tvbtn);

            mainRootLO = $(R.id.main_root_lo);
            mainShadowTV = $(R.id.main_shadow_tv);
            mainTopFlapTV = $(R.id.main_top_flap_tv);

        }
    }
}

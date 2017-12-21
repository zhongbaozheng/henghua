package com.hengdian.henghua.androidUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hengdian.henghua.R;

import java.lang.reflect.Field;

public class UIUtil {

    /**
     * Activity切换
     *
     * @param currentActivity      当前Activity
     * @param targetClazz          目标Activity class
     * @param closeCurrentActivity 是否关闭当前Activity
     */
    public static void switchActivity(Activity currentActivity, Class targetClazz, boolean closeCurrentActivity) {
        switchActivity(currentActivity, targetClazz, R.anim.push_left_in, R.anim.push_left_out, closeCurrentActivity);
    }

    /**
     * Activity切换
     *
     * @param currentActivity      当前Activity
     * @param targetClazz          目标Activity class
     * @param enterAnim            进入动画，负数使用系统默认动画
     * @param exitAnim             退出动画，负数使用系统默认动画
     * @param closeCurrentActivity 是否关闭当前Activity
     */
    public static void switchActivity(Activity currentActivity, Class targetClazz, int enterAnim, int exitAnim, boolean closeCurrentActivity) {

        LogUtil.d("switchActivity", currentActivity.getClass() + "-->" + targetClazz + ", enterAnim=" + enterAnim + ", exitAnim=" + exitAnim + ", closeCurrentActivity=" + closeCurrentActivity);

        if (enterAnim >= 0 && exitAnim >= 0)
            currentActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        currentActivity.startActivity(new Intent(currentActivity, targetClazz));

        if (closeCurrentActivity)
            currentActivity.finish();
    }


    /**
     * 初始化，沉浸式标题栏
     */
    public static void initImmersiveTitleBar(Activity activity, LinearLayout statusBarPadding) {
         /*
        布局文件加上,或设控件填充：
        android:fitsSystemWindows="true"
        android:clipToPadding="true"
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Window window = activity.getWindow();

            // 加上这句设置为全屏（状态栏不可见） 不加则只隐藏title
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //透明状态栏
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //透明导航栏，会导致app底部和系统虚拟按键重叠
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            if (statusBarPadding != null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBarPadding.getLayoutParams();
                params.height = UIUtil.getStatusBarHeight(activity);
                statusBarPadding.setLayoutParams(params);
                statusBarPadding.setVisibility(View.VISIBLE);
            }
        }
//        else if (statusBarPadding != null) {
//            statusBarPadding.setVisibility(View.GONE);
//        }
    }

    private static void setTitleBarPadding(Activity activity, View titleBar) {
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.statusBarLO.getLayoutParams();
//        params.height = UIUtil.getStatusBarHeight(this);
//        viewHolder.statusBarLO.setLayoutParams(params);


        // titleBar.setVisibility(View.VISIBLE);

//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
//        params.height = titleBar.getHeight()
//                + UIUtil.getStatusBarHeight(activity)
//                + UIUtil.dip2px(R.integer.title_padding_top
//                + R.integer.title_padding_bottom);
//
//        titleBar.setLayoutParams(params);

        titleBar.setPadding(UIUtil.dip2px(R.integer.title_padding_left), UIUtil.getStatusBarHeight(activity) + UIUtil.dip2px(R.integer.title_padding_top),
                UIUtil.dip2px(R.integer.title_padding_right), UIUtil.dip2px(R.integer.title_padding_bottom));

    }


    /**
     * EditText密码输入框，还没登录过时：按下显示，松手隐藏，登录过后始终不显示
     *
     * @param editText     文本输入框
     * @param event        动作事件
     * @param isFirstLogin 是否还没登录过
     */

    public static void pwdShouwHide(EditText editText, MotionEvent event, boolean isFirstLogin) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //按下，设置密码可见
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //松开，隐藏密码,设置文本 要一起写才能起作用
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.setSelection(editText.getText().toString().trim().length());//讲光标移至末尾
    }

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return 异常返回-1
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            /*
            获取ID方法二选一
             */
            //int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());

            if (id > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(id);
            }

        } catch (Exception e) {
            Log.e("", " Get StatusBar Height failed:" + e);
            statusBarHeight = 0;
        }
        return statusBarHeight;
    }

    /**
     * 获取状态栏高度
     *
     * @param view
     * @return
     */
    public static int getStatusBarHeight(View view) {
        if (view == null) {
            return 0;
        }
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取事件的动作名称
     *
     * @param event
     * @return
     */
    public static String getActionName(MotionEvent event) {
        String action = "unknow";
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                break;
            case MotionEvent.ACTION_SCROLL:
                action = "ACTION_SCROLL";
                break;
            case MotionEvent.ACTION_OUTSIDE:
                action = "ACTION_OUTSIDE";
                break;
            default:
                break;
        }
        return action;
    }


    /**
     * 根据布局文件ID获取view
     *
     * @param layoutID 布局文件id
     * @param root     根布局，如果提供root(不传null)时，
     *                 返回值其实就是这个root，这个方法就是把xml解析成view之后挂载在这个root下；
     *                 如果传null(不提供root)，返回值也是View，它就是xml布局里面的根节点
     * @return 布局文件转换成的view对象
     */
    public static View viewInflate(int layoutID, ViewGroup root) {
        return viewInflate(MyApplication.getAppContext(), layoutID, root);
    }

    /**
     * 根据布局文件ID获取view
     *
     * @param ctx      布局文件id
     * @param layoutID
     * @param root     根布局，如果提供root(不传null)时，
     *                 返回值其实就是这个root，这个方法就是把xml解析成view之后挂载在这个root下；
     *                 如果传null(不提供root)，返回值也是View，它就是xml布局里面的根节点
     * @return 布局文件转换成的view对象
     */
    public static View viewInflate(Context ctx, int layoutID, ViewGroup root) {
        //(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // LayoutInflater factory = LayoutInflater.from(ctx);
        return View.inflate(ctx, layoutID, root);
    }

    /**
     * @return 返回资源文件夹对象方法
     */
    public static Resources getResources() {
        return MyApplication.getAppContext().getResources();
    }

    /**
     * dip（dp ）: Density independent pixels ,与像素无关密度
     px        ： 像素
     dpi       ：dots per inch，像素密度一英寸多少个像素点。常见取值 120，160，240。
     density ： 密度，和标准dpi的比例（160px/inc）,160dpi的屏幕上，1dip=1px(density=1)
     分辨率   ： 横纵2个方向的像素点的数量，常见取值 480X800 ，320X480...
     屏幕尺寸： 屏幕对角线的长度。电脑电视同理。
     屏幕比例的问题。因为只确定了对角线长，2边长度还不一定。所以有了4：3、16：9这种，这样就可以算出屏幕边长了。

     像素密度决定比例关系
     * 1:0.75
     * 1:1
     * 1:1.5
     * 1:2
     * 1:3
     */

    /**
     * 获取当前手机的像素密度
     *
     * @return
     */
    public static float getDensity() {
        return getResources().getDisplayMetrics().density;
    }

    /**
     * dots-per-inch
     *
     * @return
     */
    public static float getDensityDpi() {
        return getResources().getDisplayMetrics().densityDpi;
    }

    public static float getScaledDensity() {
        return getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 屏幕横向像素
     *
     * @return
     */
    public static float getWidthPx() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 屏幕纵向像素
     *
     * @return
     */
    public static float getHeightPx() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * dp转换成px
     *
     * @param dip
     * @return
     */
    public static int dip2px(int dip) {
        return (int) (dip * getDensity() + 0.5);
    }

    /**
     * px转dp
     *
     * @param px
     * @return
     */
    public static int px2dip(int px) {
        return (int) (px / getDensity() + 0.5);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param px （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float px) {

        return (int) (px / getScaledDensity() + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param sp （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float sp) {
        return (int) (sp * getScaledDensity() + 0.5f);
    }

//    /**
//     * dip 转换成 px,
//     *
//     * @param dip
//     * @param context
//     * @return
//     */
//    public static float dip2Dimension(float dip, Context context) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,displayMetrics);
//    }

    /**
     * @param stringId 字符串在xml中对应R文件中的id
     * @return string.xml某节点, 对应的值
     */
    public static String getString(int stringId) {
        return getResources().getString(stringId);
    }

    /**
     * @param stringArrayId 字符串数组在xml中对应R文件中的id
     * @return 节点对应的内容
     */
    public static String[] getStringArray(int stringArrayId) {
        return getResources().getStringArray(stringArrayId);
    }

    public static Drawable getDrawable(int drawableId) {
        return getResources().getDrawable(drawableId);
    }

    /**
     * 将任务(可能在主线程中,也可能在子线程中)放置在主线程中运行的方法
     *
     * @param runnable 将任务保证在主线程中运行的方法
     */
    public static void runInMainThread(Runnable runnable) {
        //获取调用此方法所在的线程
        if (android.os.Process.myTid() == MyApplication.getAppThreadId()) {
            //如果上诉的runnable就是在主线程中要去执行的任务,则直接运行即可
            runnable.run();
        } else {
            //如果上诉的runnable运行在子线程中,将其传递到主线程中去做执行
            MyApplication.appHandler.post(runnable);
        }
    }


}

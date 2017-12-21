package com.hengdian.henghua.androidUtil;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Anderok on 2017/1/16.
 */

public class ToastUtil {

    /**
     * Toast对象
     */
    private static Toast toast = null;
    /**
     * 之前显示的内容
     */
    private static String oldMsg;
    /**
     * 第一次时间
     */
    private static long oneTime = 0;
    /**
     * 第二次时间
     */
    private static long twoTime = 0;


    public static void toastMsgShort(String msg) {
        showToast(MyApplication.getAppContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void toastMsgLong(String msg) {
        showToast(MyApplication.getAppContext(), msg, Toast.LENGTH_LONG);
    }

    public static void toastCanCel() {
        if(toast!=null){
            toast.cancel();
        }
    }

    /**
     * 显示Toast
     *
     * @param context
     * @param message
     * @param toastLength
     */
    private static void showToast(Context context, String message, int toastLength) {
        message+="";

        if (toast == null) {
            toast = Toast.makeText(context, message, toastLength);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > toastLength) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }
}


//toast = Toast.makeText(getApplicationContext(),
//"自定义位置Toast", Toast.LENGTH_LONG);
//toast.setGravity(Gravity.CENTER, 0, 0);
//toast.show();
//break;


//toast = Toast.makeText(getApplicationContext(),
//        "带图片的Toast", Toast.LENGTH_LONG);
//toast.setGravity(Gravity.CENTER, 0, 0);
//LinearLayout toastView = (LinearLayout) toast.getView();
//ImageView imageCodeProject = new ImageView(getApplicationContext());
//imageCodeProject.setImageResource(R.drawable.icon);
//toastView.addView(imageCodeProject, 0);
//toast.show();


//LayoutInflater inflater = getLayoutInflater();
//View layout = inflater.inflate(R.layout.custom,
//        (ViewGroup) findViewById(R.id.llToast));
//ImageView image = (ImageView) layout
//        .findViewById(R.id.tvImageToast);
//image.setImageResource(R.drawable.icon);
//TextView title = (TextView) layout.findViewById(R.id.tvTitleToast);
//title.setText("Attention");
//TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
//text.setText("完全自定义Toast");
//toast=new
//
//Toast(getApplicationContext()
//
//);
//toast.setGravity(Gravity.RIGHT|Gravity.TOP,12,40);
//toast.setDuration(Toast.LENGTH_LONG);
//toast.setView(layout);
//toast.show();




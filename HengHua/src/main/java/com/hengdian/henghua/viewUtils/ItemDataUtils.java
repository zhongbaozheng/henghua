package com.hengdian.henghua.viewUtils;


import com.hengdian.henghua.R;
import com.hengdian.henghua.model.viewBean.ItemBean;

import java.util.ArrayList;
import java.util.List;

public class ItemDataUtils {

    /**
     * 左侧菜单列表项
     *
     * @return
     */
    public static List<ItemBean> getLeftMenuItemBeans() {
        List<ItemBean> itemBeans = new ArrayList<>();
        itemBeans.add(new ItemBean(R.drawable.leftmenu_usrname, "姓名：", false));
        itemBeans.add(new ItemBean(R.drawable.leftmenu_account, "账号：", false));
        itemBeans.add(new ItemBean(R.drawable.leftmenu_update_pwd, "修改密码", true));
        itemBeans.add(new ItemBean(R.drawable.leftmenu_logout, "注销登录", true));
        return itemBeans;
    }

}

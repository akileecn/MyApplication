package cn.aki.zhbj.data.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/4.
 * 分类
 */
public class Categories {
    //空对象
    public static final Categories EMPTY_DATA;
    static{
        EMPTY_DATA=new Categories();
        EMPTY_DATA.data=new ArrayList<>();
    }
    public int retcode;
    public List<ParentMenu> data;
    public int[] extend;

    /**
     * 菜单
     */
    public static class Menu{
        public int id;
        public String title;
        public int type;
        public String url;
    }

    /**
     * 父菜单
     */
    public static class ParentMenu extends Menu {
        public List<Menu> children;
    }
}

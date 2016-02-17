package cn.aki.zhbj.data.response;

import java.util.List;

/**
 * Created by Administrator on 2016/2/17.
 * 新闻列表数据
 */
public class NewsData {

    public int retcode;
    public Content data;

    public static class Content{
        public String countcommenturl;
        public String more;
        public List<ListNews> news;
        public String title;
        public List<Topic> topic;
        public List<TopNews> topnews;
    }

    /**
     * 列表新闻
     */
    public static class ListNews extends BaseNews{
        public String listimage;
    }

    public static class Topic{
        public String description;
        public int id;
        public String listimage;
        public int sort;
        public String title;
        public String url;
    }

    /**
     * 热点新闻
     */
    public static class TopNews extends BaseNews{
        public String topimage;
    }

    /**
     * 新闻基类
     */
    public static abstract class BaseNews{
        public boolean comment;
        public String commentlist;
        public String commenturl;
        public int id;
        public String pubdate;
        public String title;
        public String type;
        public String url;
    }

}

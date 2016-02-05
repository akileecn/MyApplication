package cn.aki.zhbj.data.response;


import java.util.ArrayList;
import java.util.Arrays;
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
    private int retcode;
    private List<ParentNode> data;
    private int[] extend;

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public List<ParentNode> getData() {
        return data;
    }

    public void setData(List<ParentNode> data) {
        this.data = data;
    }

    public int[] getExtend() {
        return extend;
    }

    public void setExtend(int[] extend) {
        this.extend = extend;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "retcode=" + retcode +
                ", data=" + data +
                ", extend=" + Arrays.toString(extend) +
                '}';
    }

    /**
     * 节点
     */
    public static class Node{
        private int id;
        private String title;
        private int type;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    /**
     * 父节点
     */
    public static class ParentNode extends Node{
        private List<Node> children;

        public List<Node> getChildren() {
            return children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            return "ParentNode{" +
                    super.toString()+
                    ",children=" + children +
                    '}';
        }
    }
}

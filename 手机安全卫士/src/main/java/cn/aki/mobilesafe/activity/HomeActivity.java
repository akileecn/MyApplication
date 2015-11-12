package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/11/10.
 * 首页面
 */
public class HomeActivity extends Activity{
    private GridView glItem;
    private String[] items={"手机防盗","通讯卫士","软件管理"
                            ,"进程管理","流量统计","手机杀毒"
                            ,"缓存清理","高级工具","设置中心"};
    private int[] images={R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps
                        ,R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan
                        ,R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        glItem= (GridView) findViewById(R.id.gl_item);
        glItem.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(HomeActivity.this,R.layout.home_item,null);
            ImageView imageView= (ImageView) view.findViewById(R.id.item_img);
            TextView textView= (TextView) view.findViewById(R.id.item_name);
            imageView.setImageResource(images[position]);
            textView.setText(items[position]);
            return view;
        }
    }
}

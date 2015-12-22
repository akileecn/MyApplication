package cn.aki.mobilesafe.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/12/22.
 * 自定义适配器基类
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;

    protected MyBaseAdapter(List<T> list){
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}

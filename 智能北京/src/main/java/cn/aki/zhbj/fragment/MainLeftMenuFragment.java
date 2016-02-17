package cn.aki.zhbj.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.page.BasePage;

/**
 * Created by Administrator on 2016/2/3.
 * 主页面左菜单部件
 */
public class MainLeftMenuFragment extends Fragment {
    private ListView lvMenu;//菜单列表
    List<Categories.ParentMenu> mDataList;
    private BaseAdapter mMyMenuAdapter;
    private int mCurrentPosition;
    private SlidingMenu mMenu;//活动菜单
    private BasePage mCurrentPage;//当前所属页面

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main_left_menu,container,false);
        lvMenu= (ListView) rootView.findViewById(R.id.lv_menu);
        return rootView;
    }

    /**
     * 更新列表
     */
    public void updateList(BasePage page,Categories categories){
        mCurrentPage=page;
        mDataList=categories.data;
        mCurrentPosition=0;
        if(mMyMenuAdapter==null){
            mMyMenuAdapter=new MyMenuAdapter();
            lvMenu.setAdapter(mMyMenuAdapter);
            lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //更新菜单
                    mCurrentPosition=position;
                    mMyMenuAdapter.notifyDataSetChanged();
                    if(mMenu!=null&&mMenu.isMenuShowing()){
                        mMenu.toggle();
                    }
                    //更新内容页
                    mCurrentPage.updateContent(position);
                }
            });
        }
    }

    private class MyMenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Categories.ParentMenu getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=View.inflate(getActivity(),R.layout.item_main_menu,null);
            }
            TextView tvName= (TextView) convertView.findViewById(R.id.tv_name);
            tvName.setText(getItem(position).title);
            //根据是否被选中修改样式
            tvName.setEnabled(position==mCurrentPosition);
            return convertView;
        }
    }

    /**
     * 设置控制的菜单
     */
    public void setSlidingMenu(SlidingMenu menu) {
        mMenu = menu;
    }
}

package cn.aki.mobilesafe.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.adapter.MyBaseAdapter;
import cn.aki.mobilesafe.bean.AppInfo;
import cn.aki.mobilesafe.db.dao.AppLockDao;
import cn.aki.mobilesafe.manager.AppInfoManager;

/**
 * Created by Administrator on 2016/1/26.
 * 加锁应用fragment基类
 */
public class BaseLockAppFragment extends Fragment{

    private TextView tvCountTitle;//应用数统计
    private ListView lvAppList;//应用列表
    private List<AppInfo> mAppInfoList;
    private BaseAdapter mAdapter;
    private AppLockDao mAppLockDao;
    private Handler mHandler=new Handler();
    private boolean mIsLock;//是否为加锁
    private DiffHelper mHelper;//榜示

    public BaseLockAppFragment(boolean isLock) {
        super();
        mIsLock = isLock;
    }

    /**
     * 初始化view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //false表示注入时不依附到父视图上,不然会重复依附导致异常
        View view=inflater.inflate(R.layout.fragment_unlock_app,container,false);
        tvCountTitle= (TextView) view.findViewById(R.id.tv_count_title);
        lvAppList= (ListView) view.findViewById(R.id.lv_app_list);
        return view;
    }

    /**
     * 每次加载是调用
     */
    @Override
    public void onStart() {
        super.onStart();
        mAppLockDao=new AppLockDao(getContext());
        mHelper=new DiffHelper();
        //获得数据
        mAppInfoList=mHelper.getAppInfoList();
        mAdapter=new MyAdapter(mAppInfoList);
        lvAppList.setAdapter(mAdapter);
    }

    /**
     * 适配器
     */
    private class MyAdapter extends MyBaseAdapter<AppInfo>{
        protected MyAdapter(List<AppInfo> list) {
            super(list);
        }

        @Override
        public int getCount() {
            int count=super.getCount();
            //每次适配器更新时,修改显示的应用总数
            tvCountTitle.setText(mHelper.getCountTitle(count));
            return count;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                convertView=View.inflate(getContext(),R.layout.item_unlock_app,null);
                holder=new ViewHolder();
                holder.btnLock= (Button) convertView.findViewById(R.id.btn_lock);
                holder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvAppName= (TextView) convertView.findViewById(R.id.tv_app_name);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            final AppInfo info= (AppInfo) getItem(position);
            holder.ivIcon.setImageDrawable(info.getIcon());
            holder.tvAppName.setText(info.getAppName());
            //设置按钮背景
            holder.btnLock.setBackgroundResource(mHelper.getLockButtonBackgroundResource());
            final View finalConvertView = convertView;
            holder.btnLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //动画
                    TranslateAnimation anim=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,1F
                            ,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                    anim.setDuration(1000);
                    finalConvertView.startAnimation(anim);
                    //延迟刷新
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //额外点击事件处理
                            mHelper.onLockButtonClick(info);
                            //从列表中移除
                            mAppInfoList.remove(info);
                            mAdapter.notifyDataSetChanged();
                        }
                    },1000);
                }
            });
            return convertView;
        }
        private class ViewHolder{
            private ImageView ivIcon;//图标
            private TextView tvAppName;//应用名
            private Button btnLock;//加锁
        }
    }

    /**
     * 差异统一处理
     */
    private class DiffHelper{
        /**
         * 获得数据源
         */
        private List<AppInfo> getAppInfoList(){
            AppInfoManager appInfoManager=new AppInfoManager(getContext());
            return appInfoManager.getLockList(mIsLock);
        }
        /**
         * 设置统计标题
         */
        private String getCountTitle(int count){
            return (mIsLock?"已":"未")+"加锁应用("+count+")个";
        }

        /**
         * 设置按钮背景
         * @return 资源文件ID
         */
        private int getLockButtonBackgroundResource(){
            if(mIsLock){
                return R.drawable.selector_btn_unlock;
            }else{
                return R.drawable.selector_btn_lock;
            }
        }

        /**
         * 按钮点击事件
         */
        private void onLockButtonClick(AppInfo info){
            if(mIsLock){
                mAppLockDao.delete(info.getPackageName());
            }else{
                mAppLockDao.add(info.getPackageName());
            }
        }

    }
}

package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.adapter.MyBaseAdapter;
import cn.aki.mobilesafe.bean.AppInfo;
import cn.aki.mobilesafe.manager.AppInfoManager;

/**
 * Created by Administrator on 2016/1/28.
 * 清理缓存
 */
public class CleanCacheActivity extends Activity {
    private static final int WHAT_DATA_LOADED=1;
    private static final int WHAT_CACHE_CLEARED=2;

    private ListView lvApp;//应用列表
    private Button btnClearAll;//清理全部
    private List<AppInfo> mAppInfoList;
    private BaseAdapter mAdapter;

    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_DATA_LOADED:
                    lvApp.setAdapter(mAdapter);
                    break;
                case WHAT_CACHE_CLEARED:
                    Toast.makeText(CleanCacheActivity.this,"缓存清理完成",Toast.LENGTH_SHORT).show();
                    mAppInfoList.clear();
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initUI(){
        setContentView(R.layout.activity_clean_cache);
        lvApp= (ListView) findViewById(R.id.lv_app);
        btnClearAll= (Button) findViewById(R.id.btn_clear_all);
        //清理全部缓存
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager=getPackageManager();
                try {
                    Method method= PackageManager.class.getMethod("freeStorageAndNotify",long.class, IPackageDataObserver.class);
                    method.invoke(packageManager, Long.MAX_VALUE, new IPackageDataObserver.Stub(){
                        //清理完成时调用
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                            mHandler.sendEmptyMessage(WHAT_CACHE_CLEARED);
                        }
                    });
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(){
        final AppInfoManager appInfoManager=new AppInfoManager(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppInfoList=appInfoManager.getCacheList();
                mAdapter=new MyAdapter(mAppInfoList);
                mHandler.sendEmptyMessage(WHAT_DATA_LOADED);
            }
        }).start();
    }

    /**
     * 适配器
     */
    private class MyAdapter extends MyBaseAdapter<AppInfo>{

        protected MyAdapter(List<AppInfo> list) {
            super(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                convertView=View.inflate(CleanCacheActivity.this,R.layout.item_clean_cache,null);
                holder=new ViewHolder();
                holder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvAppName= (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tvCacheSize= (TextView) convertView.findViewById(R.id.tv_cache_size);
                holder.btnClear= (Button) convertView.findViewById(R.id.btn_clear);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo= (AppInfo) getItem(position);
            holder.tvAppName.setText(appInfo.getAppName());
            holder.tvCacheSize.setText("缓存 "+Formatter.formatFileSize(CleanCacheActivity.this,appInfo.getSize()));
            holder.ivIcon.setImageDrawable(appInfo.getIcon());
            holder.btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+appInfo.getPackageName()));
                    startActivity(intent);
                }

            });
            return convertView;
        }

        private class ViewHolder{
            private ImageView ivIcon;
            private TextView tvAppName;
            private TextView tvCacheSize;
            private Button btnClear;
        }
    }

}
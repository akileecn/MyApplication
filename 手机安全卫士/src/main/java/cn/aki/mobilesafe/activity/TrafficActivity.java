package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.adapter.MyBaseAdapter;
import cn.aki.mobilesafe.bean.AppInfo;
import cn.aki.mobilesafe.manager.AppInfoManager;

/**
 * Created by Administrator on 2016/1/29.
 * 流量统计
 */
public class TrafficActivity extends Activity {
    private ListView lvApp;
    private List<TrafficAppInfo> mAppInfoList;
    private BaseAdapter mAdapter;

    private static final int WHAT_DATA_LOADED=1;
    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                //加载数据完成
                case WHAT_DATA_LOADED:
                    mAdapter=new MyAdapter(mAppInfoList);
                    lvApp.setAdapter(mAdapter);
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
        setContentView(R.layout.activity_traffic);
        lvApp= (ListView) findViewById(R.id.lv_app);
    }

    private void initData(){
        final AppInfoManager appInfoManager=new AppInfoManager(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppInfoList=appInfoManager.baseGetList(appInfoManager.new OnLoadListener<TrafficAppInfo>(){
                    @Override
                    public void onLooping(PackageInfo packageInfo, List<TrafficAppInfo> appInfoList) {
                        TrafficAppInfo appInfo=new TrafficAppInfo();
                        initAppBaseInfo(packageInfo, appInfo);
                        int uid=packageInfo.applicationInfo.uid;
                        //提交的流量
                        //设备不支持时返回TrafficStats.UNSUPPORTED(-1)
                        long uploadSize=TrafficStats.getUidTxBytes(uid);
                        if(uploadSize!=TrafficStats.UNSUPPORTED){
                            appInfo.setUploadSize(uploadSize);
                        }
                        //接收的流量
                        long downloadSize=TrafficStats.getUidRxBytes(uid);
                        if(downloadSize!=TrafficStats.UNSUPPORTED){
                            appInfo.setDownloadSize(downloadSize);
                        }
                        appInfoList.add(appInfo);
                    }
                });
                mHandler.sendEmptyMessage(WHAT_DATA_LOADED);
            }
        }).start();
    }

    /**
     * 带流量的应用信息
     */
    private class TrafficAppInfo extends AppInfo{
        private long uploadSize;//上传
        private long downloadSize;//下载

        /**
         * 计算总流量 uploadSize+downloadSize
         * @return 总流量
         */
        public long getTotalSize() {
            return uploadSize+downloadSize;
        }

        public long getUploadSize() {
            return uploadSize;
        }

        public void setUploadSize(Long uploadSize) {
            this.uploadSize = uploadSize;
        }

        public long getDownloadSize() {
            return downloadSize;
        }

        public void setDownloadSize(Long downloadSize) {
            this.downloadSize = downloadSize;
        }
    }

    /**
     * 适配器
     */
    private class MyAdapter extends MyBaseAdapter<TrafficAppInfo>{

        protected MyAdapter(List<TrafficAppInfo> list) {
            super(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(TrafficActivity.this,R.layout.item_traffic,null);
                holder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvAppName= (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tvDownload= (TextView) convertView.findViewById(R.id.tv_download);
                holder.tvUpload= (TextView) convertView.findViewById(R.id.tv_upload);
                holder.tvTotal= (TextView) convertView.findViewById(R.id.tv_total);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            TrafficAppInfo appInfo= (TrafficAppInfo) getItem(position);
            holder.tvTotal.setText(Formatter.formatFileSize(TrafficActivity.this,appInfo.getTotalSize()));
            holder.tvDownload.setText("下载"+Formatter.formatFileSize(TrafficActivity.this,appInfo.getDownloadSize()));
            holder.tvUpload.setText("上传"+Formatter.formatFileSize(TrafficActivity.this,appInfo.getUploadSize()));
            holder.tvAppName.setText(appInfo.getAppName());
            holder.ivIcon.setImageDrawable(appInfo.getIcon());
            return convertView;
        }

        private class ViewHolder{
            private ImageView ivIcon;//图标
            private TextView tvAppName;//应用名
            private TextView tvTotal;//总流量
            private TextView tvDownload;//下载流量
            private TextView tvUpload;//上传流量
        }
    }
}

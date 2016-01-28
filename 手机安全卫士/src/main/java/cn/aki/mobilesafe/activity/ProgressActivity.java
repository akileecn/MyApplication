package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.bean.ProgressInfo;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.manager.ProgressInfoManager;
import cn.aki.mobilesafe.utils.SystemInfoUtils;
import cn.aki.mobilesafe.utils.UiUtils;

/**
 * Created by Administrator on 2016/1/11.
 * 进程管理
 */
public class ProgressActivity extends Activity{
    private TextView tvProgressCount;
    private TextView tvMemoryCount;
    private ListView lvProgress;
    private ProgressInfoManager mProgressInfoManager;
    private List<ProgressInfo> mProgressInfoList;
    private List<ProgressInfo> mSystemList;
    private List<ProgressInfo> mUserList;
    private BaseAdapter mAdapter;
    private long mAvailMemory;
    private long mTotalMemory;
    private SharedPreferences mPref;
    private static final int WHAT_LOAD_PROGRESS_INFO=1; //进程数据加载完成
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                //进程信息加载完成
                case WHAT_LOAD_PROGRESS_INFO:
                    tvProgressCount.setText("运行中的进程" + mProgressInfoList.size() + "个");
                    //初次
                    if(mAdapter==null){
                        mAdapter=new MyAdapter();
                        lvProgress.setAdapter(mAdapter);
                    }else{
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initEvent();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    /**
     * 初始化UI
     */
    private void initUI(){
        setContentView(R.layout.activity_progress);
        tvProgressCount= (TextView) findViewById(R.id.tv_progress_count);
        tvMemoryCount= (TextView) findViewById(R.id.tv_memory_count);
        lvProgress= (ListView) findViewById(R.id.lv_progress);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG,Context.MODE_PRIVATE);
        mProgressInfoManager=new ProgressInfoManager(this);
    }

    /**
     * 绑定事件
     */
    private void initEvent(){
        lvProgress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    return;
                }
                //设置勾选状态
                ProgressInfo pi = (ProgressInfo) parent.getItemAtPosition(position);
                boolean checked = pi.getChecked();
                MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder) view.getTag();
                holder.cbCheck.setChecked(!checked);
                pi.setChecked(!checked);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(){
        /**内存信息*/
        long[] memory= SystemInfoUtils.getMemoryInfo(this);
        mAvailMemory=memory[0];
        mTotalMemory=memory[1];
        tvMemoryCount.setText("剩余/总内存:"+Formatter.formatFileSize(this,mAvailMemory)
                +"/"+Formatter.formatFileSize(this,mTotalMemory));
        /**进程列表*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProgressInfoList=mProgressInfoManager.getList();
                //分组
                mSystemList=new ArrayList<>();
                mUserList=new ArrayList<>();
                boolean showSystem=mPref.getBoolean(Constants.SharedPreferences.KEY_SHOW_SYSTEM_PROGRESS,false);
                //显示系统进程
                if(showSystem){
                    for(ProgressInfo pi:mProgressInfoList){
                        if(pi.getIsSystem()){
                            mSystemList.add(pi);
                        }else{
                            mUserList.add(pi);
                        }
                    }
                }else{
                    //不显示
                    for(ProgressInfo pi:mProgressInfoList){
                        if(!pi.getIsSystem()){
                            mUserList.add(pi);
                        }
                    }
                    mProgressInfoList=mUserList;
                }
                handler.sendEmptyMessage(WHAT_LOAD_PROGRESS_INFO);
            }
        }).start();
    }

    /**
     * 适配器
     */
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            //不显示系统应用的话
            if(mSystemList==null||mSystemList.size()==0){
                return mProgressInfoList.size()+1;
            }else{
                return mProgressInfoList.size()+2;
            }
        }

        @Override
        public Object getItem(int position) {
            int userCount=mUserList.size();
            if(position==0){
                return "用户应用("+mUserList.size()+")";
            }else if(position<userCount+1){
                return mUserList.get(position-1);
            }else if(position==userCount+1){
                return "系统应用("+mSystemList.size()+")";
            }else{
                return mSystemList.get(position-userCount-2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object item=getItem(position);
            if(item instanceof ProgressInfo){
                ViewHolder viewHolder;
                //被重用的view为null或为特殊view
                if(convertView==null||convertView instanceof TextView){
                    convertView=View.inflate(ProgressActivity.this,R.layout.item_progress_info,null);
                    viewHolder=new ViewHolder();
                    viewHolder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
                    viewHolder.tvAppName= (TextView) convertView.findViewById(R.id.tv_app_name);
                    viewHolder.tvMemoryCount= (TextView) convertView.findViewById(R.id.tv_memory_count);
                    viewHolder.cbCheck= (CheckBox) convertView.findViewById(R.id.cb_check);
                    convertView.setTag(viewHolder);
                }else{
                    viewHolder= (ViewHolder) convertView.getTag();
                }
                //保证获取的对象一致
                ProgressInfo pi= (ProgressInfo) item;
                viewHolder.tvAppName.setText(pi.getAppName());
                viewHolder.tvMemoryCount.setText("内存占用:" + Formatter.formatFileSize(ProgressActivity.this, pi.getMemorySize()));
                viewHolder.ivIcon.setImageDrawable(pi.getIcon());
                //隐藏自身的checkBox
                if(isSelf(pi)){
                    viewHolder.cbCheck.setVisibility(View.INVISIBLE);
                }else{
                    viewHolder.cbCheck.setVisibility(View.VISIBLE);
                    //设置checkBox状态
                    viewHolder.cbCheck.setChecked(pi.getChecked());
                }
                return convertView;
            }else{
                //特殊view
                String text=item.toString();
                TextView view=new TextView(ProgressActivity.this);
                view.setBackgroundColor(Color.GRAY);
                view.setTextColor(Color.WHITE);
                view.setText(text);
                return view;
            }
        }

        private class ViewHolder{
            private ImageView ivIcon;
            private TextView tvAppName;
            private TextView tvMemoryCount;
            private CheckBox cbCheck;
        }
    }

    /**
     * 全选
     */
    public void selectAll(View view){
        selectItem(true);
    }

    /**
     * 反选
     */
    public void selectOpposite(View view){
        selectItem(false);
    }

    /**
     * 选择条目
     * @param flag true全选 false反选
     */
    private void selectItem(boolean flag){
        if(mProgressInfoList!=null){
            for(ProgressInfo pi:mProgressInfoList){
                if(flag){
                    pi.setChecked(true);
                }else{
                    pi.setChecked(!pi.getChecked());
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 是否为自身
     * @param pi 应用信息
     */
    private boolean isSelf(ProgressInfo pi){
        return getPackageName().equals(pi.getProgressName());
    }

    /**
     * 清理进程
     */
    public void killProgress(View view){
        if(mProgressInfoList!=null){
            int progressCount=0;
            long clearMemory=0;
            ActivityManager am= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            Iterator<ProgressInfo> it=mProgressInfoList.iterator();
            ProgressInfo pi;
            while(it.hasNext()){
                pi=it.next();
                //不清理自己
                if(isSelf(pi)){
                    continue;
                }
                if(pi.getChecked()){
                    //杀死进程
                    am.killBackgroundProcesses(pi.getProgressName());
                    //总列表、部分列表中都移除
                    it.remove();
                    if(pi.getIsSystem()){
                        mSystemList.remove(pi);
                    }else{
                        mUserList.remove(pi);
                    }
                    progressCount++;
                    clearMemory+=pi.getMemorySize();
                }
            }
            mAdapter.notifyDataSetChanged();
            UiUtils.showToast(this, "清理" + progressCount + "个进程，释放" + Formatter.formatFileSize(this, clearMemory) + "内存");
            mAvailMemory+=clearMemory;
            tvProgressCount.setText("运行中的进程" + mProgressInfoList.size() + "个");
            tvMemoryCount.setText("剩余/总内存:" + Formatter.formatFileSize(this, mAvailMemory)
                    + "/" + Formatter.formatFileSize(this, mTotalMemory));
        }
    }

    /**
     * 跳转至设置页
     */
    public void toSetting(View view){
        startActivity(new Intent(this,ProgressSettingActivity.class));
    }
}

package cn.aki.mobilesafe.activity;

import android.app.Activity;
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
import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.bean.ProgressInfo;
import cn.aki.mobilesafe.manager.ProgressInfoManager;
import cn.aki.mobilesafe.utils.SystemInfoUtils;

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
    private static final int WHAT_LOAD_PROGRESS_INFO=1;
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_LOAD_PROGRESS_INFO:
                    tvProgressCount.setText("运行中的进程" + mProgressInfoList.size() + "个");
                    lvProgress.setAdapter(new MyAdapter());
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
        initData();
        initEvent();
    }

    /**
     * 初始化UI
     */
    private void initUI(){
        setContentView(R.layout.activity_progress);
        tvProgressCount= (TextView) findViewById(R.id.tv_progress_count);
        tvMemoryCount= (TextView) findViewById(R.id.tv_memory_count);
        lvProgress= (ListView) findViewById(R.id.lv_progress);
    }

    /**
     * 绑定事件
     */
    private void initEvent(){
        lvProgress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view instanceof TextView){
                    return;
                }
                //设置勾选状态
                ProgressInfo pi= (ProgressInfo) parent.getItemAtPosition(position);
                boolean checked=pi.getChecked();
                MyAdapter.ViewHolder holder= (MyAdapter.ViewHolder) view.getTag();
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
        tvMemoryCount.setText("剩余/总内存:"+Formatter.formatFileSize(this,memory[0])
                +"/"+Formatter.formatFileSize(this,memory[1]));
        /**进程列表*/
        mProgressInfoManager=new ProgressInfoManager(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProgressInfoList=mProgressInfoManager.getList();
                //分组
                mSystemList=new ArrayList<>();
                mUserList=new ArrayList<>();
                for(ProgressInfo pi:mProgressInfoList){
                    if(pi.getIsSystem()){
                        mSystemList.add(pi);
                    }else{
                        mUserList.add(pi);
                    }
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
            return mProgressInfoList.size()+2;
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
                    viewHolder.tvApkName= (TextView) convertView.findViewById(R.id.tv_apk_name);
                    viewHolder.tvMemoryCount= (TextView) convertView.findViewById(R.id.tv_memory_count);
                    viewHolder.cbCheck= (CheckBox) convertView.findViewById(R.id.cb_check);
                    convertView.setTag(viewHolder);
                }else{
                    viewHolder= (ViewHolder) convertView.getTag();
                }
                //保证获取的对象一致
                ProgressInfo pi= (ProgressInfo) item;
                viewHolder.tvApkName.setText(pi.getAppName());
                viewHolder.tvMemoryCount.setText("内存占用:" + Formatter.formatFileSize(ProgressActivity.this, pi.getMemorySize()));
                viewHolder.ivIcon.setImageDrawable(pi.getIcon());
                //设置checkBox状态
                viewHolder.cbCheck.setChecked(pi.getChecked());
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
            private TextView tvApkName;
            private TextView tvMemoryCount;
            private CheckBox cbCheck;
        }
    }
}

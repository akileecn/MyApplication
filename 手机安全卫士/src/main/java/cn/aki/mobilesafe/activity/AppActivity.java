package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.bean.AppInfo;
import cn.aki.mobilesafe.manager.AppInfoManager;

/**
 * Created by Administrator on 2015/12/28.
 * 软件管理
 */
public class AppActivity extends Activity {
    private TextView tvRomRemain;
    private TextView tvSdRemain;
    private TextView tvAppCount;
    private ListView lvApp;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mSystemList;
    private List<AppInfo> mUserList;
    private PopupWindow mPopupWindow;
    private AppInfo mClickedAppInfo;

    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            lvApp.setAdapter(new MyAdaptor());
            tvAppCount.setText("用户应用("+mUserList.size()+")");
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initUI(){
        setContentView(R.layout.activity_app);
        //存储空间
        tvRomRemain= (TextView) findViewById(R.id.tv_rom_remain);
        tvSdRemain= (TextView) findViewById(R.id.tv_sd_remain);
        long romRemain=Environment.getDataDirectory().getFreeSpace();
        long sdRemain=Environment.getExternalStorageDirectory().getFreeSpace();
        tvRomRemain.setText("内存可用:"+ Formatter.formatFileSize(this,romRemain));
        tvSdRemain.setText("SD卡可用:"+Formatter.formatFileSize(this,sdRemain));

        //软件列表
        lvApp= (ListView) findViewById(R.id.lv_app);
        tvAppCount= (TextView) findViewById(R.id.tv_app_count);
        //动态显示应用统计
        lvApp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mUserList!=null){
                    mPopupWindow.dismiss();
                    if(firstVisibleItem<mUserList.size()+1){
                        tvAppCount.setText("用户应用("+mUserList.size()+")");
                    }else{
                        tvAppCount.setText("系统应用("+mSystemList.size()+")");
                    }
                }
            }
        });

        //view和大小必须设置，不然显示不出来
        final View contentView=View.inflate(AppActivity.this,R.layout.popup_app_menu,null);
        mPopupWindow=new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        //不设置背景的话动画无效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //相对自己的缩放动画
        final ScaleAnimation scaleAnimation=new ScaleAnimation(0.5F,1.0F,0.5F,1.0F, Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF);
        scaleAnimation.setDuration(500L);
        //监听点击
        MyOnClickListener myOnClickListener=new MyOnClickListener();
        contentView.findViewById(R.id.tv_uninstall).setOnClickListener(myOnClickListener);
        contentView.findViewById(R.id.tv_run).setOnClickListener(myOnClickListener);
        contentView.findViewById(R.id.tv_share).setOnClickListener(myOnClickListener);
        contentView.findViewById(R.id.tv_detail).setOnClickListener(myOnClickListener);
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object clickedObject=lvApp.getItemAtPosition(position);
                //仅应用条目有效
                if(clickedObject!=null&&clickedObject instanceof AppInfo){
                    //保存被点击元素
                    mClickedAppInfo= (AppInfo) clickedObject;
                    //获得view在屏幕上的坐标
                    int[] location=new int[2];
                    view.getLocationInWindow(location);
                    //dismiss之后重新定位才生效
                    mPopupWindow.dismiss();
                    mPopupWindow.showAtLocation(parent, Gravity.START | Gravity.TOP, 50, location[1]);
                    contentView.startAnimation(scaleAnimation);
                }
            }
        });
    }

    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppInfoList= new AppInfoManager(AppActivity.this).getList();
                //应用类型分组
                mSystemList=new ArrayList<>();
                mUserList=new ArrayList<>();
                for(AppInfo appInfo:mAppInfoList){
                    if(appInfo.getIsSystem()){
                        mSystemList.add(appInfo);
                    }else{
                        mUserList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 应用软件列表适配器
     */
    private class MyAdaptor extends BaseAdapter{

        @Override
        public int getCount() {
            return mAppInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            int userSize=mUserList.size();
            if(position<userSize){
                return mUserList.get(position);
            }else if(position==userSize){
                return null;
            }else{
                return mSystemList.get(position-userSize-1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position==mUserList.size()){
                TextView view=new TextView(AppActivity.this);
                view.setBackgroundColor(Color.GRAY);
                view.setTextColor(Color.WHITE);
                view.setText("系统应用("+mSystemList.size()+")");
                return view;
            }else{
                ViewHolder viewHolder;
                //被重用的view为null或为特殊view
                if(convertView==null||convertView instanceof TextView){
                    convertView=View.inflate(AppActivity.this,R.layout.item_app_info,null);
                    viewHolder=new ViewHolder();
                    viewHolder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
                    viewHolder.tvAppName= (TextView) convertView.findViewById(R.id.tv_app_name);
                    viewHolder.tvLocation= (TextView) convertView.findViewById(R.id.tv_location);
                    viewHolder.tvSize= (TextView) convertView.findViewById(R.id.tv_size);
                    convertView.setTag(viewHolder);
                }else{
                    viewHolder= (ViewHolder) convertView.getTag();
                }
                //保证获取的对象一致
                AppInfo appInfo= (AppInfo) getItem(position);
                viewHolder.tvLocation.setText(appInfo.getLocation());
                viewHolder.tvAppName.setText(appInfo.getAppName());
                viewHolder.tvSize.setText(Formatter.formatFileSize(AppActivity.this, appInfo.getSize()));
                viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
                return convertView;
            }
        }

        private class ViewHolder{
            private ImageView ivIcon;
            private TextView tvAppName;
            private TextView tvLocation;
            private TextView tvSize;
        }

    }

    /**
     * 弹出窗口点击监听
     */
    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String packageName=mClickedAppInfo.getPackageName();
            Intent intent=null;
            switch (v.getId()){
                case R.id.tv_uninstall:
                    intent=new Intent(Intent.ACTION_DELETE, Uri.parse("package:"+packageName));
                    break;
                case R.id.tv_run:
                    //可能不存在
                    intent=getPackageManager().getLaunchIntentForPackage(packageName);
                    break;
                case R.id.tv_share:
                    intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    //各手机软件助手为解析https://play.google.com/store/apps/details?id=开头的连接
                    intent.putExtra(Intent.EXTRA_TEXT
                            , "Hi！推荐您使用软件：" + mClickedAppInfo.getAppName() + "下载地址:https://play.google.com/store/apps/details?id=" + packageName);
                    break;
                case R.id.tv_detail:
                    intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+packageName));
                default:
                    break;
            }
            if(intent!=null){
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mPopupWindow.dismiss();
        super.onDestroy();
    }
}

package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Random;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.bean.VirusInfo;
import cn.aki.mobilesafe.common.Constants;
import cn.aki.mobilesafe.db.dao.AntivirusDao;
import cn.aki.mobilesafe.utils.Md5Utils;

/**
 * Created by Administrator on 2016/1/15.
 * 查毒
 */
public class AntivirusActivity extends Activity{
    private ImageView ivScan;
    private ProgressBar pbScan;
    private TextView tvLog;
    private ScrollView svLog;
    private static final int WHAT_SCANNING=1;   //正在扫描
    private static final int WHAT_SCANNED=2;  //扫描完成
    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                /**扫描中*/
                case WHAT_SCANNING:
                    ScanResult sr= (ScanResult) msg.obj;
                    if(sr.virusInfo!=null){
                        SpannableString str=new SpannableString("发现病毒:"+sr.virusInfo.getName()+" "+sr.appName+" "+sr.virusInfo.getDesc()+"\n");
                        str.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), 0);
                        tvLog.append(str);
                    }else{
                        tvLog.append("扫描安全:"+sr.appName+"\n");
                    }
                    //向下滚动
                    svLog.fullScroll(View.FOCUS_DOWN);
                    break;
                /**扫描完成*/
                case WHAT_SCANNED:
                    ivScan.clearAnimation();
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
        setContentView(R.layout.activity_antivirus);
        ivScan= (ImageView) findViewById(R.id.iv_scan);
        pbScan= (ProgressBar) findViewById(R.id.pb_scan);
        tvLog= (TextView) findViewById(R.id.tv_log);
        svLog= (ScrollView) findViewById(R.id.sv_log);
        RotateAnimation rotateAnimation=new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5F);
        rotateAnimation.setDuration(1000*5);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        ivScan.startAnimation(rotateAnimation);
    }

    private void initData(){
        scanVirus();
    }

    /**
     * 查询病毒
     */
    private void scanVirus(){
        new Thread(){
            @Override
            public void run() {
                PackageManager packageManager=getPackageManager();
                List<PackageInfo> piList = packageManager.getInstalledPackages(0);
                pbScan.setMax(piList.size());
                int progress=0;
                for (PackageInfo pi : piList){
                    //获得的是****.apk安装包路径
                    String sourceDir = pi.applicationInfo.sourceDir;
                    String appName=pi.applicationInfo.loadLabel(packageManager).toString();
                    String md5=Md5Utils.encode(new File(sourceDir));
                    Log.i(Constants.TAG,appName+"md5->"+md5);
                    ScanResult sr=new ScanResult();
                    //应用名称
                    sr.appName=appName;
                    //可能的病毒信息
                    sr.virusInfo=AntivirusDao.getByMd5(md5);
                    //假装在杀毒
                    SystemClock.sleep(200);
                    Message msg=mHandler.obtainMessage();
                    msg.what=WHAT_SCANNING;
                    msg.obj=sr;
                    mHandler.sendMessage(msg);
                    progress++;
                    pbScan.setProgress(progress);
                }
                mHandler.sendEmptyMessage(WHAT_SCANNED);
            }
        }.start();
    }

    private class ScanResult{
        private String appName;
        private VirusInfo virusInfo;
    }
}

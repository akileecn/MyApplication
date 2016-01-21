package cn.aki.mobilesafe.manager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.bean.ProgressInfo;

/**
 * Created by Administrator on 2016/1/11.
 * 进程信息管理
 */
public class ProgressInfoManager {

    private Context mContext;
    private ActivityManager mActivityManager;
    public ProgressInfoManager(Context context){
        mContext=context;
        mActivityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * 获得进程信息
     */
    public List<ProgressInfo> getList(){
        PackageManager packageManager=mContext.getPackageManager();
        List<RunningAppProcessInfo> runningAppProcessInfoList = mActivityManager.getRunningAppProcesses();
        List<ProgressInfo> progressInfoList=new ArrayList<>();
        for(RunningAppProcessInfo runningAppProcessInfo:runningAppProcessInfoList){
            ProgressInfo progressInfo=new ProgressInfo();
            //进程名
            String processName=runningAppProcessInfo.processName;
            progressInfo.setProgressName(processName);
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                //图标
                progressInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                //应用名
                progressInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                //是否系统应用
                progressInfo.setIsSystem((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)!=0);
            } catch (PackageManager.NameNotFoundException e) {
                progressInfo.setAppName(processName);
                progressInfo.setIcon(mContext.getResources().getDrawable(R.mipmap.ic_launcher));
                progressInfo.setIsSystem(true);
            }
            //内存使用大小
            Debug.MemoryInfo[] processMemoryInfo = mActivityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            progressInfo.setMemorySize((long)processMemoryInfo[0].getTotalPrivateDirty()<<10);
            progressInfoList.add(progressInfo);
        }
        return progressInfoList;
    }

    /**
     * 清理除自己以外的进程
     */
    public void killOthers(){
        List<RunningAppProcessInfo> piList = mActivityManager.getRunningAppProcesses();
        String selfPackageName=mContext.getPackageName();
        for(RunningAppProcessInfo pi:piList){
            String progressName=pi.processName;
            mActivityManager.killBackgroundProcesses(progressName);
            //干掉自己以外所有后台进程
            if(!selfPackageName.equals(progressName)) {
                mActivityManager.killBackgroundProcesses(progressName);
            }
        }
    }

}

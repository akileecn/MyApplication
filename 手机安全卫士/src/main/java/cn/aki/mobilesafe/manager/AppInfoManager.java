package cn.aki.mobilesafe.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.os.SystemClock;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.aki.mobilesafe.bean.AppInfo;
import cn.aki.mobilesafe.db.dao.AppLockDao;

/**
 * Created by Administrator on 2015/12/29.
 * 软件信息管理类
 */
public class AppInfoManager {
    private Context mContext;
    private PackageManager mPackageManager;
    public AppInfoManager(Context context){
        mContext=context;
        mPackageManager=mContext.getPackageManager();
    }

    /**
     * 获得所有应用信息
     * @return  应用信息集合
     */
    public List<AppInfo> getList(){
        List<AppInfo> appInfoList=new ArrayList<>();
        List<PackageInfo> installedPackageList = mPackageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo:installedPackageList){
            AppInfo appInfo=new AppInfo();
            //应用名
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(mPackageManager).toString());
            //图标
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(mPackageManager));
            //包名
            appInfo.setPackageName(packageInfo.packageName);
            int flags=packageInfo.applicationInfo.flags;
            //是否安装在SD卡
            appInfo.setIsSD((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0);
            //是否为系统应用
            appInfo.setIsSystem((flags&ApplicationInfo.FLAG_SYSTEM)!=0);
            //应用大小(不准确)
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            appInfo.setSize(new File(sourceDir).length());
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }

    /**
     * 获得应用信息(是否被锁)
     * @param isLocked 是否被锁
     */
    public List<AppInfo> getLockList(boolean isLocked){
        AppLockDao dao=new AppLockDao(mContext);
        List<AppInfo> appInfoList=new ArrayList<>();
        List<PackageInfo> installedPackageList = mPackageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo:installedPackageList){
            //true取在数据库的,false取不在的
            if((isLocked&&dao.exists(packageInfo.packageName))
                    ||(!isLocked&&!dao.exists(packageInfo.packageName))){
                AppInfo appInfo=new AppInfo();
                //应用名
                appInfo.setAppName(packageInfo.applicationInfo.loadLabel(mPackageManager).toString());
                //图标
                appInfo.setIcon(packageInfo.applicationInfo.loadIcon(mPackageManager));
                //包名
                appInfo.setPackageName(packageInfo.packageName);
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }

    /**
     * 获得带缓存大小的应用信息
     */
    public List<AppInfo> getCacheList(){
        final List<AppInfo> appInfoList=new ArrayList<>();
        List<PackageInfo> installedPackageList = mPackageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo:installedPackageList){
            final AppInfo appInfo=new AppInfo();
            //应用名
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(mPackageManager).toString());
            //图标
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(mPackageManager));
            //包名
            String packageName=packageInfo.packageName;
            appInfo.setPackageName(packageName);
            //通过反射调用被hide的方法,获得缓存大小
            try {
                Method method=PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                method.invoke(mPackageManager, packageName, new IPackageStatsObserver.Stub() {
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        if (pStats.cacheSize > 0) {
                            appInfo.setSize(pStats.cacheSize);
                            appInfoList.add(appInfo);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SystemClock.sleep(500);
        return appInfoList;
    }

}

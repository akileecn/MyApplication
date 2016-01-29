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
        return baseGetList(new OnLoadListener<AppInfo>() {
            @Override
            public void onLooping(PackageInfo packageInfo, List<AppInfo> appInfoList) {
                AppInfo appInfo=new AppInfo();
                initAppBaseInfo(packageInfo,appInfo);
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

        });
    }

    /**
     * 获得应用信息(是否被锁)
     * @param isLocked 是否被锁
     */
    public List<AppInfo> getLockList(final boolean isLocked){
        final AppLockDao dao=new AppLockDao(mContext);
        return baseGetList(new OnLoadListener<AppInfo>() {
            @Override
            public void onLooping(PackageInfo packageInfo, List<AppInfo> appInfoList) {
                //true取在数据库的,false取不在的
                if((isLocked&&dao.exists(packageInfo.packageName))
                        ||(!isLocked&&!dao.exists(packageInfo.packageName))){
                    AppInfo appInfo=new AppInfo();
                    initAppBaseInfo(packageInfo,appInfo);
                    appInfoList.add(appInfo);
                }
            }
        });
    }

    /**
     * 获得带缓存大小的应用信息
     */
    public List<AppInfo> getCacheList(){
        return baseGetList(new OnLoadListener<AppInfo>() {
            @Override
            public void onLooping(final PackageInfo packageInfo, final List<AppInfo> appInfoList) {
                //通过反射调用被hide的方法,获得缓存大小
                try {
                    Method method=PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                    method.invoke(mPackageManager, packageInfo.packageName, new IPackageStatsObserver.Stub() {
                        @Override
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                            if (pStats.cacheSize > 0) {
                                AppInfo appInfo=new AppInfo();
                                //初始化其他基本信息
                                initAppBaseInfo(packageInfo,appInfo);
                                appInfo.setSize(pStats.cacheSize);
                                appInfoList.add(appInfo);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLooped(List<AppInfo> appInfoList) {
                SystemClock.sleep(500);
            }
        });
    }

    /**
     * 自定义封装应用信息接口
     * @param <T>
     */
    public abstract class OnLoadListener<T extends AppInfo>{
        /**
         * 循环中
         * @param packageInfo 包信息
         * @param appInfoList 应用信息集合
         */
        public abstract void onLooping(PackageInfo packageInfo,List<T> appInfoList);

        /**
         * 循环结束
         * @param appInfoList 应用信息集合
         */
        protected void onLooped(List<T> appInfoList){}
        /**
         * 初始化应用基本信息
         * @param packageInfo 包信息
         * @param appInfo 应用信息
         */
        protected void initAppBaseInfo(PackageInfo packageInfo,T appInfo){
            //应用名
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(mPackageManager).toString());
            //图标
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(mPackageManager));
            //包名
            appInfo.setPackageName(packageInfo.packageName);
        }
    }

    /**
     * 查询应用信息基本方法
     */
    public <T extends AppInfo> List<T> baseGetList(OnLoadListener<T> listener){
        List<T> appInfoList=new ArrayList<>();
        List<PackageInfo> installedPackageList = mPackageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo:installedPackageList){
            listener.onLooping(packageInfo,appInfoList);
        }
        listener.onLooped(appInfoList);
        return appInfoList;
    }

}

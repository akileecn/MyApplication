package cn.aki.mobilesafe.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.fragment.BaseLockAppFragment;
//import cn.aki.mobilesafe.fragment.UnlockAppFragment;

/**
 * Created by Administrator on 2016/1/26.
 * 程序锁 为了兼容性考虑而继承FragmentActivity
 */
public class AppLockActivity extends FragmentActivity {
    Button btnUnlock;//未加锁
    Button btnLock;//已加锁
    FrameLayout flAppList;//应用frame
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI(){
        setContentView(R.layout.activity_app_lock);
        btnLock= (Button) findViewById(R.id.btn_lock);
        btnUnlock= (Button) findViewById(R.id.btn_unlock);
        flAppList= (FrameLayout) findViewById(R.id.fl_app_list);

        /**加载默认fragment*/
        final FragmentManager fragmentManager=getSupportFragmentManager();
//        final Fragment unlockAppFragment=new UnlockAppFragment();
        final Fragment unlockAppFragment=new BaseLockAppFragment(false);
        final Fragment lockAppFragment=new BaseLockAppFragment(true);
        fragmentManager.beginTransaction().replace(R.id.fl_app_list,unlockAppFragment).commit();

        /**未加锁*/
        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
                btnLock.setBackgroundResource(R.drawable.tab_right_default);
                fragmentManager.beginTransaction().replace(R.id.fl_app_list,unlockAppFragment).commit();
            }
        });

        /**已加锁*/
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUnlock.setBackgroundResource(R.drawable.tab_right_default);
                btnLock.setBackgroundResource(R.drawable.tab_right_pressed);
                fragmentManager.beginTransaction().replace(R.id.fl_app_list,lockAppFragment).commit();
            }
        });

    }
}

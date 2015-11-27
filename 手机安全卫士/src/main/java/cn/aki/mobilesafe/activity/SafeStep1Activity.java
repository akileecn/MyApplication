package cn.aki.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/11/20.
 * 手机防盗设置向导1
 */
public class SafeStep1Activity extends BaseSafeStepActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_step1);
    }

    @Override
    void toPrevious() {

    }

    @Override
    void toNext() {
        startActivity(new Intent(this,SafeStep2Activity.class));
        //设置页面跳转动画
        overridePendingTransition(R.anim.tran_right_in,R.anim.tran_left_out);
        finish();
    }

}

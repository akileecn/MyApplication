package cn.aki.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/11/13.
 * 自定义设置条目视图
 */
public class SettingItemView extends RelativeLayout {
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbCheck;
    private String mEnableDesc;
    private String mDisableDesc;
    public SettingItemView(Context context) {
        super(context);
        initView(null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs){
        View.inflate(getContext(), R.layout.view_setting_item,this);
        tvTitle= (TextView) findViewById(R.id.tv_title);
        tvDesc= (TextView) findViewById(R.id.tv_desc);
        cbCheck= (CheckBox) findViewById(R.id.cb_check);
        if(attrs!=null){
            //获得自定义属性
            TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.SettingItemView);
            String title=typedArray.getString(R.styleable.SettingItemView_headline);
            tvTitle.setText(title);
            mEnableDesc=typedArray.getString(R.styleable.SettingItemView_enable_desc);
            mDisableDesc=typedArray.getString(R.styleable.SettingItemView_disable_desc);
            typedArray.recycle();
        }
    }

    public boolean isChecked(){
        return cbCheck.isChecked();
    }

    public void check(boolean enable){
        cbCheck.setChecked(enable);
        if(enable){
            tvDesc.setText(mEnableDesc);
            tvDesc.setTextColor(Color.GREEN);
        }else{
            tvDesc.setText(mDisableDesc);
            tvDesc.setTextColor(Color.RED);
        }
    }

}

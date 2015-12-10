package cn.aki.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.aki.mobilesafe.R;

/**
 * Created by Administrator on 2015/12/7.
 * 设置自定义带你及
 */
public class SettingClickView extends RelativeLayout{
    private TextView tvTitle;
    private TextView tvDesc;
    public SettingClickView(Context context) {
        super(context);
        initView(null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public void initView(AttributeSet attrs){
        View view=View.inflate(getContext(), R.layout.view_setting_click,this);
        tvTitle= (TextView) view.findViewById(R.id.tv_title);
        tvDesc= (TextView) findViewById(R.id.tv_desc);
        if(attrs!=null){
            TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.SettingItemView);
            String title=typedArray.getString(R.styleable.SettingItemView_headline);
            setTitle(title);
            typedArray.recycle();
        }
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    public void setDesc(String desc){
        tvDesc.setText(desc);
    }
}

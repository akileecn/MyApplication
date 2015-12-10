package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.aki.mobilesafe.R;
import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/12/10.
 * 拖动归属地显示位置
 */
public class DragAddressActivity extends Activity {
    private ImageView ivDrag;
    private TextView tvTop;
    private TextView tvBottom;
    private SharedPreferences mPref;

    private int tempX;
    private int tempY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_address);
        tvTop= (TextView) findViewById(R.id.tv_top);
        tvBottom= (TextView) findViewById(R.id.tv_bottom);
        ivDrag= (ImageView) findViewById(R.id.iv_drag);
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG, Context.MODE_PRIVATE);
        initDrag();
    }

    /**
     * 初始化拖拽图标
     */
    private void initDrag(){
        int initX=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_X,0);
        int initY=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_Y,0);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        params.setMargins(initX,initY,0,0);
        ivDrag.setLayoutParams(params);
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        tempX= (int) event.getRawX();
                        tempY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX= (int) event.getRawX();
                        int newY= (int) event.getRawY();
                        int dx=newX-tempX;
                        int dy=newY-tempY;
                        //重绘
                        ivDrag.layout(ivDrag.getLeft()+dx,ivDrag.getTop()+dy,ivDrag.getRight()+dx,ivDrag.getBottom()+dy);
                        if(ivDrag.getTop()<tvTop.getBottom()){
                            tvTop.setVisibility(View.INVISIBLE);
                        }else{
                            tvTop.setVisibility(View.VISIBLE);
                        }
                        if(ivDrag.getBottom()>tvBottom.getTop()){
                            tvBottom.setVisibility(View.INVISIBLE);
                        }else{
                            tvBottom.setVisibility(View.VISIBLE);
                        }
                        tempX=newX;
                        tempY=newY;
                        break;
                    case MotionEvent.ACTION_UP:
                        //保存
                        SharedPreferences.Editor editor=mPref.edit();
                        editor.putInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_X,ivDrag.getLeft());
                        editor.putInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_Y,ivDrag.getTop());
                        editor.apply();
                        break;
                    default:
                        break;
                }
                //设置为false时无法获得后续动作
                return true;
            }
        });
    }
}

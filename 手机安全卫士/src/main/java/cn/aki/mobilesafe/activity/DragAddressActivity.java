package cn.aki.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
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
    private int appWidth;
    private int appHeight;

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Rect rect=new Rect();
            //获得应用高度
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            appWidth=rect.width();
            appHeight=rect.height();
            initDrag();
        }
    }

    /**
     * 初始化拖拽图标
     */
    private void initDrag(){
        resetTopBottom();
        int initX=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_X,0);
        int initY=mPref.getInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_Y,0);
        final RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        params.setMargins(initX,initY,0,0);
        ivDrag.setLayoutParams(params);
        ivDrag.setOnClickListener(new View.OnClickListener() {
            private long[] times=new long[2];
            @Override
            public void onClick(View v) {
                System.arraycopy(times,1,times,0,times.length-1);
                if(SystemClock.uptimeMillis()-times[0]<500){
                    //重绘
                    int left=appWidth/2-ivDrag.getWidth()/2;
                    int top=appHeight/2-ivDrag.getHeight()/2;
                    int right=appWidth/2+ivDrag.getWidth()/2;
                    int bottom=appHeight/2+ivDrag.getHeight()/2;
                    ivDrag.layout(left, top, right, bottom);
                    //保存
                    SharedPreferences.Editor editor=mPref.edit();
                    editor.putInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_X,ivDrag.getLeft());
                    editor.putInt(Constants.SharedPreferences.KEY_ADDRESS_LOCATION_Y,ivDrag.getTop());
                    editor.apply();
                }else{
                    times[times.length-1]=SystemClock.uptimeMillis();
                }
            }
        });
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
                        int left=ivDrag.getLeft();
                        int top=ivDrag.getTop();
                        int right=ivDrag.getRight();
                        int bottom=ivDrag.getBottom();
                        boolean changed=false;
                        //防越界
                        if(left+dx>0&&right+dx<appWidth){
                            tempX=newX;
                            changed=true;
                            left+=dx;
                            right+=dx;
                        }
                        if(top+dy>0&&bottom+dy<appHeight){
                            tempY=newY;
                            changed=true;
                            top+=dy;
                            bottom+=dy;
                        }
                        if(changed){
                            //重绘
                            ivDrag.layout(left, top, right, bottom);
                            resetTopBottom();
                        }
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
                //允许点击监听继续响应事件
                return false;
            }
        });
    }

    /**
     * 重置上下浮动框
     */
    private void resetTopBottom(){
        if(ivDrag.getTop()+ivDrag.getHeight()/2>appHeight/2){
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        }else{
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }
    }
}

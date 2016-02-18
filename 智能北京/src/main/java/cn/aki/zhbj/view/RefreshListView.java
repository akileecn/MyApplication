package cn.aki.zhbj.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.aki.zhbj.R;

/**
 * Created by Administrator on 2016/2/18.
 * 下拉刷新列表视图
 */
public class RefreshListView extends ListView{
    private View mHeaderView;
    private int mHeaderHeight;
    /**headerView中的视图*/
    private ImageView ivArrow;
    private ProgressBar pbLoading;
    private TextView tvTitle;
    private TextView tvTime;
    /**状态*/
    private int mCurrentStatus=STATUS_PULL_DOWN;
    private static final int STATUS_PULL_DOWN=1;
    private static final int STATUS_REFRESHING=2;
    private static final int STATUS_RELEASE_REFRESH=3;

    private int startY;
    public RefreshListView(Context context) {
        super(context);
        initView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        /**初始化头部视图*/
        mHeaderView=View.inflate(getContext(), R.layout.header_refresh,null);
        ivArrow= (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pbLoading= (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);
        tvTitle= (TextView) mHeaderView.findViewById(R.id.tv_title);
        tvTime= (TextView) mHeaderView.findViewById(R.id.tv_time);
        //测量后才能获得测量得到的值,getWidth只能在view调用onMeasure之后获得
        mHeaderView.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        mHeaderHeight=mHeaderView.getMeasuredHeight();
        //padding为负可隐藏
        mHeaderView.setPadding(0, -mHeaderHeight, 0, 0);
        addHeaderView(mHeaderView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY= (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dy= (int) ev.getRawY()-startY;
                if(getFirstVisiblePosition()==0){
                    mHeaderView.setPadding(0,dy-mHeaderHeight,0,0);
                    //向下划且为最开始位置
                    if(dy>0&&getFirstVisiblePosition()<=1){
                        if(mCurrentStatus==STATUS_PULL_DOWN&&dy>mHeaderHeight){
                            mCurrentStatus=STATUS_RELEASE_REFRESH;
                            refreshHeaderView();
                        }else if(mCurrentStatus==STATUS_RELEASE_REFRESH&&dy<mHeaderHeight){
                            mCurrentStatus=STATUS_PULL_DOWN;
                            refreshHeaderView();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mCurrentStatus==STATUS_RELEASE_REFRESH){
                    mCurrentStatus=STATUS_REFRESHING;
                    refreshHeaderView();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 更新头部视图
     */
    private void refreshHeaderView(){
        switch (mCurrentStatus){
            case STATUS_PULL_DOWN:
                tvTitle.setText("下拉刷新");
                startArrowAnimation(false);
                break;
            case STATUS_REFRESHING:
                ivArrow.clearAnimation();
                ivArrow.setVisibility(View.INVISIBLE);
                pbLoading.setVisibility(View.VISIBLE);
                tvTitle.setText("刷新中...");
                String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
                tvTime.setText(time);
                refreshed();
                break;
            case STATUS_RELEASE_REFRESH:
                tvTitle.setText("松开刷新");
                startArrowAnimation(true);
                break;
            default:
                break;
        }
    }

    /**
     * 箭头动画
     * @param turnUp 是否向上翻转
     */
    private void startArrowAnimation(boolean turnUp){
        Animation anim=new RotateAnimation(turnUp?0:180,turnUp?180:0,Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5F);
        anim.setDuration(200);
        anim.setFillAfter(true);
        ivArrow.startAnimation(anim);
    }

    private void refreshed(){
        mHeaderView.setPadding(0,-mHeaderHeight,0,0);
        ivArrow.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
    }
}

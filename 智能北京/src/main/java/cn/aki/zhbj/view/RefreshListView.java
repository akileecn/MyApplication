package cn.aki.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
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
    private View mHeaderView;//刷新头
    private int mHeaderHeight;
    private View mFooterView;//加载底
    private int mFooterHeight;
    /**headerView中的视图*/
    private ImageView ivArrow;
    private ProgressBar pbLoading;
    private TextView tvTitle;
    private TextView tvTime;
    /**状态*/
    private int mCurrentStatus=STATUS_PULL_DOWN;
    private static final int STATUS_PULL_DOWN=0;//下拉
    private static final int STATUS_REFRESHING=1;//刷新
    private static final int STATUS_RELEASE_REFRESH=2;//释放
    private static final int STATUS_LOADING=3;//加载更多
    private OnLoadListener mLoadListener;

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
        tvTime.setText("未刷新");
        //测量后才能获得测量得到的值,getWidth只能在view调用onMeasure之后获得
        mHeaderView.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        mHeaderHeight=mHeaderView.getMeasuredHeight();
        //padding为负可隐藏
        mHeaderView.setPadding(0, -mHeaderHeight, 0, 0);
        addHeaderView(mHeaderView);
        /**初始化底部视图*/
        mFooterView=View.inflate(getContext(),R.layout.footer_load,null);
        mFooterView.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        mFooterHeight=mFooterView.getMeasuredHeight();
        //使用View.GONE的话底部仍然有空白
        mFooterView.setPadding(0,-mFooterHeight,0,0);
        addFooterView(mFooterView);
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //划动结束
                if((scrollState==SCROLL_STATE_FLING||scrollState==SCROLL_STATE_IDLE)
                        &&getLastVisiblePosition()==getCount()-1){
                    mCurrentStatus=STATUS_LOADING;
                    mFooterView.setPadding(0,0,0,0);
                    if(mLoadListener!=null){
                        mLoadListener.loadMore();
                    }
                    mHeaderView.setPadding(0, -mHeaderHeight, 0, 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private int startY;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY= (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endY= (int) ev.getRawY();
                if(startY==0){
                    startY=(int) ev.getRawY();
                }
                int dy= endY-startY;
                /**向下划且为最开始位置刷新*/
                if(dy>0&&getFirstVisiblePosition()==0){
                    mHeaderView.setPadding(0,dy-mHeaderHeight,0,0);
                    if(mCurrentStatus!=STATUS_RELEASE_REFRESH&&dy>mHeaderHeight){
                        mCurrentStatus=STATUS_RELEASE_REFRESH;
                        refreshHeaderView();
                    }else if(mCurrentStatus!=STATUS_PULL_DOWN&&dy<mHeaderHeight){
                        mCurrentStatus=STATUS_PULL_DOWN;
                        refreshHeaderView();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //手动重置
                startY=0;
                switch (mCurrentStatus){
                    case STATUS_PULL_DOWN:
                        //隐藏
                        mHeaderView.setPadding(0, -mHeaderHeight, 0, 0);
                        break;
                    case STATUS_RELEASE_REFRESH:
                        //刷新
                        mCurrentStatus=STATUS_REFRESHING;
                        refreshHeaderView();
                        mHeaderView.setPadding(0, 0, 0, 0);
                        if(mLoadListener!=null){
                            mLoadListener.refresh();
                        }
                        break;
                    default:
                        break;
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
                ivArrow.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.INVISIBLE);
                break;
            case STATUS_REFRESHING:
                ivArrow.clearAnimation();
                ivArrow.setVisibility(View.INVISIBLE);
                pbLoading.setVisibility(View.VISIBLE);
                tvTitle.setText("刷新中...");
                break;
            case STATUS_RELEASE_REFRESH:
                tvTitle.setText("松开刷新");
                startArrowAnimation(true);
                ivArrow.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mFooterView.setPadding(0,-mFooterHeight,0,0);
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

    /**
     * 通知view加载完成
     */
    public void notifyRefreshed(boolean success){
        if(mCurrentStatus==STATUS_LOADING){
            mFooterView.setPadding(0,-mFooterHeight,0,0);
            mCurrentStatus=STATUS_PULL_DOWN;
        }else{
            mHeaderView.setPadding(0, -mHeaderHeight, 0, 0);
            if(success){
                String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
                tvTime.setText(time);
            }
            mCurrentStatus=STATUS_PULL_DOWN;
            refreshHeaderView();
        }
    }

    /**
     * 加载监听接口
     */
    public interface OnLoadListener{
        /**
         * 刷新
         */
        void refresh();

        /**
         * 加载更多
         */
        void loadMore();
    }

    /**
     * 设置加载监听器
     */
    public void setOnLoadListener(OnLoadListener listener){
        mLoadListener=listener;
    }

}

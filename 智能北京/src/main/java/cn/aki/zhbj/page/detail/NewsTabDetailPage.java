package cn.aki.zhbj.page.detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import cn.aki.zhbj.R;
import cn.aki.zhbj.activity.NewsDetailActivity;
import cn.aki.zhbj.common.C;
import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.data.response.NewsData;
import cn.aki.zhbj.utils.ImageUtils;
import cn.aki.zhbj.view.RefreshListView;

/**
 * Created by Administrator on 2016/2/16.
 * 新闻tab详情页
 */
public class NewsTabDetailPage extends BaseDetailPage {
    private ViewPager vpTop;//热点图
    private CirclePageIndicator cpiTop;//热点图指示器
    private RefreshListView lvNews;//新闻列表
    private TextView tvTitle;//热点新闻标题
    private String mMoreUrl;//加载更多连接
    private List<NewsData.TopNews> mTopNewsList;//热点新闻数据列表
    private BaseAdapter mNewsAdapter;//新闻列表适配器
    private List<NewsData.ListNews> mNewsList;//新闻数据列表
    private ImageOptions mImageOptions;//图片加载参数
    private SharedPreferences mPref;
    private ImageUtils mImageUtils;

    private static final int WHAT_CAROUSEL=1;//轮播
    private Handler mHandler;

    public NewsTabDetailPage(Context context, Categories.Menu data) {
        super(context, data);
    }

    @Override
    protected void initView() {
        mImageUtils=new ImageUtils(mContext);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=4;//宽高设置为原来的1/4
        mImageUtils.setOptions(options);
        mPref=C.getConfig(mContext);
        mImageOptions=new ImageOptions.Builder()
                .setLoadingDrawableId(R.drawable.topnews_item_default)
                .setFailureDrawableId(R.drawable.topnews_item_default)
                .build();
        mRootView= View.inflate(mContext, R.layout.detail_page_news_tab,null);
        /**热点新闻*/
        View topHeaderView=View.inflate(mContext,R.layout.header_top_news,null);
        vpTop= (ViewPager) topHeaderView.findViewById(R.id.vp_top);
        cpiTop= (CirclePageIndicator) topHeaderView.findViewById(R.id.cpi_top);
        tvTitle= (TextView) topHeaderView.findViewById(R.id.tv_title);
        vpTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvTitle.setText(mTopNewsList.get(position).title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        vpTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mHandler.removeMessages(WHAT_CAROUSEL);
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.sendEmptyMessageDelayed(WHAT_CAROUSEL,3000);
                        break;
                }
                return false;
            }
        });
        /**一般新闻*/
        lvNews= (RefreshListView) mRootView.findViewById(R.id.lv_news);
        lvNews.addHeaderView(topHeaderView);
        lvNews.setOnLoadListener(new RefreshListView.OnLoadListener() {
            @Override
            public void refresh() {
                initData();
            }

            @Override
            public void loadMore() {
                initData(true);
            }
        });
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@param position 有header或footer时会包含这些元素，与数据源的位置会有不用
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //有header或footer时parent.getAdapter()得到的不是自定义的adapter，包含了header或footer
                NewsData.ListNews news = (NewsData.ListNews) parent.getAdapter().getItem(position);
                //取到header或footer时为null
                if (news != null) {
                    /**记录点击历史*/
                    recordClickedLink(news.id);
                    //局部刷新界面
                    ((TextView) view.findViewById(R.id.tv_title)).setTextColor(Color.GRAY);
                    /**打开详情页*/
                    Intent intent = new Intent(mContext, NewsDetailActivity.class);
                    intent.putExtra(C.Extra.URL, news.url);
                    mContext.startActivity(intent);
                }
            }
        });
        //解析缓存
        String cache= mJsonCache.getCache(C.Url.BASE + mData.url);
        if(cache!=null){
            parseData(cache, false);
        }
        initData();
    }

    @Override
    public void initData() {
        initData(false);
    }

    /**
     * 加载数据
     * @param isMore 是否为加载更多
     */
    public void initData(final boolean isMore) {
        if(isMore&&TextUtils.isEmpty(mMoreUrl)){
            Toast.makeText(mContext,"最后一页",Toast.LENGTH_SHORT).show();
            lvNews.notifyRefreshed(false);
            return;
        }
        final String url=C.Url.BASE+(isMore?mMoreUrl:mData.url);
        RequestParams params=new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseData(result, isMore);
                lvNews.notifyRefreshed(true);
                mJsonCache.saveCache(url, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, "加载数据失败", Toast.LENGTH_SHORT).show();
                lvNews.notifyRefreshed(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 解析数据
     */
    private void parseData(String result,boolean isMore){
        Gson gson=new Gson();
        NewsData newsData=gson.fromJson(result.replaceAll(C.Url.DEPRECATED_BASE,C.Url.BASE),NewsData.class);
        mMoreUrl=newsData.data.more;
        if(!isMore){
            mTopNewsList=newsData.data.topnews;
            mNewsList=newsData.data.news;

            //热点
            if(mTopNewsList!=null&&mTopNewsList.size()>0){
                vpTop.setAdapter(new MyTopPagerAdapter());
                cpiTop.setViewPager(vpTop);
                tvTitle.setText(mTopNewsList.get(0).title);
            }
            //新闻
            if(mNewsList!=null&&mNewsList.size()>0){
                mNewsAdapter = new MyNewsAdapter();
                lvNews.setAdapter(mNewsAdapter);
            }
        }else{
            List<NewsData.ListNews> moreNewsList=newsData.data.news;
            if(moreNewsList!=null&&moreNewsList.size()>0){
                mNewsList.addAll(moreNewsList);
                mNewsAdapter.notifyDataSetChanged();
            }
        }
        //自动轮播
        if(mHandler==null){
            mHandler=new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what){
                        case WHAT_CAROUSEL:
                            int position=vpTop.getCurrentItem();
                            if(position<vpTop.getAdapter().getCount()-1){
                                position++;
                            }else{
                                position=0;
                            }
                            vpTop.setCurrentItem(position);
                            mHandler.sendEmptyMessageDelayed(WHAT_CAROUSEL,3000);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
//            mHandler.sendEmptyMessageDelayed(WHAT_CAROUSEL, 3000);
        }
    }

    /**
     * 热点新闻适配器
     */
    private class MyTopPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mTopNewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view=new ImageView(mContext);
            NewsData.TopNews topNews=mTopNewsList.get(position);
//            x.image().bind(view, topNews.topimage,mImageOptions);
            mImageUtils.bind(topNews.topimage,view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 新闻列表适配器
     */
    private class MyNewsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsData.ListNews getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView=View.inflate(mContext,R.layout.item_tab_news,null);
                viewHolder=new ViewHolder();
                viewHolder.ivImage= (ImageView) convertView.findViewById(R.id.iv_image);
                viewHolder.tvTime= (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tvTitle= (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }
            NewsData.ListNews news=getItem(position);
            viewHolder.tvTitle.setText(news.title);
            if(isLinkClicked(news.id)){
                viewHolder.tvTitle.setTextColor(Color.GRAY);
            }
            viewHolder.tvTime.setText(news.pubdate);
//            x.image().bind(viewHolder.ivImage,news.listimage,mImageOptions);
            mImageUtils.bind(news.listimage,viewHolder.ivImage);
            return convertView;
        }

        private class ViewHolder{
            private ImageView ivImage;
            private TextView tvTitle;
            private TextView tvTime;
        }
    }

    /**
     * 链接是否被点击
     * @param id 新闻id
     */
    private boolean isLinkClicked(int id){
        String clickedLink=mPref.getString(C.Sp.KEY_CLICKED_LINK,null);
        return clickedLink!=null&&clickedLink.contains(","+id+",");
    }

    /**
     * 记录被点击链接
     * @param id 新闻id
     */
    private void recordClickedLink(int id){
        String clickedLink=mPref.getString(C.Sp.KEY_CLICKED_LINK,null);
        if(clickedLink==null){
            mPref.edit().putString(C.Sp.KEY_CLICKED_LINK,","+id+",").apply();
        }else if(!clickedLink.contains(","+id+",")){
            mPref.edit().putString(C.Sp.KEY_CLICKED_LINK,clickedLink+id+",").apply();
        }
    }
}

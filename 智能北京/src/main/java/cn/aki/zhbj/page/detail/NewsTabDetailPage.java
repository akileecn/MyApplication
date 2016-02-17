package cn.aki.zhbj.page.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import cn.aki.zhbj.common.C;
import cn.aki.zhbj.data.response.Categories;
import cn.aki.zhbj.data.response.NewsData;

/**
 * Created by Administrator on 2016/2/16.
 * 新闻tab详情页
 */
public class NewsTabDetailPage extends BaseDetailPage {
    private ViewPager vpTop;//热点图
    private CirclePageIndicator cpiTop;//热点图指示器
    private ListView lvNews;//新闻列表
    private TextView tvTitle;//热点新闻标题
    private NewsData mNewsData;//总新闻数据
    private List<NewsData.TopNews> mTopNewsList;//热点新闻数据列表
    private List<NewsData.ListNews> mNewsList;//新闻数据列表
    private ImageOptions mImageOptions;//图片加载参数

    public NewsTabDetailPage(Context context, Categories.Menu data) {
        super(context, data);
    }

    @Override
    protected void initView() {
        mImageOptions=new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.topnews_item_default)
                .setFailureDrawableId(R.mipmap.topnews_item_default)
                .build();
        mRootView= View.inflate(mContext, R.layout.detail_page_news_tab,null);
        lvNews= (ListView) mRootView.findViewById(R.id.lv_news);
        /**热点图作为一般新闻列表的headerView*/
        View topHeaderView=View.inflate(mContext,R.layout.header_top_news,null);
        vpTop= (ViewPager) topHeaderView.findViewById(R.id.vp_top);
        cpiTop= (CirclePageIndicator) topHeaderView.findViewById(R.id.cpi_top);
        tvTitle= (TextView) topHeaderView.findViewById(R.id.tv_title);

        lvNews.addHeaderView(topHeaderView);
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
        initData();
    }

    @Override
    public void initData() {
        RequestParams params=new RequestParams(C.Url.BASE+mData.url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, "加载数据失败", Toast.LENGTH_SHORT).show();
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
            x.image().bind(view, topNews.topimage,mImageOptions);
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
            viewHolder.tvTime.setText(news.pubdate);
            x.image().bind(viewHolder.ivImage,news.listimage,mImageOptions);
            return convertView;
        }

        private class ViewHolder{
            private ImageView ivImage;
            private TextView tvTitle;
            private TextView tvTime;
        }
    }

    /**
     * 解析数据
     */
    private void parseData(String result){
        Gson gson=new Gson();
        mNewsData=gson.fromJson(result.replaceAll(C.Url.DEPRECATED_BASE,C.Url.BASE),NewsData.class);
        mTopNewsList=mNewsData.data.topnews;
        mNewsList=mNewsData.data.news;
        //热点
        if(mTopNewsList!=null&&mTopNewsList.size()>0){
            vpTop.setAdapter(new MyTopPagerAdapter());
            cpiTop.setViewPager(vpTop);
            tvTitle.setText(mTopNewsList.get(0).title);
        }
        //新闻
        if(mNewsList!=null&&mNewsList.size()>0){
            lvNews.setAdapter(new MyNewsAdapter());
        }
    }
}

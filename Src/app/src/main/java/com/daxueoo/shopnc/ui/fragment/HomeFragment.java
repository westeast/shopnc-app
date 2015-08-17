package com.daxueoo.shopnc.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daxueoo.shopnc.R;
import com.daxueoo.shopnc.adapter.ImagePagerAdapter;
import com.daxueoo.shopnc.adapter.TopicAdapter;
import com.daxueoo.shopnc.model.TopicMessage;
import com.daxueoo.shopnc.scan.CaptureActivity;
import com.daxueoo.shopnc.utils.ACache;
import com.daxueoo.shopnc.utils.ConstUtils;
import com.daxueoo.shopnc.utils.ListUtils;
import com.daxueoo.shopnc.utils.SystemUtils;
import com.daxueoo.shopnc.widgets.ForScrollViewListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

/**
 * 这是首页的Fragment，主要有滚动图片，ListView，Button等
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private String TAG = "HomeFragment";

    //  自动滚动图片
    private AutoScrollViewPager viewPager;
    private List<Integer> imageIdList;

    //  Header标题
    private TextView tv_title;

    private ForScrollViewListView listView;

    private List<TopicMessage> data = new ArrayList<TopicMessage>();
    private TopicAdapter topicAdapter;

    private PtrClassicFrameLayout mPtrFrame;
    private ScrollView scrollView;
    private LoadMoreListViewContainer loadMoreListViewContainer;

    private LinearLayout ll_my_order;

    private ACache aCache;
    private boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = View.inflate(activity, R.layout.fragment_home, null);

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_web_view_frame);
        //  滚动图片
        viewPager = (AutoScrollViewPager) view.findViewById(R.id.view_pager);

        tv_title = (TextView) view.findViewById(R.id.titlebar_tv);

        listView = (ForScrollViewListView) view.findViewById(R.id.list_fragment_topic);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        //  设置滚动到顶部
        scrollView.smoothScrollTo(0, 0);

        ll_my_order = (LinearLayout) view.findViewById(R.id.ll_my_order);

//        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_list_view_container);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_title.setText(R.string.tab_tv_index);

        aCache = ACache.get(this.getActivity());
        //  初始化按钮
        initButton();
        //  初始化滚动图片
        initViewPager();
        //  初始化ListView，后期换成RecyclerView
        //  TODO    换成RecyclerView
        initListView();

        if (isFirst) {
            thread.start();
            isFirst = false;
            //  初始化数据
            if (aCache.getAsJSONArray("themeList") != null) {
                initData();
            }
        }
        //  设置ScrollView嵌套ListView的滚动问题

        //  初始化下拉刷新
        initPtr();
        //  初始化上拉加载
        initLoadMore();

    }

    /**
     * 初始化按钮
     */
    private void initButton() {
        ll_my_order.setOnClickListener(this);
    }

    /**
     * 初始化加载更多
     */
    private void initLoadMore() {

//        //TODO 加载更多
//        // load more container
//        loadMoreListViewContainer.useDefaultHeader();
//        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
//            @Override
//            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
//                //mDataModel.queryNextPage();
//            }
//        });

//        // process data
//        EventCenter.bindContainerAndHandler(this, new DemoSimpleEventHandler() {
//
//            public void onEvent(ImageListDataEvent event) {
//
//                // ptr
//                mPtrFrameLayout.refreshComplete();
//
//                // load more
//                loadMoreListViewContainer.loadMoreFinish(mDataModel.getListPageInfo().isEmpty(), mDataModel.getListPageInfo().hasMore());
//
//                mAdapter.notifyDataSetChanged();
//            }
//
//            public void onEvent(ErrorMessageDataEvent event) {
//                loadMoreListViewContainer.loadMoreError(0, event.message);
//            }
//
//        }).tryToRegisterIfNot();
    }

    /**
     * 下拉刷新
     */
    private void initPtr() {
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, scrollView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrame.refreshComplete();
                        //  异步刷新数据
                        handler.sendEmptyMessage(1);
                    }
                }, 100);
            }
        });
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                //  mPtrFrame.autoRefresh();
                Toast.makeText(HomeFragment.this.getActivity(), "Str start", Toast.LENGTH_SHORT).show();
            }
        }, 100);
    }

    /**
     * 多线程获取数据
     */
    private Thread thread = new Thread() {
        @Override
        public void run() {
            //  判断网络连接情况获取数据
            if (isNetConnected()) {
                handler.sendEmptyMessage(0);
            } else {
                Toast.makeText(HomeFragment.this.getActivity(), R.string.network_fail, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * handler初始化网络数据
     */
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //  获取网络数据
                    if (aCache.getAsJSONArray("themeList") == null) {
                        initOriginData();
                    }
                    break;
                case 1:
                    Log.e(TAG, "test");
                    topicAdapter.notifyDataSetChanged(); // 发送消息通知ListView更新
                    SystemUtils.setListViewHeightBasedOnChildren(listView);
                    Toast.makeText(HomeFragment.this.getActivity(), "没有更多新数据了。。", Toast.LENGTH_LONG).show();
                    break;
                //  下拉刷新
                case 2:
                    //  获取最新数据
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取远程数据
     */
    private void initOriginData() {
        Log.e(TAG, ConstUtils.HOT_THEME);
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        JsonObjectRequest objRequest = new JsonObjectRequest(ConstUtils.HOT_THEME, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                Log.e(TAG, obj.toString());

                try {
                    JSONObject jsonObject = obj.getJSONObject("datas");
                    JSONArray replyThemeList = jsonObject.getJSONArray("reply_themelist");
                    //  缓存获取到的结果
                    aCache.put("themeList", replyThemeList);
                    for (int i = 0; i < replyThemeList.length(); i++) {
                        JSONObject theme = replyThemeList.getJSONObject(i);
                        String themeTitle = theme.getString("theme_name");
                        String themeContent = theme.getString("theme_content");
                        String themeName = theme.getString("member_name");
                        String themeId = theme.getString("theme_id");
                        TopicMessage msg = new TopicMessage(themeTitle, themeContent, themeName, "12小时前", "5条回复");
                        if (i % 2 == 0) {
                            msg.setIcon_url("");
                        } else {
                            msg.setIcon_url("http://ww1.sinaimg.cn/mw690/b03d2261gw1ev0qrv2xmgj20jt0df0vl.jpg");
                        }
                        msg.setTopic_user_icon("https://cdn.v2ex.co/avatar/7b5a/e206/89357_large.png");

                        data.add(msg);
                    }
                    Log.e(TAG, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
                //  Toast.makeText(HomeFragment.this.getActivity(), "获取数据成功。", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeFragment.this.getActivity(), "获取数据失败，请重新获取。", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.toString());
            }
        });

        requestQueue.add(objRequest);
        requestQueue.start();
    }

    /**
     * ListView
     */

    private void initListView() {
        topicAdapter = new TopicAdapter(this.getActivity(), data);
        listView.setAdapter(topicAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, view.getTag().toString());
//                Log.e(TAG, data.get((Integer) view.getTag()).getTopic_title());
            }
        });
    }

    /**
     * 初始化数据，从缓存里读入，ACache
     */
    private void initData() {
        JSONArray replyThemeList = aCache.getAsJSONArray("themeList");
        for (int i = 0; i < replyThemeList.length(); i++) {
            try {
                JSONObject theme = replyThemeList.getJSONObject(i);
                String themeTitle = theme.getString("theme_name");
                String themeContent = theme.getString("theme_content");
                String themeName = theme.getString("member_name");
                TopicMessage msg = new TopicMessage(themeTitle, themeContent, themeName, "12小时前", "5条回复");
                msg.setIcon_url("http://pic1.nipic.com/2008-08-12/200881211331729_2.jpg");
                msg.setTopic_user_icon("https://cdn.v2ex.co/avatar/7b5a/e206/89357_large.png");
                data.add(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化ViewPager，即滚动的视图
     */
    private void initViewPager() {
        imageIdList = new ArrayList<Integer>();
        // TODO 添加网络图片，设置跳转链接
        imageIdList.add(R.mipmap.banner1);
        imageIdList.add(R.mipmap.banner2);
        imageIdList.add(R.mipmap.banner3);
        imageIdList.add(R.mipmap.banner4);

        //  调用SystemUtils的获取宽高方法
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (SystemUtils.getScreen(this.getActivity()).heightPixels / 4));
        viewPager.setLayoutParams(params);
        viewPager.setAdapter(new ImagePagerAdapter(this.getActivity(), imageIdList).setInfiniteLoop(true));

        //  setOnPageChangeListener过期方法，被addOnPageChangeListener、removeOnPageChangeListener代替
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        viewPager.setInterval(2000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % ListUtils.getSize(imageIdList));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_my_order:
                Intent intent = new Intent();
                intent.setClass(HomeFragment.this.getActivity(), CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_my_trade:
                break;
        }
    }

    /**
     * 一个ViewPager的OnPageChangeListener类，可以重写onPageSelected方法去添加小圆点等
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}

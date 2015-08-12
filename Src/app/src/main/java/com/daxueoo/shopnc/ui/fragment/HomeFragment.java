package com.daxueoo.shopnc.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daxueoo.shopnc.R;
import com.daxueoo.shopnc.adapter.ImagePagerAdapter;
import com.daxueoo.shopnc.adapter.TopicAdapter;
import com.daxueoo.shopnc.model.TopicMessage;
import com.daxueoo.shopnc.network.adapter.NormalPostRequest;
import com.daxueoo.shopnc.utils.ConstUtils;
import com.daxueoo.shopnc.utils.ListUtils;
import com.daxueoo.shopnc.utils.SystemUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;

/**
 * 这是首页的Fragment，主要有滚动图片，ListView，Button等
 */

public class HomeFragment extends BaseFragment {

    private String TAG = "HomeFragment";

    //  自动滚动图片
    private AutoScrollViewPager viewPager;
    private List<Integer> imageIdList;

    //  Header标题
    private TextView tv_title;

    private ListView mListView;

    private List<TopicMessage> data = new ArrayList<TopicMessage>();
    private TopicAdapter mAdapter;

    private PtrClassicFrameLayout mPtrFrame;
    private ScrollView mScrollView;
    private LoadMoreListViewContainer loadMoreListViewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(activity, R.layout.fragment_home, null);
        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_web_view_frame);
        //  滚动图片
        viewPager = (AutoScrollViewPager) view.findViewById(R.id.view_pager);

        tv_title = (TextView) view.findViewById(R.id.titlebar_tv);

        mListView = (ListView) view.findViewById(R.id.list_fragment_topic);

        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);

        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_list_view_container);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_title.setText(R.string.tab_tv_index);

        thread.start();
        //  初始化滚动图片
        initViewPager();
        //  初始化ListView，后期换成RecyclerView
        //  TODO    换成RecyclerView
        initListView();
        //  初始化数据
        initData();
        //  设置ScrollView嵌套ListView的滚动问题
        SystemUtils.setListViewHeightBasedOnChildren(mListView);
        //  初始化下拉刷新
        initPtr();
        //  初始化上拉加载
        initLoadMore();

    }

    /**
     * 初始化加载更多
     */
    private void initLoadMore() {
        //TODO 加载更多
        // load more container
        loadMoreListViewContainer.useDefaultHeader();
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //mDataModel.queryNextPage();
            }
        });

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
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mScrollView, header);
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
                    initOriginData();
                    mAdapter.notifyDataSetChanged(); // 发送消息通知ListView更新
                    break;
                case 1:
                    Log.e(TAG, "test");
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
                Toast.makeText(HomeFragment.this.getActivity(), "获取数据成功。", Toast.LENGTH_LONG).show();
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
        mAdapter = new TopicAdapter(this.getActivity(), data);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 初始化数据，从缓存里读入，ACache
     */
    private void initData() {
        for (int i = 0; i < 10; i++) {
            TopicMessage msg = new TopicMessage("标题", "这是一个内容,tv_views,tv_views,tv_views,tv_views,tv_views,tv_views", "独步清风", "12小时前", "5条回复");
            msg.setIcon_url("http://pic1.nipic.com/2008-08-12/200881211331729_2.jpg");
            data.add(msg);
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

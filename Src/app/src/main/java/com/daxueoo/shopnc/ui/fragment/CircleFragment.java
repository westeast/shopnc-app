package com.daxueoo.shopnc.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daxueoo.shopnc.R;
import com.daxueoo.shopnc.adapter.CircleAdapter;
import com.daxueoo.shopnc.model.CircleMessage;
import com.daxueoo.shopnc.model.TopicMessage;
import com.daxueoo.shopnc.utils.ConstUtils;
import com.daxueoo.shopnc.utils.TitleBuilder;
import com.daxueoo.shopnc.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 这是圈子的fragment
 * Created by user on 15-8-2.
 */
public class CircleFragment extends BaseFragment{
    private static final String TAG = "CircleFragment";
    //  视图
    private View view;
    //  适配器
    private CircleAdapter mAdapter;
    //  网格
    private GridView gridView;

    private List<CircleMessage> data = new ArrayList<CircleMessage>();
    private PtrClassicFrameLayout mPtrFrame;

    private Boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = View.inflate(activity, R.layout.fragment_circle, null);

        new TitleBuilder(view).setTitleText("圈子").setRightImage(R.drawable.tilebar_right_btn).setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(activity, "right click~", Toast.LENGTH_SHORT);
            }
        });

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_web_view_frame);

        gridView = (GridView) view.findViewById(R.id.gridview);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  初始化GridView
        initGridView();
        //  初始化数据
        if (isFirst){
            initData();
            isFirst = false;
        }
        //  初始化下拉刷新
        initPtr();
    }

    /**
     * 初始化下拉刷新
     */
    private void initPtr() {
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, gridView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrame.refreshComplete();
                    }
                }, 100);
            }
        });
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                //mPtrFrame.autoRefresh();
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
                Toast.makeText(CircleFragment.this.getActivity(), R.string.network_fail, Toast.LENGTH_SHORT).show();
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
                    break;
                case 1:
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
     * 初始化GridView
     */
    private void initGridView() {
        mAdapter = new CircleAdapter(this.getActivity(), data);
        gridView.setAdapter(mAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 0; i < 10; i++) {
            CircleMessage msg = new CircleMessage("http://pic1.nipic.com/2008-08-12/200881211331729_2.jpg","标题", "这是一个内容,tv_views,tv_viewsws","15/100人");
            data.add(msg);
        }
    }

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
                Toast.makeText(CircleFragment.this.getActivity(), "获取数据失败，请重新获取。", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.toString());
            }
        });

        requestQueue.add(objRequest);
        requestQueue.start();
    }
}

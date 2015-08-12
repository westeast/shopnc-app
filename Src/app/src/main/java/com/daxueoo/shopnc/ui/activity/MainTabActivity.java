package com.daxueoo.shopnc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.daxueoo.shopnc.R;
import com.daxueoo.shopnc.sdk.Shopnc;
import com.daxueoo.shopnc.ui.fragment.CircleFragment;
import com.daxueoo.shopnc.ui.fragment.HomeFragment;
import com.daxueoo.shopnc.ui.fragment.MallFragment;
import com.daxueoo.shopnc.ui.fragment.TradeFragment;
import com.daxueoo.shopnc.ui.fragment.UserFragment;
import com.daxueoo.shopnc.utils.ConstUtils;

/**
 * 主页的Activity.FragmentTabHost设置跳转
 * Created by user on 15-8-2.
 */
public class MainTabActivity extends FragmentActivity {

    private String TAG = "MainTabActivity";

    //  定义FragmentTabHost对象
    private FragmentTabHost fragmentTabHost;
    //  单选
    private RadioGroup radioGroup;

    //  定义那些Fragment
    private final Class[] fragments = {HomeFragment.class, MallFragment.class, CircleFragment.class, TradeFragment.class, UserFragment.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.fl_content);

        // 得到fragment的个数
        int count = fragments.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(i + "").setIndicator(i + "");
            // 将Tab按钮添加进Tab选项卡中
            fragmentTabHost.addTab(tabSpec, fragments[i], null);
        }

        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    //  首页
                    case R.id.rb_home:
                        fragmentTabHost.setCurrentTab(0);
                        break;
                    //  商城
                    case R.id.rb_mall:
                        //  跳转到WebViewActivity
                        toWebViewActivity();
                        //  mTabHost.setCurrentTab(1);
                        break;
                    //  圈子
                    case R.id.rb_circle:
                        fragmentTabHost.setCurrentTab(2);
                        break;
                    //  交易
                    case R.id.rb_trade:
                        fragmentTabHost.setCurrentTab(3);
                        break;
                    //  个人中心
                    case R.id.rb_user:
                        //这里判断是否有token，然后弹出对话框
                        Shopnc.isLogin(MainTabActivity.this);
                        fragmentTabHost.setCurrentTab(4);
                        break;
                    default:
                        break;
                }
            }
        });

        fragmentTabHost.setCurrentTab(0);
    }

    /**
     * 跳转到WebViewActivity，暂时商城用
     */
    private void toWebViewActivity() {
        Intent intent = new Intent();
        intent.setClass(MainTabActivity.this, WebViewActivity.class);
        intent.putExtra("url", ConstUtils.URL_WAP_SHOPNC);
        intent.putExtra("type", ConstUtils.WEB_TYPE_MALL);
        startActivity(intent);
        finish();
    }
}

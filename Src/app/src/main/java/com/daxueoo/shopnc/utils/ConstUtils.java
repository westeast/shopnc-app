package com.daxueoo.shopnc.utils;

/**
 * 常量类
 * Created by user on 15-8-2.
 */
public class ConstUtils {

    //  客户端类型
    public static final String CLIENT_TYPE = "android";

    //  商城类型
    public static final String WEB_TYPE_MALL = "mall";

    //  圈子类型
    public static final String WEB_TYPE_CIRCLE = "circle";

    //  首页地址
    public static final String URL_WAP_SHOPNC = "http://daxueoo.com:8080/mobile";

    public static final String URL_WAP_THEME = "";

    public static final String URL_API_SHOPNC = "http://daxueoo.com:9091/api";

    //  用户登录地址
    public static final String LOGIN_URL = URL_API_SHOPNC + "/index.php?act=login";

    //  用户注册地址
    public static final String REGISTER_URL = URL_WAP_SHOPNC + "/index.php?act=login&op=register";

    //  圈子api地址
    public static final String CIRCLE_URL = URL_API_SHOPNC + "/index.php?act=circle&op=";

    //  圈子热门话题
    public static final String HOT_THEME = CIRCLE_URL + "get_reply_themelist";

    //  获取用户信息api
    public static final String USER_PROFILES_API = URL_API_SHOPNC + "/index.php?act=user_center&op=user_info" ;

}
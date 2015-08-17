package com.daxueoo.shopnc.adapter;

/**
 * Created by user on 15-7-30.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.daxueoo.shopnc.R;
import com.daxueoo.shopnc.model.TopicMessage;
import com.daxueoo.shopnc.ui.activity.ImagePreviewActivity;
import com.daxueoo.shopnc.ui.activity.ThemeDetailActivity;
import com.daxueoo.shopnc.utils.BitmapCache;
import com.daxueoo.shopnc.widgets.RelativeTimeTextView;
import com.daxueoo.shopnc.widgets.RoundedImageView;

import java.util.List;

/**
 * Created by user on 15-7-30.
 */

public class TopicAdapter extends BaseAdapter {
    /**
     * 上下文对象
     */
    private Context mContext = null;
    public List<TopicMessage> data;

    private ImageLoader mImageLoader;

    public TopicAdapter(Context ctx, List<TopicMessage> data) {
        mContext = ctx;
        this.data = data;

        RequestQueue mQueue = Volley.newRequestQueue(ctx);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_topic, parent, false);
            holder = new ViewHolder();
            holder.topiclist = (RelativeLayout) convertView.findViewById(R.id.topiclist);

            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_topic_title);
            holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_topic_content);

            holder.iv_head_icon = (NetworkImageView) convertView.findViewById(R.id.iv_topic_head);

            holder.iv_head_user_icon = (RoundedImageView) convertView.findViewById(R.id.iv_topic_user_head);

            holder.tv_views = (TextView) convertView.findViewById(R.id.tv_topic_views);

            convertView.setTag(holder);
        } else {// 直接获得ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        final TopicMessage msg = data.get(position);

        holder.tv_title.setText(msg.getTopic_title());
        holder.tv_desc.setText(msg.getTopic_content());
        holder.tv_views.setText(msg.getTopic_views());

        //  图片的点击事件
        holder.iv_head_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ImagePreviewActivity.class);
                intent.putExtra("pic_url", msg.getIcon_url());
                mContext.startActivity(intent);
            }
        });

        //  标题的点击事件
        holder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ThemeDetailActivity.class);
                mContext.startActivity(intent);
                Toast.makeText(mContext, msg.getTopic_title(), Toast.LENGTH_LONG).show();
            }
        });

        //  内容的点击事件，和标题同理
        holder.tv_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ThemeDetailActivity.class);
                mContext.startActivity(intent);
                Toast.makeText(mContext, msg.getTopic_content(), Toast.LENGTH_LONG).show();
            }
        });

        //  用户头像的点击事件
        holder.iv_head_user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ThemeDetailActivity.class);
                mContext.startActivity(intent);
                Toast.makeText(mContext, msg.getTopic_user_icon(), Toast.LENGTH_LONG).show();
            }
        });

        //  用户昵称的点击事件，和头像同理
        holder.tv_views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ThemeDetailActivity.class);
                mContext.startActivity(intent);
                Toast.makeText(mContext, msg.getTopic_views(), Toast.LENGTH_LONG).show();
            }
        });

        if (msg.getIcon_url() == null || msg.getIcon_url().equals("")) {
            holder.iv_head_icon.setVisibility(View.GONE);
        } else {
            holder.iv_head_icon.setDefaultImageResId(android.R.drawable.ic_menu_rotate);
            holder.iv_head_icon.setErrorImageResId(R.mipmap.ic_launcher);
            holder.iv_head_icon.setImageUrl(msg.getIcon_url(), mImageLoader);
        }

        if (msg.getTopic_user_icon() == null || msg.getTopic_user_icon().equals("")) {
            holder.iv_head_user_icon.setVisibility(View.GONE);
        } else {
            RequestQueue mQueue = Volley.newRequestQueue(mContext);
            ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.iv_head_user_icon, android.R.drawable.ic_menu_rotate, R.mipmap.ic_launcher);
            imageLoader.get(msg.getTopic_user_icon(), listener);
        }

        return convertView;
    }

    static class ViewHolder {

        RelativeLayout topiclist;
        //  标题
        TextView tv_title;
        //  内容
        TextView tv_desc;
        //  昵称
        TextView tv_views;

        //  配图
        NetworkImageView iv_head_icon;
        //  用户头像
        RoundedImageView iv_head_user_icon;
    }

}
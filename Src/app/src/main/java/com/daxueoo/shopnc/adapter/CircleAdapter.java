package com.daxueoo.shopnc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.daxueoo.shopnc.R;
import com.daxueoo.shopnc.model.CircleMessage;
import com.daxueoo.shopnc.utils.BitmapCache;
import com.daxueoo.shopnc.widgets.RelativeTimeTextView;

import java.util.List;

/**
 * Created by user on 15-8-4.
 */

public class CircleAdapter extends BaseAdapter {
    /**
     * 上下文对象
     */
    private Context mContext = null;
    public List<CircleMessage> data;

    private ImageLoader mImageLoader;


    public CircleAdapter(Context ctx, List<CircleMessage> data) {
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

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_circle, parent, false);
            holder = new ViewHolder();
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_circle);

            holder.rl_text = (RelativeLayout) convertView.findViewById(R.id.rl_text);
            holder.circle_title = (TextView) convertView.findViewById(R.id.tv_circle_title);
            holder.circle_desc = (TextView) convertView.findViewById(R.id.tv_circle_desc);
            holder.circle_people = (TextView) convertView.findViewById(R.id.tv_circle_people);

            holder.iv_circle = (ImageView) convertView.findViewById(R.id.itemimageview);

            convertView.setTag(holder);
        } else {//  直接获得ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        CircleMessage msg = data.get(position);

        holder.circle_title.setText(msg.getTitle());
        holder.circle_desc.setText(msg.getContent());
        holder.circle_people.setText(msg.getPeople());

        holder.iv_circle.setImageResource(R.drawable.test_circle);
        holder.rl_text.setAlpha(155);

//        holder.iv_circle.setImageAlpha(155);
//        holder.iv_circle.setAlpha(155);

        return convertView;
    }

    static class ViewHolder {

        RelativeLayout relativeLayout;
        RelativeLayout rl_text;
        TextView circle_title;
        TextView circle_desc;
        TextView circle_people;

        ImageView iv_circle;
    }

}
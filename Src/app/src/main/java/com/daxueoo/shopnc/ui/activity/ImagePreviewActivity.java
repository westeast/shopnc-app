package com.daxueoo.shopnc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.daxueoo.shopnc.R;
import com.daxueoo.shopnc.utils.BitmapCache;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by user on 15-8-14.
 */
public class ImagePreviewActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        TextView tv_title = (TextView) findViewById(R.id.titlebar_tv);
        tv_title.setText("图片预览");

        Intent intent = getIntent();
        String pic_url = intent.getStringExtra("pic_url");

        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);

        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(photoView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_menu_rotate);
        imageLoader.get(pic_url, listener);
    }

}

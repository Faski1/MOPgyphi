package com.example.mopgyphi.Helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mopgyphi.Helper.roomHelper.GifEntity;
import com.example.mopgyphi.MainActivity;
import com.example.mopgyphi.Models.Gif;
import com.example.mopgyphi.MyApp;
import com.example.mopgyphi.R;

import java.util.List;

public class GridAdapter extends BaseAdapter {


    private Context mContext;
    private List<GifEntity> listaGifova;


    public GridAdapter(Context mContext, List<GifEntity> gifList) {
        this.mContext = mContext;
        this.listaGifova = gifList;
    }


    @Override
    public int getCount() {
        return listaGifova.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);

            GlideApp.with(mContext).asGif().diskCacheStrategy(DiskCacheStrategy.DATA)
                    .load(listaGifova.get(position).fixedhurl)
                    .optionalCenterCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(imageView);
        return imageView;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}


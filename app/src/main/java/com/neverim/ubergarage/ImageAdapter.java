package com.neverim.ubergarage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bitmap> images;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, ArrayList<Bitmap> images) {
        this.context = context;
        this.images = images;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.grid_item, null); // inflate the layout
        ImageView icon = convertView.findViewById(R.id.grid_image); // get the reference of ImageView
        icon.setImageBitmap(images.get(position)); // set logo images
        icon.setClipToOutline(true);
        return convertView;
    }
}

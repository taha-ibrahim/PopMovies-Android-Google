package com.example.taha.shofmovie;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Taha on 8/21/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> links ;


    public ImageAdapter(Context c,ArrayList<Movie> x) {
        mContext = c;
        links  = x;
}


    public int getCount() {
        return links.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager window = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            window.getDefaultDisplay().getMetrics(metrics);
            if(MainActivity.mTwoPane == true)
                imageView.setLayoutParams(new GridView.LayoutParams((int) ((metrics.widthPixels/2)/3),(int)(((metrics.widthPixels/2)/3)*1.42)));
            else
                imageView.setLayoutParams(new GridView.LayoutParams(metrics.widthPixels / 2, (int)((metrics.widthPixels/2)*1.42)));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(this.mContext).setLoggingEnabled(true);


            Picasso.with(this.mContext).load(links.get(position).getMoviePoster()).into(imageView);


        return imageView;
    }

}

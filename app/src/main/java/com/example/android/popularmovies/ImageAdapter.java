package com.example.android.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by safwanx on 9/26/16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public void setmThumbIds(String[] mThumbIds) {
        this.mThumbIds = mThumbIds;
    }

    // references to our images
    private String[] mThumbIds = null;


    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    public int getCount() {
        return (mThumbIds == null ? 0 : mThumbIds.length);
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    //create a new image view for each item referenced by the adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            //if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            //Calculate the height(in pixels) for the image for different devices.
            Resources resources = mContext.getResources();
            float heightPx = TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            mContext.getResources().getInteger(R.integer.poster_height_dp),
                            resources.getDisplayMetrics());

            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) heightPx));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //imageView.setPadding(8,8,8,8);
        } else {
            imageView = (ImageView) convertView;
        }


        if (mThumbIds != null) {
            String url = mThumbIds[position];
            Picasso.with(mContext).load(url).placeholder(R.drawable.default_preview)
                    .into(imageView);
        }

        return imageView;
    }


}

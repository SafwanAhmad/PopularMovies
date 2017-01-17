package com.example.android.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * We are using a cursor adapter to easily load and bind data. As this is being used with cursor loader hence
 * the loader manager will take care of re querying on data being changed. So while creating new ImageAdapter we
 * will pass 0 as the third parameter in the recommended constructor.
 *
 * Created by safwanx on 9/26/16.
 */
public class ImageAdapter extends CursorAdapter {

    public ImageAdapter(Context context, Cursor cursor, int flag) {
        super(context, cursor, flag);
    }

    @Override
    public int getCount() {
        return ( getCursor() == null ? 0 : getCursor().getCount() );
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView;

        imageView = new ImageView(context);

        //Calculate the height(in pixels) for the image for different devices.
        Resources resources = mContext.getResources();
        float heightPx = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        mContext.getResources().getInteger(R.integer.poster_height_dp),
                        resources.getDisplayMetrics());

        imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) heightPx));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setPadding(8,8,8,8);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // The binding is simple. We have our image view that was created in newView method. Also
        // cursor is automatically pointing to the right data (it's position is based on the item
        // position in the grid). So we just take out the string corresponding to poster url and
        // use Picasso to download image and attach that image to this image view.

        if (cursor != null) {
            //TODO Assign mapping of columns inside MoviePostersFragment
            String url = cursor.getString(1);

            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.default_preview)
                    //Adding caching of poster for offline access
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into((ImageView)view);
        }
    }
}

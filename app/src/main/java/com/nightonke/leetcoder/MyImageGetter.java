package com.nightonke.leetcoder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Weiping on 2016/1/8.
 */
public class MyImageGetter implements Html.ImageGetter {

    private Context mContext;
    private Drawable drawable;
    private String source;
    private TextView textView;

    MyImageGetter(Context mContext, TextView textView) {
        this.mContext = mContext;
        this.textView = textView;
        drawable = ContextCompat.getDrawable(mContext, R.drawable.rocket);
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = ContextCompat.getDrawable(mContext, R.drawable.rocket);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new ImageGetterAsyncTask(mContext, source, d).execute(textView);

        return d;

//        Log.d("LeetCoder", source);
//        this.source = source;
//        Picasso.with(mContext).load(source).into(target);
//        return drawable;
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            drawable = new BitmapDrawable(mContext.getResources(), bitmap);
            getDrawable(source);
            Log.d("LeetCoder", "Loaded");
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("LeetCoder", "Failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("LeetCoder", "Prepare");
        }
    };

    class ImageGetterAsyncTask extends AsyncTask<TextView, Void, Bitmap> {


        private LevelListDrawable levelListDrawable;
        private Context context;
        private String source;
        private TextView t;

        public ImageGetterAsyncTask(Context context, String source, LevelListDrawable levelListDrawable) {
            this.context = context;
            this.source = source;
            this.levelListDrawable = levelListDrawable;
        }

        @Override
        protected Bitmap doInBackground(TextView... params) {
            t = params[0];
            try {
                return Picasso.with(context).load(source).get();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            try {
                Log.d("LeetCoder", "Finish");
                Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                Point size = new Point();
                ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(size);
                int multiplier = size.x / bitmap.getWidth();
                levelListDrawable.addLevel(1, 1, d);
                levelListDrawable.setBounds(0, 0, bitmap.getWidth() * multiplier, bitmap.getHeight() * multiplier);
                levelListDrawable.setLevel(1);
                t.setText(t.getText()); // invalidate() doesn't work correctly...
                t.invalidate();
            } catch (Exception e) { /* Like a null bitmap, etc. */ }
        }
    }
}

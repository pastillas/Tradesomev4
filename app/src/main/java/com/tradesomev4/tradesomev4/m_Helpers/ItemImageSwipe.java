package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tradesomev4.tradesomev4.R;

import java.io.InputStream;

public class ItemImageSwipe extends PagerAdapter {
    private Bitmap imageSource[] = new Bitmap[4];
    private Context ctx;
    private LayoutInflater layoutInflater;
    private String imageUrl[] = new String[4];

    public ItemImageSwipe(Context c, String image1Url, String image2Url, String image3Url, String image4Url ) {
        this.imageUrl[0] = image1Url;
        this.imageUrl[1] = image2Url;
        this.imageUrl[2] = image3Url;
        this.imageUrl[3] = image4Url;
        ctx = c;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.activity_custom_swip, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.swip_image_view);

        if(imageSource[position] == null){
            Glide.with(ctx)
                    .load(imageUrl[position]).centerCrop()
                    .into(imageView);
            //new ImageLoadTask(imageView, position).execute(imageUrl[position]);
        }

        imageView.setImageBitmap(imageSource[position]);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
        private int position;
        private ImageView imageView;

        public ImageLoadTask(ImageView imageView, int position) {
            this.position = position;
            this.imageView = imageView;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Log.d("MainActivity", "doInBackround");

            String urlDisplay = urls[0];
            Bitmap myBitmap = null;
            try {
                InputStream input = new java.net.URL(urlDisplay).openStream();
                myBitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                Log.d("MainActivity", "BitmapParseError");
                e.printStackTrace();
            }

            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            imageSource[position] = result;
        }
    }
}
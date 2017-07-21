package com.mfino.bsim.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.util.ArrayList;

import com.mfino.bsim.R;

public class ImageSliderAdapter extends PagerAdapter {

	private ArrayList<String> IMAGES;
	private LayoutInflater inflater;
	private Context context;
    private ProgressBar progressbar;

	public ImageSliderAdapter(Context context, ArrayList<String> IMAGES) {
		this.context = context;
		this.IMAGES = IMAGES;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return IMAGES.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		View imageLayout = inflater.inflate(R.layout.imageslider_frame, view, false);

		assert imageLayout != null;
		final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
		final ProgressBar progressbar = (ProgressBar) imageLayout.findViewById(R.id.progressbar);

		new DownloadImageTask(progressbar, imageView).execute(IMAGES.get(position));
		imageView.setScaleType(ScaleType.FIT_XY);
		view.addView(imageLayout, 0);

		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	    ProgressBar progressbar;

	    public DownloadImageTask(ProgressBar progressbar, ImageView bmImage) {
	        this.bmImage = bmImage;
	        this.progressbar = progressbar;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0].trim();
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	progressbar.setVisibility(View.GONE);
	    	Resources res = context.getResources();
	    	RoundedBitmapDrawable dr =
	    	    RoundedBitmapDrawableFactory.create(res, result);
	    	dr.setCornerRadius(25.0f);
	    	bmImage.setImageDrawable(dr);
	    	bmImage.setScaleType(ScaleType.FIT_XY);

	    	//bmImage.setImageDrawable(img);
	        //bmImage.setImageBitmap(result);
	    }
	}

}
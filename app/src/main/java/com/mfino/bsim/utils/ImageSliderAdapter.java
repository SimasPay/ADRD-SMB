package com.mfino.bsim.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import com.mfino.bsim.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class ImageSliderAdapter extends PagerAdapter {

	private ArrayList<String> IMAGES;
	private LayoutInflater inflater;
	private Context context;

	public ImageSliderAdapter(Context context, ArrayList<String> IMAGES) {
		this.context = context;
		this.IMAGES = IMAGES;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return IMAGES.size();
	}

	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup view, int position) {
		View imageLayout = inflater.inflate(R.layout.imageslider_frame, view, false);

		assert imageLayout != null;
		final ImageView imageView = imageLayout.findViewById(R.id.image);
		final ProgressBar progressbar = imageLayout.findViewById(R.id.progressbar);

		new DownloadImageTask(progressbar, imageView).execute(IMAGES.get(position));
		imageView.setScaleType(ScaleType.FIT_XY);
		view.addView(imageLayout, 0);

		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}
	
	@SuppressLint("StaticFieldLeak")
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	    ProgressBar progressbar;

	    private DownloadImageTask(ProgressBar progressbar, ImageView bmImage) {
	        this.bmImage = bmImage;
	        this.progressbar = progressbar;
	    }

	    protected Bitmap doInBackground(String... urls) {
	    	/*
			String urldisplay = urls[0].trim();
			Bitmap bMap = null;
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
			URL url;
			BufferedOutputStream out;
			InputStream in;
			BufferedInputStream buf;

			//BufferedInputStream buf;
			try {
				url = new URL(urldisplay);
				in = url.openStream();
				//in.reset();
				buf = new BufferedInputStream(in);
				bMap = BitmapFactory.decodeStream(buf);
				buf.reset();
				if (in != null) {
					in.close();
				}
				buf.close();
			} catch (Exception e) {
				Log.e("Error reading file", e.toString());
			}
	        return bMap;
			*/
			// TODO Auto-generated method stub
			String urlStr = urls[0].trim();
			Bitmap img = null;

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlStr);
			HttpResponse response;
			try {
				response = (HttpResponse)client.execute(request);
				HttpEntity entity = response.getEntity();
				BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
				InputStream inputStream = bufferedEntity.getContent();
				img = BitmapFactory.decodeStream(inputStream);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img;
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
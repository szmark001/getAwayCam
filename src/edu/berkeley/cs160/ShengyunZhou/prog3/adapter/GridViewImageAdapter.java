package edu.berkeley.cs160.ShengyunZhou.prog3.adapter;

import edu.berkeley.cs160.ShengyunZhou.prog3.FullScreenViewActivity;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.AppConstant;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewImageAdapter extends BaseAdapter {

	private Activity _activity;
	private ArrayList<String> _filePaths = new ArrayList<String>();
	private int imageWidth;
	private ExifInterface exifReader;
	private Double mLongitude, mLatitude;
	private Utils _utils;

	public GridViewImageAdapter(Activity activity, ArrayList<String> filePaths,
			int imageWidth, Double latitude, Double longitude) {
		this._activity = activity;
		this._filePaths = filePaths;
		this.imageWidth = imageWidth;
		this.mLongitude = longitude;
		this.mLatitude = latitude;
		this._utils = new Utils(activity);
	}

	@Override
	public int getCount() {
		return this._filePaths.size();
	}

	@Override
	public Object getItem(int position) {
		return this._filePaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		String rawLongitude, rawLatitude;
		double distance = 0;
		double photoLongitude = 1, photoLatitude = 1;
		double alpha = 1;
		
		try {
			exifReader = new ExifInterface(_filePaths.get(position));
		} catch (IOException e) {
			Log.d(AppConstant.PHOTO_ALBUM, "failed to get Exif info");
		}
		
		rawLatitude = exifReader.getAttribute(exifReader.TAG_GPS_LATITUDE);
		rawLongitude = exifReader.getAttribute(exifReader.TAG_GPS_LONGITUDE);
		Log.d(AppConstant.PHOTO_ALBUM, "RawValues:"+rawLatitude+" "+rawLongitude);
		if (rawLatitude == null) {
			photoLatitude = 0;
		} else {
			photoLatitude = _utils.dmsToDecimal(rawLatitude);
		}
		if (rawLongitude == null) {
			photoLongitude = 0;
		} else {
			photoLongitude = _utils.dmsToDecimal(rawLongitude);
		}
		
		distance = Math.sqrt((mLatitude - photoLatitude)*(mLatitude - photoLatitude) + (mLongitude - photoLongitude)*(mLongitude - photoLongitude));
		if (distance < AppConstant.MIN_DISTANCE) {
			alpha = distance/AppConstant.MIN_DISTANCE;
		}
		
		if (convertView == null) {
			imageView = new ImageView(_activity);
		} else {
			imageView = (ImageView) convertView;
		}

		// get screen dimensions
		Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
				imageWidth);

		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
				imageWidth));
		imageView.setImageBitmap(image);

		// image view click listener
		imageView.setOnClickListener(new OnImageClickListener(position));
		imageView.setAlpha((float) alpha);
		return imageView;
	}

	class OnImageClickListener implements OnClickListener {

		int _postion;

		// constructor
		public OnImageClickListener(int position) {
			this._postion = position;
		}

		@Override
		public void onClick(View v) {
			// on selecting grid view image
			// launch full screen activity

			Intent i = new Intent(_activity, FullScreenViewActivity.class);
			i.putExtra("position", _postion);
			_activity.startActivity(i);
		}

	}

	/*
	 * Resizing image size
	 */
	public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {

			File f = new File(filePath);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}

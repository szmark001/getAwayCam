package edu.berkeley.cs160.ShengyunZhou.prog3.adapter;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import edu.berkeley.cs160.ShengyunZhou.prog3.R;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.AppConstant;

import edu.berkeley.cs160.ShengyunZhou.prog3.helper.Utils;

public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private ArrayList<String> _imagePaths;
	private LayoutInflater inflater;
	private ExifInterface exifReader;
	private Double mLongitude, mLatitude;

	private Utils _utils;

	// constructor
	public FullScreenImageAdapter(Activity activity,
			ArrayList<String> imagePaths, Double latitude, Double longitude) {
		this._activity = activity;
		this._imagePaths = imagePaths;
		this.mLongitude = longitude;
		this.mLatitude = latitude;
		this._utils = new Utils(activity);
	}

	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		boolean rtn = (view == ((RelativeLayout) object));
		/*
		 * String rawLongitude, rawLatitude; double distance = 0; double
		 * photoLongitude = 1, photoLatitude = 1; double alpha = 1; int
		 * position;
		 * 
		 * if (rtn) { position = getItemPosition(object); try { exifReader = new
		 * ExifInterface(_imagePaths.get(position)); } catch (IOException e) {
		 * Log.d(AppConstant.PHOTO_ALBUM, "failed to get Exif info"); }
		 * 
		 * rawLatitude = exifReader.getAttribute(exifReader.TAG_GPS_LATITUDE);
		 * rawLongitude = exifReader
		 * .getAttribute(exifReader.TAG_GPS_LONGITUDE); //
		 * Log.d(AppConstant.PHOTO_ALBUM, //
		 * "RawValues:"+rawLatitude+" "+rawLongitude); if (rawLatitude == null)
		 * { photoLatitude = 0; } else { photoLatitude =
		 * _utils.dmsToDecimal(rawLatitude); } if (rawLongitude == null) {
		 * photoLongitude = 0; } else { photoLongitude =
		 * _utils.dmsToDecimal(rawLongitude); } Toast.makeText( this._activity,
		 * "Current Latitude: " + mLatitude + "\n" + "Current Longitude: " +
		 * mLongitude + "\n" + "Photo Latitude: " + photoLatitude + "\n" +
		 * "Photo Longitude: " + photoLongitude, Toast.LENGTH_LONG).show(); }
		 */

		return rtn;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imgDisplay;
		String rawLongitude, rawLatitude;
		double distance = 0;
		double photoLongitude = 1, photoLatitude = 1;
		double alpha = 1;
		try {
			exifReader = new ExifInterface(_imagePaths.get(position));
		} catch (IOException e) {
			Log.d(AppConstant.PHOTO_ALBUM, "failed to get Exif info");
		}

		rawLatitude = exifReader.getAttribute(exifReader.TAG_GPS_LATITUDE);
		rawLongitude = exifReader.getAttribute(exifReader.TAG_GPS_LONGITUDE);
		// Log.d(AppConstant.PHOTO_ALBUM,
		// "RawValues:"+rawLatitude+" "+rawLongitude);
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

		Toast.makeText(
				this._activity,
				"Current Latitude: " + mLatitude + "\n" + "Current Longitude: "
						+ mLongitude + "\n" + "Photo Latitude: "
						+ photoLatitude + "\n" + "Photo Longitude: "
						+ photoLongitude, Toast.LENGTH_LONG).show();

		distance = Math.sqrt((mLatitude - photoLatitude)
				* (mLatitude - photoLatitude) + (mLongitude - photoLongitude)
				* (mLongitude - photoLongitude));
		if (distance < AppConstant.MIN_DISTANCE) {
			alpha = distance / AppConstant.MIN_DISTANCE;
		}
		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image,
				container, false);

		imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position),
				options);
		imgDisplay.setImageBitmap(bitmap);
		imgDisplay.setAlpha((float) alpha);

		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}
}
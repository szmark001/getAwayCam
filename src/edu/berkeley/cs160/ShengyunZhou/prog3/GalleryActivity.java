package edu.berkeley.cs160.ShengyunZhou.prog3;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;
import edu.berkeley.cs160.ShengyunZhou.prog3.adapter.GridViewImageAdapter;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.AppConstant;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.MyLocationListener;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.Utils;

public class GalleryActivity extends Activity {
	private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private Intent cameraIntent;
    private Uri fileUri;
    private MyLocationListener mLocationListener;
    private double mylatitude;
    private double mylongitude;
    private ExifInterface exifReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		gridView = (GridView) findViewById(R.id.grid_view);
		 
        utils = new Utils(this);
 
        // Initilizing Grid View
        InitilizeGridLayout();
 
        // loading all image paths from SD card
        imagePaths = utils.getFilePaths();
        
        // initiallizing gps
        mLocationListener = new MyLocationListener(GalleryActivity.this);
        
        if (mLocationListener.canGetLocation()){
            updateLocation();
        }
        
        // Gridview adapter
        adapter = new GridViewImageAdapter(GalleryActivity.this, imagePaths,
                columnWidth, mylatitude, mylongitude);

        // setting grid view adapter
        gridView.setAdapter(adapter);
	}
	//Update location based on the info of the current locationlistener
	public void updateLocation() {
		mLocationListener.getLocation();
		mylatitude = mLocationListener.getLatitude();
        mylongitude = mLocationListener.getLongitude();
        Toast.makeText(this, "Current Latitude: " + mylatitude + "\n" + "Current Longitude: " + mylongitude, Toast.LENGTH_LONG).show();
	}
	
	private void refreshGallery() {
		updateLocation();
		imagePaths = utils.getFilePaths();
		 
        // Gridview adapter
        adapter = new GridViewImageAdapter(GalleryActivity.this, imagePaths,
                columnWidth, mylatitude, mylongitude);
 
        // setting grid view adapter
        gridView.setAdapter(adapter);
	}
	
	private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());
 
        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);
 
        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_camera:
	            initCamera();
	            return true;
	        case R.id.action_refresh:
	        	refreshGallery();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void initCamera() {
		cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(FileColumns.MEDIA_TYPE_IMAGE);
		// create a file to save the image
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// set the image file name
		startActivityForResult(cameraIntent,
				AppConstant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		updateLocation();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == AppConstant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	    	Uri photoUri = null;
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	        	Intent i = new Intent(GalleryActivity.this, InternetPicActivity.class);
    			i.putExtra("longitude", mylongitude);
    			i.putExtra("latitude", mylatitude);
    			GalleryActivity.this.startActivity(i);
    			
	        	photoUri = fileUri;
	        	insertGPSInfo(photoUri.getPath(), mylatitude, mylongitude);
	            this.refreshGallery();
	            Toast.makeText(this, "Image saved to:\n" +
	                     photoUri, Toast.LENGTH_LONG).show();
    			
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        	Toast.makeText(this, "Image not saved", Toast.LENGTH_LONG).show();
	        } else {
	            // Image capture failed, advise user
	        	Toast.makeText(this, "Image capture failed", Toast.LENGTH_LONG).show();
	        }
	    }
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + AppConstant.PHOTO_ALBUM);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(AppConstant.PHOTO_ALBUM, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == FileColumns.MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}
	
	private void insertGPSInfo(String filePath, Double latitude, Double longitude) {
		try {
			exifReader = new ExifInterface(filePath);
		} catch (IOException e) {
			Log.d(AppConstant.PHOTO_ALBUM, "failed to open metaData");
		}
		Log.d(AppConstant.PHOTO_ALBUM, "inserting meta data"+ filePath.toString() +" " + latitude +" " + longitude);
		exifReader.setAttribute(exifReader.TAG_GPS_LATITUDE, utils.decimalToDms(latitude));
		exifReader.setAttribute(exifReader.TAG_GPS_LONGITUDE, utils.decimalToDms(longitude));
		try {
			exifReader.saveAttributes();
		} catch (IOException e) {
			Log.d(AppConstant.PHOTO_ALBUM, "failed to save metaData");
		}
	}
	
}

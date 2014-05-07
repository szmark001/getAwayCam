package edu.berkeley.cs160.ShengyunZhou.prog3;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.berkeley.cs160.ShengyunZhou.prog3.adapter.FullScreenImageAdapter;
import edu.berkeley.cs160.ShengyunZhou.prog3.adapter.GridViewImageAdapter;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.MyLocationListener;
import edu.berkeley.cs160.ShengyunZhou.prog3.helper.Utils;

public class FullScreenViewActivity extends Activity {
	private ViewPager mViewPager;
	private FullScreenImageAdapter mAdapter;
	private Utils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private int initPosition = 0;
	private MyLocationListener mLocationListener;
	private double mylatitude;
	private double mylongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_view);

		utils = new Utils(this);
		imagePaths = utils.getFilePaths();
		mViewPager = (ViewPager) findViewById(R.id.pager);

		// initiallizing gps
		mLocationListener = new MyLocationListener(FullScreenViewActivity.this);

		if (mLocationListener.canGetLocation()) {
			updateLocation();
		}
		mAdapter = new FullScreenImageAdapter(this, imagePaths, mylatitude,
				mylongitude);
		mViewPager.setAdapter(mAdapter);
		initPosition = getIntent().getIntExtra("position", 0);
		mViewPager.setCurrentItem(initPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_screen_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void updateLocation() {
		mLocationListener.getLocation();
		mylatitude = mLocationListener.getLatitude();
		mylongitude = mLocationListener.getLongitude();
	}

	public void refresh() {
		updateLocation();
		imagePaths = utils.getFilePaths();

		
		mAdapter = new FullScreenImageAdapter(this, imagePaths, mylatitude,
				mylongitude);

		mViewPager.setAdapter(mAdapter);
		initPosition = getIntent().getIntExtra("position", 0);
		mViewPager.setCurrentItem(initPosition);
	}

}

package edu.berkeley.cs160.ShengyunZhou.prog3;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.MessageDigest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InternetPicActivity extends Activity {
	private double mylongitude, mylatitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_internet_pic);
		Intent i = getIntent();
		mylongitude = i.getDoubleExtra("longitude", 0);
		mylatitude = i.getDoubleExtra("latitude", 0);
		
		new AsyncTask<Void, Void, Void>() {
			public Void doInBackground(Void... args) {
				try {
					loadFlikrImage();
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
				return null;
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.internet_pic, menu);
		return true;
	}
	private void loadFlikrImage() throws Throwable {
		String outxml="";
		HttpGet request	= new HttpGet();
		
		String sorted = "b5d419b104f55563" + "api_key" + "456f49f9c6ed2c3c7360c4695fdad20d" + "format" + "rest" + "lat" + mylatitude + "lon" + mylongitude + "method" + "flickr.photos.search";
		byte[] bytesOfMessage = sorted.getBytes("UTF-8");
		byte[] signature = MessageDigest.getInstance("MD5").digest(bytesOfMessage);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < signature.length; ++i) {
			sb.append(Integer.toHexString((signature[i] & 0xFF) | 0x100).substring(1,3));
	    }
	    String sig = sb.toString();
		String url= "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=456f49f9c6ed2c3c7360c4695fdad20d&lat="+mylatitude+"&lon="+mylongitude+ "&format=rest&api_sig=" + sig;
		HttpClient client =	new	DefaultHttpClient();	
		HttpResponse response = client.execute(new HttpGet(url));
		StatusLine sl = response.getStatusLine();
			
		if(sl.getStatusCode() == HttpStatus.SC_OK){	
			ByteArrayOutputStream out =	new ByteArrayOutputStream();	
			response.getEntity().writeTo(out);	
			out.close();	
			outxml = out.toString();
		}
		DocumentBuilderFactory	factory	= DocumentBuilderFactory.newInstance();			
		DocumentBuilder	builder	= factory.newDocumentBuilder();
		InputSource	is = new InputSource(new StringReader(outxml));	
		Document doc = builder.parse(is);
		
		Node photo = doc.getElementsByTagName("photo").item(0);
		String farm = photo.getAttributes().getNamedItem("farm").getNodeValue();
		String server = photo.getAttributes().getNamedItem("server").getNodeValue();
		String id = photo.getAttributes().getNamedItem("id").getNodeValue();
		String secret = photo.getAttributes().getNamedItem("secret").getNodeValue();
        String resulturl = "http://farm"+ farm +".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
        final Drawable image = Drawable.createFromStream(((InputStream)new java.net.URL(resulturl).getContent()), resulturl);
        runOnUiThread(new Runnable() {
        	public void run() {
        		Toast.makeText(InternetPicActivity.this, "Pictures Around" + "\nLatitude: "+mylatitude + "\nLongitude: "+ mylongitude, Toast.LENGTH_LONG).show();
                ((ImageView)findViewById(R.id.imageView)).setImageDrawable(image);
                ((TextView)findViewById(R.id.textView1)).setVisibility(View.GONE);
        	}
        });
	}
}

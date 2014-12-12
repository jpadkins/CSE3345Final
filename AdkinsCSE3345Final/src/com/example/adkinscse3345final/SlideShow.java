package com.example.adkinscse3345final;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class SlideShow extends Activity {
	
	// this String array contains the urls of the images
	private String[] images;
	// this is the handle for the ImageView
	private ImageView imageView;
	// this keeps track of the location in the images array;
	private int location;
	// this is an object of a class which extends AsyncTask
	private cycleImages c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide_show);
		
		// set variables
		location = 0;
		c = new cycleImages();
				
		// set handlers
		imageView = (ImageView) findViewById(R.id.image);
		
		// set images from the extra data sent by MainActivity
		images = getIntent().getStringArrayExtra("images");
		
		// start slideshow
		c.execute();
		
	}
	
	// this function changes the image to the next one in the array
	private void setImage() {
		// if there are no more images, reset location and start over
		if (location >= images.length) {
			location = 0;
		}
		// set the imageview by the urls in images[]
		try {
			final Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(images[location]).getContent());
			Log.i("SlideShow", images[location]);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					imageView.setImageBitmap(bitmap);
				}
			});
			location++;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// overridden onBackPressed() function to return to MainActivity
	public void onBackPressed() {
		finish();
	}
	
	// cycleImages extends AsyncTask and is responsible for setting the images
	final class cycleImages extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
				setImage();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

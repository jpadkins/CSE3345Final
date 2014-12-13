package com.example.adkinscse3345final;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {

	// et is the EditText in MainActivity
	private EditText et;
	// bt is the button which creates a SlideShow intent
	private Button bt;
	// tv is the textview displaying the progress of the scrollbar
	private TextView tv;
	// sb is the seekbar that lets the user choose the number of pages
	private SeekBar sb;
	// tvTime is the textview displaying the progress of the scrollbar
	private TextView tvTime;
	// sbTime is the seekbar that lets the user choose the number of pages
	private SeekBar sbTime;
	// images is a List containing the urls of the images
	private List<String> images = new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// set handlers
		et = (EditText) findViewById(R.id.editText);
		bt = (Button) 	findViewById(R.id.button);
		tv = (TextView) findViewById(R.id.pageProgress);
		sb = (SeekBar)	findViewById(R.id.pages);
		tvTime = (TextView) findViewById(R.id.timeProgress);
		sbTime = (SeekBar)	findViewById(R.id.time);
		
		// set font color and size
		tv.setTextColor(Color.WHITE);
		tvTime.setTextColor(Color.WHITE);
		
		// set initial progress of the seekbars
		sb.setProgress(25);
		sbTime.setProgress(3);
		
		// set on click listener for button
		bt.setOnClickListener(this);
		
		// create listener for seekbars
		sb.setOnSeekBarChangeListener(this);
		sbTime.setOnSeekBarChangeListener(this);
	}
	
	// function for Button bt's onClickListener
	public void onClick(View v) {
		if (v.getId() == R.id.button) {
			// create callAPI object for networking
			callAPI a = new callAPI();
			// check if input is valid
			try {
				a.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			// reset edittext
			et.setText("");
			// prepare to go to Slide Show 
			if (images.size() > 0) {
				String[] imagesArray = images.toArray(new String[images.size()]);
				Intent intent = new Intent(this, SlideShow.class);
				intent.putExtra("duration", sbTime.getProgress()*1000);
				intent.putExtra("images", imagesArray);
				// call the intent
				startActivity(intent);
			}
			// else if the response was bad
			else {
				Toast.makeText(getApplicationContext(), "Something went wrong with the search, sorry!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// function to handle the Seekbar and displaying progress
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int limit = progress+1;
		if (seekBar.getId() == R.id.pages) {
			tv.setText("Number of posts to check for images: " + limit);
		}
		if (seekBar.getId() == R.id.time) {
			tvTime.setText("Number of seconds to display each image: " + limit);
		}
	}
	
	// returns a JSON string from a given url
	public String getStringFromUrl(String url) {
		String string;
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				while ((string = reader.readLine()) != null) {
					builder.append(string);
				}
			}
			else {
				Log.e("API", "error reading from file.");
			}
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	// calls getStringFromUrl based on contents of et and parses
	private void requestAndParse() {
		int limit = sb.getProgress()+1;
		String string = "http://www.reddit.com/r/" + et.getText().toString() + "/hot.json?sort=hot&limit=" + limit;
		// replace spaces with + for the HttpGet object
		string = string.replace(' ', '+');
		string = getStringFromUrl(string);
		// this is the default json response if the parameters were not correct
		if (string == "{\"kind\": \"Listing\", \"data\": {\"modhash\": \"\", \"children\": [], \"after\": null, \"before\": null}}" 
				|| string == "{\"error\": 404}") {
			Toast.makeText(getApplicationContext(), "Invalid Subreddit Name!", Toast.LENGTH_SHORT).show();
		}
		// else the parameters were correct and we have a good json response
		else {
			images.clear();
			Log.i("MainActivity", string);
			try {
				JSONArray jArray = new JSONObject(string).getJSONObject("data").getJSONArray("children");
				JSONObject jObject;
				// parse the individual image urls out of the JSONObject
				for (int i = 0; i < jArray.length(); i++) {
					jObject = jArray.getJSONObject(i).getJSONObject("data");
					string = jObject.optString("url");
					// check if it is a valid image url
					if (string.endsWith(".jpg") || string.endsWith(".png")) {
						images.add(string);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	// callAPI extends AsyncTask for performing networking off main thread
	final class callAPI extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			requestAndParse();
			return null;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
}

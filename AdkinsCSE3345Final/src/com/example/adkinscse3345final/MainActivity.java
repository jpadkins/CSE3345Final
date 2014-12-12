package com.example.adkinscse3345final;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends Activity {

	// et is the EditText in MainActivity
	EditText et;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		String string = "www.reddit.com/r/funny/hot.json?sort=hot";
	}
	
	
	// callAPI extends AsyncTask for performing networking off main thread
	final class callAPI extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
		return null;
		
		}
	}
}

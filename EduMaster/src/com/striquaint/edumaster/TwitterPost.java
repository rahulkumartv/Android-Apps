package com.striquaint.edumaster;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.striquaint.edumaster.TwitterApp.TwDialogListener;


public class TwitterPost extends Activity {
	private TwitterApp mTwitter;
	String wedstory,customstory;
	int selectedStroy;
	String error;
	private ProgressDialog mProgressDlg;
	private static final String twitter_consumer_key = "aCAnvMwszJy7iWy9yp5AfQ";//"7nHPgiPv3u1fyHCHkx7jg";//"tZwVDVvKmPrIfq5ECYoUgg";//"L7OlgUeTgBQMftaMazZUSg";
	private static final String twitter_secret_key ="Pt7YHNjyO1oerv0ujNwBLdATRSWxIKNCdsAZbLdDTQ";//"PgX0oJsQLfovknN8d60JsSnAdCjAfrw2mrf7yFIisY";//"xrmmA80dBE6FkHyG0tlNaT7dfKliLoV4f8xaCUFvFu4";//"blS4ktNjju0uFom3HdkynIOrtXrkHtt7nkzZD0uU"; 


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.twitterpost);

		mProgressDlg	= new ProgressDialog(this);
		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
		
		//Bundle data = getIntent().getExtras();
       // selectedStroy = data.getInt("selectedStory");
      //  wedstory = data.getString("story");
        Log.v("selectedStroy in TwitterPost",":"+selectedStroy);

        //SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
      //  String curentDateandTime = sdf.format(new Date());
        //customstory= getResources().getString(R.string.sharemsg )+curentDateandTime;//"<a href=\"http://itunes.apple.com/us/app/wedgie-dares/id500195407?ls=1&mt=8/\"> you can check out more at Wedgie Dares</a> "+""
		reviewEdit.setText(customstory);//Html.fromHtml()
		
		mTwitter 	= new TwitterApp(this, twitter_consumer_key,twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
		
		
		if(!mTwitter.hasAccessToken())
		{
			mTwitter.authorize();
		}
		
		((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if (mTwitter.hasAccessToken()) {
					//mTwitterBtn.setChecked(true);
					//String username = mTwitter.getUsername();
					//username		= (username.equals("")) ? "Unknown" : username;
					
					//mTwitterBtn.setText("  Twitter (" + username + ")");
					//mTwitterBtn.setTextColor(Color.WHITE);

					
					String tweet =reviewEdit.getText().toString();
					if(tweet.equalsIgnoreCase("")){
						Toast.makeText(TwitterPost.this, "Type some text to be post", Toast.LENGTH_SHORT).show();

					}if(!tweet.equalsIgnoreCase("")){
					//postTweet(tweet);
					postToTwitter(tweet);
					}
					
				}else if(!mTwitter.hasAccessToken())
				{
					mTwitter.authorize();
				}
			}
		});
		
		
		
	}
	
	private void postToTwitter(final String review) {
		mProgressDlg.setMessage("posting ...");
		mProgressDlg.show();
		new Thread() {
			@Override
			public void run() {
				int what = 0;
				
				try {
					mTwitter.updateStatus(review);
					//Toast.makeText(TwitterPost.this, "Review posted", Toast.LENGTH_SHORT).show();

					
				} catch (Exception e) {
					what = 1;
					Log.w("Exception","postToTwitter():"+e);
					//error = ""+e;
				}
				if(what==0)
				mHandler.sendMessage(mHandler.obtainMessage(what));
				
			}
		}.start();
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();
			String text = (msg.what == 0) ? "Posted to Twitter" : "Post to Twitter failed";
			/*if(selectedStroy!=402)
			{
				Intent i = new Intent(TwitterPost.this,StoryTabActivity.class);
				
				startActivity(i);
				finish();
			}
			else{*/
				Intent i = new Intent(TwitterPost.this,MainScreen.class);
				i.putExtra("selectedStory", selectedStroy);
				startActivity(i);
				//finish();
			//}
			Toast.makeText(TwitterPost.this, text, Toast.LENGTH_SHORT).show();
		}
	};

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		public void onComplete(String value) {
			String username = mTwitter.getUsername();
			username		= (username.equals("")) ? "No Name" : username;
		
			//mTwitterBtn.setText("  Twitter  (" + username + ")");
			//mTwitterBtn.setChecked(true);
			//mTwitterBtn.setTextColor(Color.WHITE);
			
			Toast.makeText(TwitterPost.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();
		}
		
		public void onError(String value) {
			//mTwitterBtn.setChecked(false);
			
			Toast.makeText(TwitterPost.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
}
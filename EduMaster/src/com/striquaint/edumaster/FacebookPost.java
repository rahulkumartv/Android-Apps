package com.striquaint.edumaster;

import java.io.ByteArrayOutputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;

public class FacebookPost extends Activity{
	private Facebook mFacebook;
	//private CheckBox mFacebookCb;
	private ProgressDialog mProgress;

	private Handler mRunOnUi = new Handler();
	//ImageView ratioimage;
	int selectedStroy;
	String wedstory,customstory,msg;
	
	private static final String APP_ID = "170039026462338";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fbpost);
		
		final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
		reviewEdit.setText(customstory);//Html.fromHtml()
		
		mProgress	= new ProgressDialog(this);
		
		mFacebook 	= new Facebook(APP_ID);
		//Bitmap bit = BitmapFactory.decodeFile("sdcard/screenshot1.png");
		//ratioimage.setImageBitmap(bit);
		SessionStore.restore(mFacebook, this);

		/*if (!mFacebook.isSessionValid()) {
			
			facebookLogin();
			
		}
		*/
		((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String review = reviewEdit.getText().toString();
				
				if (review.equals("")) return;
			
				if (mFacebook.isSessionValid()) {
				//if (mFacebookCb.isChecked())
					postToFacebook(review);
				}
				else if (!mFacebook.isSessionValid()) {
					facebookLogin();
				}
			}
		});
	}
	
	private void facebookLogin() {
		// TODO Auto-generated method stub
		
		if (mFacebook.isSessionValid()) {
			
		}else{
		mFacebook.authorize(this, PERMISSIONS, -1, new FbLoginDialogListener());
		
		}
		
		
		
		
	}
	 private final class FbLoginDialogListener implements DialogListener {
	        public void onComplete(Bundle values) {
	            SessionStore.save(mFacebook, FacebookPost.this);
	           
	           // mFacebookBtn.setText("  Facebook (No Name)");
	           // mFacebookBtn.setChecked(true);
				//mFacebookBtn.setTextColor(Color.WHITE);
				 
	            getFbName();
	        }

	        public void onFacebookError(FacebookError error) {
	           Toast.makeText(FacebookPost.this, "Facebook connection failed", Toast.LENGTH_SHORT).show();
	           
	           //mFacebookBtn.setChecked(false);
	        }
	        
	        public void onError(DialogError error) {
	        	Toast.makeText(FacebookPost.this, "Facebook connection failed", Toast.LENGTH_SHORT).show(); 
	        	
	        	//mFacebookBtn.setChecked(false);
	        }

	        public void onCancel() {
	        	//mFacebookBtn.setChecked(false);
	        }
	    }
	 
	 
		private Handler mFbHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mProgress.dismiss();
				
				if (msg.what == 0) {
					String username = (String) msg.obj;
			        username = (username.equals("")) ? "No Name" : username;
			            
			        SessionStore.saveName(username, FacebookPost.this);
			        
			       // mFacebookBtn.setText("  Facebook (" + username + ")");
			         
			        Toast.makeText(FacebookPost.this, "Connected to Facebook as " + username, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(FacebookPost.this, "Connected to Facebook", Toast.LENGTH_SHORT).show();
				}
			}
		};
	 
	 private void getFbName() {
			mProgress.setMessage("Finalizing ...");
			mProgress.show();
			
			new Thread() {
				@Override
				public void run() {
			        String name = "";
			        int what = 1;
			        
			        try {
			        	String me = mFacebook.request("me");
			        	
			        	JSONObject jsonObj = (JSONObject) new JSONTokener(me).nextValue();
			        	name = jsonObj.getString("name");
			        	what = 0;
			        } catch (Exception ex) {
			        	ex.printStackTrace();
			        }
			        
			        mFbHandler.sendMessage(mFbHandler.obtainMessage(what, name));
				}
			}.start();
		}

	 
	private void postToFacebook(String review) {	
		mProgress.setMessage("Posting ...");
		mProgress.show();
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		Bundle params = new Bundle();
		
		params.putString("name", "EduApp");
		params.putString("caption", "Eduapp Link");
		params.putString("description", review);
		params.putString("link", "Google Play Link");
		
		params.putString("picture", "EduAPpp Picture");
		
		mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
	}

	private final class WallPostListener extends BaseRequestListener {
        public void onComplete(final String response) {
        	mRunOnUi.post(new Runnable() {
        		public void run() {
        			mProgress.cancel();
        			
        			Toast.makeText(FacebookPost.this, "Posted to Facebook", Toast.LENGTH_SHORT).show();
        		/*	if(selectedStroy==402){
        			
        				Intent i = new Intent(FacebookPost.this,StoryTabActivity.class);
        				
        				startActivity(i);
        				finish();
        			}
        			else{*/
        				Intent i = new Intent(FacebookPost.this,MainScreen.class);
        				//i.putExtra("selectedStory", selectedStroy);
        				startActivity(i);
        				//finish();
        			//}
        		}
        	});
        }
    }
}
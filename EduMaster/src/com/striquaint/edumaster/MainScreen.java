package com.striquaint.edumaster;
import java.io.IOException;

import android.R.color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainScreen extends Activity {

	private static int ID_FACEBOOK = 0;
	private static int ID_TWITTER = 1;
	private static int ID_EMAIL = 2;
	private static int ID_MESSAGE = 3;
	private DBHelper m_DBhelper;
	Dialog SettingsDialog;
	static final int ID_SETTINGDIALOG = 1;
	private int QuesSection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		m_DBhelper = new DBHelper(this);
		try {
			m_DBhelper.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_DBhelper.openDataBase();
		//Intent QuizApp =  new Intent( MainScreen.this, QuizActivity.class);
		//startActivity(QuizApp);
		TextView ShareView = (TextView) findViewById(R.id.ShareView);
		ShareView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Item[] items = {
					    new Item("Facebook", R.drawable.fb_icon),
					    new Item("Twitter", R.drawable.twiiter_icon),
					    new Item("Email", R.drawable.email_icon),
					    new Item("Message", R.drawable.message_icon)
					};
				ListAdapter adapter = new ArrayAdapter<Item>( MainScreen.this, android.R.layout.select_dialog_item,
					    android.R.id.text1,
					    items)
					    {
					        public View getView(int position, View convertView, ViewGroup parent) {
					            //User super class to create the View
					            View v = super.getView(position, convertView, parent);
					            TextView tv = (TextView)v.findViewById(android.R.id.text1);

					            //Put the image on the TextView
					            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

					            //Add margin between image and text (support various screen densities)
					            int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
					            tv.setCompoundDrawablePadding(dp5);

					            return v;
					        }
					    };
				final AlertDialog.Builder ShareDialog = new AlertDialog.Builder(MainScreen.this);
				ShareDialog.setTitle("Share Via");
				ShareDialog.setAdapter(adapter, null);
				final AlertDialog dialog = ShareDialog.create();
				dialog.getListView().setOnItemClickListener( new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int nPos, long arg3) {
						dialog.dismiss();
						if( ID_FACEBOOK == nPos)
						{
							if( isOnline() )
							{
								Intent i = new Intent(MainScreen.this,FacebookPost.class);
								//i.putExtra("selectedStory", selectedStroy);
								//i.putExtra("story",story.getText().toString());
								startActivity(i);
								finish();
							}
							else
							{
								
							}
						}
						if( ID_TWITTER == nPos)
						{
							if( isOnline() )
							{
								Intent i = new Intent(MainScreen.this,TwitterPost.class);
								startActivity(i);
								finish();
							}
							else
							{
								
							}
						}
						if( ID_EMAIL == nPos)
						{
							if( isOnline() )
							{
								Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
								emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
								emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Education App Reviews");
								emailIntent.putExtra(Intent.EXTRA_TEXT   , "Hi Downlaod Edu App from Following Link");
								emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								emailIntent.setType("message/rfc822");
								startActivity(Intent.createChooser( emailIntent, "Share Review"));

							}
							else
							{
								
							}
						}
						if( ID_MESSAGE == nPos)
						{
							Intent sendIntent = new Intent(Intent.ACTION_VIEW);
							sendIntent.putExtra("sms_body", "Hai Test Message "); 
							sendIntent.setType("vnd.android-dir/mms-sms");
							startActivity(sendIntent);
						}
					}
				});
				dialog.show();
				
				
			}
		});
		TextView QuestionView = (TextView) findViewById(R.id.Textview1);
		QuestionView.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Cursor cursor = m_DBhelper.QueryRawSQL( "SELECT DISTINCT section_name from question_table");
				AlertDialog.Builder QuestionSelect = new AlertDialog.Builder(MainScreen.this);
				String[] ListItems  = new String[cursor.getCount()];
				if( cursor.moveToFirst())
		        {
		        	int nIndex =0;
		        	do{
		        		ListItems[ nIndex ] = cursor.getString(0);
		        		nIndex++;
		            }while( cursor.moveToNext());
		        }
				cursor.close();
				QuestionSelect.setTitle("Choose Section");
				QuestionSelect.setAdapter(new ArrayAdapter<String>(MainScreen.this, android.R.layout.simple_list_item_1, ListItems), new DialogInterface.OnClickListener(
						) {
					
					@Override
					public void onClick(DialogInterface dialog, int nPos ) {
						// TODO Auto-generated method stub
						QuesSection  = nPos;
						showDialog(ID_SETTINGDIALOG);
						//TestSett.show();
					}
				});
				QuestionSelect.show();
			}	
		});
		
	}
	@Override
	protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //m_DBhelper.close();
	}
	@Override
	protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
      //  m_DBhelper.close();
	}
	 private boolean isOnline() {
		 ConnectivityManager cm =   (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		 NetworkInfo netInfo = cm.getActiveNetworkInfo();
		 if (netInfo != null && netInfo.isConnected()) {
	            return true;
	        }
	        return false;
	    }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_screen, menu);
		return true;
	}
	public static class Item{
	    public final String text;
	    public final int icon;
	    public Item(String text, Integer icon) {
	        this.text = text;
	        this.icon = icon;
	    }
	    @Override
	    public String toString() {
	        return text;
	    }
	}
	@Override
	protected Dialog onCreateDialog(int id) {
	 // TODO Auto-generated method stub
	 
	 SettingsDialog = null;
	 switch(id){
	 case(ID_SETTINGDIALOG):
	 SettingsDialog = new Dialog(this);
	 SettingsDialog.setContentView(R.layout.testsettings);
	 SettingsDialog.getWindow().setBackgroundDrawableResource( color.background_light );
	  Button Ok= (Button)SettingsDialog.findViewById(R.id.OkButton);
	  Ok.setOnClickListener(btnScreenDialog_OKOnClickListener);
	 }
	 return SettingsDialog;
	}
	 
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	 // TODO Auto-generated method stub
	 switch(id){
	 case(ID_SETTINGDIALOG):
	  dialog.setTitle("Customize Test Papper");
	  break;
	 }
	}
	 
	private Button.OnClickListener btnScreenDialog_OKOnClickListener
	 = new Button.OnClickListener(){
	 
	 @Override
	 public void onClick(View arg0) {
	  // TODO Auto-generated method stub
		 SettingsDialog.dismiss();
		 Spinner spinner= (Spinner)SettingsDialog.findViewById(R.id.TimerSpinner);
		 String strTemp = spinner.getSelectedItem().toString();
		 strTemp =  strTemp.substring(0, (strTemp.length() - 4));
		 Intent Quiz =  new Intent( MainScreen.this , QuizActivity.class);
		 Quiz.putExtra("TIMERVALUE", strTemp);
		 spinner= (Spinner)SettingsDialog.findViewById(R.id.QuesCntSpinner);
		 strTemp = spinner.getSelectedItem().toString();
		 Quiz.putExtra("QUESTCNT", strTemp);
		 Quiz.putExtra("QUESTSECTION",  Integer.toString(QuesSection));
		 //m_DBhelper.close();
		 finish();
		 startActivity(Quiz);
		 
	 }};
}

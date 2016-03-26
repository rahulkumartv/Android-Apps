package com.striquaint.pincodeassist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.R.string;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Button;

public class DownLoadManager {
	private DownloadHelper m_DownloadHelper;
	private String m_urlForAllPackage = "https://dl.dropbox.com/s/sawrdokkbjjlikk/PINCodesExcelFormat.zip";
	public DownLoadManager( Context DownLoadActivity, boolean bFullPackage)
	{
		
		if( bFullPackage )
		{
			 m_DownloadHelper =  new DownloadHelper( DownLoadActivity);
			 m_DownloadHelper.execute(m_urlForAllPackage);
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(DownLoadActivity.getApplicationContext());
			builder.setTitle("Customize package");
			DataBaseHelper DBHelper = new DataBaseHelper( DownLoadActivity.getApplicationContext());
			try {
	        	DBHelper.createDataBase();
	        	}
	        catch (IOException ioe) {
	        	throw new Error("Unable to create database");
	        	}
			try {
	        	DBHelper.openDataBase();
	        	}
	        catch(SQLException sqle){
	        	throw sqle;
	        	}
	        Cursor cursor = DBHelper.QueryRawSQL( "SELECT DISTINCT state_name from state_table");
	        int nCount = cursor.getCount();
	        
	        String[] ListItems = new String[nCount];
	        final Boolean[] stateChecked = new Boolean[nCount];
	        if( cursor.moveToFirst())
	        {
	        	int nIndex =0;
	        	do{	
	        		ListItems[ nIndex ] = cursor.getString(0);
	        		nIndex++;
	            }while( cursor.moveToNext());
	        }
	        cursor.close();
	        DBHelper.close();
			builder.setMultiChoiceItems(ListItems, null, new OnMultiChoiceClickListener(){

				@Override
				public void onClick(DialogInterface dialog,
						int which, boolean isChecked) {
					// TODO Auto-generated method stub
					stateChecked[which]=isChecked;
					
				}});
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}});
			AlertDialog alert = builder.create();
			alert.show();

		}
	}
public class DownloadHelper extends AsyncTask<String, String, Void> 
{
	private ProgressDialog m_Progressdialog;
	private Boolean m_bNotifcation;
	private Notification m_Notification;
	NotificationManager m_NotificationManager;
	private Context m_DownLoadActivity;
	private int NOTIFICATION_ID = 1;
	private PendingIntent m_ContentIntent;
	public DownloadHelper( Context DownLoadActivity) 
	{		
		this.m_DownLoadActivity = DownLoadActivity;
		m_bNotifcation = false;
		CreateProgressBar();
	}
	protected void onPreExecute()
	{
		m_Progressdialog.show();	
	}	
	protected Void doInBackground(String... myurl) 
	{		
		  try {
			  for( int i = 0; i< myurl.length; i++)
			  {
				  int nPackage = i+1;
				  String strMessage = "Downloading Pincode Package " + nPackage + "of" + myurl.length;
				  m_Progressdialog.setMessage(strMessage);
				  URL downloadUrl  = new URL(myurl[i]);
				  File filePath = new File(""+downloadUrl);
				  String zipname = filePath.getName();
				  URLConnection urlConction = downloadUrl.openConnection();
				  urlConction.connect();
				  
				  int lenghtOfFile = urlConction.getContentLength();
				  InputStream downloadStream = downloadUrl.openStream();
				  File downloadDir = new File(Environment.getExternalStorageDirectory()+"/download/");
				  if(!downloadDir.exists()){
					  downloadDir.mkdir();
					  }
				  FileOutputStream zipStream = new FileOutputStream(downloadDir+"/"+zipname);
				  byte streamData[] = new byte[1024];
				  int streamCount = 0;
				  int totalCount = 0;
				  while (( streamCount = downloadStream.read( streamData )) != -1)
				  {
					  totalCount += streamCount;
					  zipStream.write( streamData , 0, streamCount );
					  onProgressUpdate((int)((totalCount*100)/lenghtOfFile));
					  }
				  downloadStream.close();
				  zipStream.close();
			  }
			  } catch (MalformedURLException e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
				  } catch (FileNotFoundException e) {
					  // TODO Auto-generated catch block
					  e.printStackTrace();
					  } catch (IOException e) {
						  // TODO Auto-generated catch block
						  e.printStackTrace();
						  }
		  return null;
    }
	private void CreateProgressBar(){
		m_Progressdialog=new ProgressDialog(m_DownLoadActivity);
		m_Progressdialog.setCancelable(false);
		m_Progressdialog.setMessage("downloading Starting");
		m_Progressdialog.setMax(100);
		m_Progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_Progressdialog.setButton( DialogInterface.BUTTON_POSITIVE, "DownLoad in Background", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				m_bNotifcation = true;
				CreateNotification();
				m_Progressdialog.dismiss();
			}});
	}
	private void CreateNotification() {
        //get the notification manager
		m_NotificationManager = (NotificationManager) m_DownLoadActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        //create the notification
        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = "Downloading Pincode";
        long when = System.currentTimeMillis();
        m_Notification = new Notification(icon, tickerText, when);

        //create the content which is shown in the notification pulldown
        String mContentTitle = "Pincode Downloader"; //Full title of the notification in the pull down
        CharSequence contentText = m_Progressdialog.getProgress() + "% complete"; //Text of the notification in the pull down

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent();
        m_ContentIntent = PendingIntent.getActivity(m_DownLoadActivity, 0, notificationIntent, 0);

        //add the additional content and intent to the notification
        m_Notification.setLatestEventInfo(m_DownLoadActivity, mContentTitle, contentText, m_ContentIntent);

        //make this notification appear in the 'Ongoing events' section
        m_Notification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
        m_NotificationManager.notify(NOTIFICATION_ID, m_Notification);
    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    public void progressUpdate(int percentageComplete) {
        //build up the new status message
        CharSequence contentText = percentageComplete + "% complete";
        CharSequence contentTitle = "Pincode Downloader";
        //publish it to the status bar
        m_Notification.setLatestEventInfo(m_DownLoadActivity, contentTitle, contentText, m_ContentIntent);
        m_NotificationManager.notify(NOTIFICATION_ID, m_Notification);
    }

	protected void onPostExecute(Void result){
		m_Progressdialog.dismiss();
		m_NotificationManager.cancel(NOTIFICATION_ID);
		
	}
	protected void onProgressUpdate( int progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
		if( m_bNotifcation){
			progressUpdate(progress);
		}
		else{
			m_Progressdialog.setProgress(progress);
		}
    }
}

}


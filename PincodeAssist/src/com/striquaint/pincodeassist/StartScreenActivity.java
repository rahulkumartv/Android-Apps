package com.striquaint.pincodeassist;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.string;
import android.animation.AnimatorSet.Builder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class StartScreenActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Button btnPnSearch =  (Button) findViewById(R.id.button1);
        btnPnSearch.setOnClickListener( new OnClickListener() {
			
        	//START - handling search pincode button clicks
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] strListValues = new String[]{
						"Search by Pincode",
						"Search by Area"
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setTitle("Select Search Option");
				builder.setSingleChoiceItems(strListValues, -1, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	dialog.dismiss();
				    	if( item == 0 )
						{
							Intent SrchbyPncd =  new Intent( StartScreenActivity.this , SearchByPincode.class);
							startActivity( SrchbyPncd );
						}
						else
						{
							Intent Srchbyarea =  new Intent( StartScreenActivity.this , SearchByArea.class);
							startActivity( Srchbyarea );
						}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		} );
      //END - handling search pincode button clicks
        Button mailButton = (Button) findViewById( R.id.button3);
        mailButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( false == isOnline())
				{
					if( false == isOnline())
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						builder.setTitle("Network Eror");
						builder.setMessage("Not Connected");
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});
						AlertDialog alert = builder.create();
						alert.show();
						return;
					}
					return;
				}
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"STRIQUAINTSOLUTIONS@gmail.com"});
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pincode Assist Feedback");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT   , "...write your feedback/complaint........");
				emailIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
				emailIntent.setType("message/rfc822");//text/plain
				try {
				    startActivity(Intent.createChooser( emailIntent, "Send Feedback of Pincode Assist "));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(StartScreenActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}	
			}
		});
        Button OrderBtton = (Button)findViewById(R.id.button2);
        OrderBtton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( false == isOnline())
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle("Network Eror");
					builder.setMessage("Not Connected");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					return;
				}
				String[] strOrderPackages = new String[]{
						"Full Indian Pincode Package for Rs.1500",
						"Order state wise pincode package( each Rs.100)"
						};
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setTitle("Select Your Packge");
				builder.setSingleChoiceItems(strOrderPackages, -1, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	
				    	dialog.dismiss();
				    	if( item == 0 )
						{
				    		new DownLoadManager( StartScreenActivity.this, true);
						}
				    	else
				    	{
				           new DownLoadManager( StartScreenActivity.this, false);
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				}
		});
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
        ///getMenuInflater().inflate(R.menu.activity_start_screen, menu);
    	MenuInflater inflater = getMenuInflater();    	
        inflater.inflate(R.menu.activity_start_screen, menu);	
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()) 
    	{
    	case R.id.Help:
    		return true;
    	case R.id.info:
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    		}
    	}
}

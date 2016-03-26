package com.striquaint.pincodeassist;
import static com.striquaint.pincodeassist.Constants.FIRST_COLUMN;
import static com.striquaint.pincodeassist.Constants.SECOND_COLUMN;
import static com.striquaint.pincodeassist.Constants.THIRD_COLUMN;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class SearchByArea extends Activity {
	private String m_selDistText;
	private String m_selStateID;
	private String m_selDistID;
	private String m_selStateText;
	private String m_SelPstoffText;
	private String m_outPincode;
	Button m_stateButton;
	Button m_districtButton;
	Button m_postoffButton;
	String[] m_ListItems;
	ArrayAdapter<String> m_adapter;
	private DataBaseHelper m_DBHelper = new DataBaseHelper( this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_by_area);
		try {
        	m_DBHelper.createDataBase();
        	}
        catch (IOException ioe) {
        	throw new Error("Unable to create database");
        	}
        try {
        	m_DBHelper.openDataBase();
        	}
        catch(SQLException sqle){
        	throw sqle;
        	}
        m_stateButton = (Button) findViewById(R.id.button1);
        m_stateButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub 
				Cursor cursor = m_DBHelper.QueryRawSQL( "SELECT DISTINCT state_name from state_table");
		        int nCount = cursor.getCount();
		        
		        m_ListItems = new String[nCount];
		        if( cursor.moveToFirst())
		        {
		        	int nIndex =0;
		        	do{	
		        		m_ListItems[ nIndex ] = cursor.getString(0);
		        		nIndex++;
		            }while( cursor.moveToNext());
		        }
		        cursor.close();
		        Bundle bundle = new Bundle();
		        bundle.putStringArray(FIRST_COLUMN, m_ListItems);
		        Intent SrchbyPncd =  new Intent( SearchByArea.this , AlphabeticSectionListView.class);
		        SrchbyPncd.putExtra("key", FIRST_COLUMN);
		        SrchbyPncd.putExtras(bundle);
		    	startActivityForResult(SrchbyPncd, 100);
				
			}
		});
        m_districtButton = ( Button ) findViewById(R.id.button2);
        m_districtButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strQuery = "SELECT district_name from district_table WHERE state_id='";
		    	strQuery += m_selStateID + "'";
		    	Cursor cursor = m_DBHelper.QueryRawSQL( strQuery );
				int nCount = cursor.getCount();
				if( nCount == 0 )
				{
					Toast.makeText(getApplicationContext(), "Please Select State", Toast.LENGTH_SHORT).show();
				}
				else
				{
					m_ListItems = new String[nCount];
			        if( cursor.moveToFirst())
			        {
			        	int nIndex =0;
			        	do{
			        		m_ListItems[ nIndex ] = cursor.getString(0);
			        		nIndex++;
			            }while( cursor.moveToNext());
			        }
			        cursor.close();
			        Bundle bundle = new Bundle();
			        bundle.putStringArray(SECOND_COLUMN, m_ListItems);
			        Intent SrchbyPncd =  new Intent( SearchByArea.this , AlphabeticSectionListView.class);
			        SrchbyPncd.putExtra("key", SECOND_COLUMN);
			        SrchbyPncd.putExtras(bundle);
			    	startActivityForResult(SrchbyPncd, 101);
				}
			}
		});
        m_postoffButton = ( Button ) findViewById(R.id.button3);
        m_postoffButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strQuery = "SELECT postoffice from pincode_table WHERE state_id='";
		    	strQuery += m_selStateID + "' AND district_id='" + m_selDistID + "'" ;
		    	Cursor cursor = m_DBHelper.QueryRawSQL( strQuery );
				int nCount = cursor.getCount();
				if( nCount == 0 )
				{
					Toast.makeText(getApplicationContext(), "Please Select District", Toast.LENGTH_SHORT).show();
				}
				else
				{
					m_ListItems = new String[nCount];
			        if( cursor.moveToFirst())
			        {
			        	int nIndex =0;
			        	do{
			        		m_ListItems[ nIndex ] = cursor.getString(0);
			        		nIndex++;
			            }while( cursor.moveToNext());
			        }
			        cursor.close();
			        Bundle bundle = new Bundle();
			        bundle.putStringArray(THIRD_COLUMN, m_ListItems);
			        Intent SrchbyPncd =  new Intent( SearchByArea.this , AlphabeticSectionListView.class);
			        SrchbyPncd.putExtra("key", THIRD_COLUMN);
			        SrchbyPncd.putExtras(bundle);
			    	startActivityForResult(SrchbyPncd, 102);
				}
			}
		});
        Button searchBtn = (Button) findViewById( R.id.button4);
        searchBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setTitle("Pincode of "+ m_SelPstoffText +" is");
				builder.setMessage(m_outPincode);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
        
   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_by_area, menu);
		return true;
	}
	@Override
	protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        m_DBHelper.close();
       }
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String strCode = Integer.toString(requestCode);
        if( data == null)
        {
        	return;
        }
        if( strCode.equals("100")){
 
             // Storing result in a variable called myvar
             // get("website") 'website' is the key value result dat
        	m_selStateText  = data.getExtras().getString( FIRST_COLUMN );
             m_stateButton.setText( m_selStateText );
             String strQuery = "SELECT state_ID from state_table WHERE state_name='";
             strQuery += m_selStateText + "'";
		     Cursor cursor = m_DBHelper.QueryRawSQL( strQuery );
		     cursor.moveToFirst();
		     m_selStateID = cursor.getString(0);
		     cursor.close();
             
        }
        if( strCode.equals("101")){
        	 
            // Storing result in a variable called myvar
            // get("website") 'website' is the key value result dat
            m_selDistText = data.getExtras().getString(SECOND_COLUMN);
            m_districtButton.setText( m_selDistText );
            String strQuery = "SELECT district_ID from district_table WHERE district_name='";
	    	strQuery += m_selDistText + "' AND state_id='" + m_selStateID + "'";
	    	Cursor cursor = m_DBHelper.QueryRawSQL( strQuery );
	    	cursor.moveToFirst();
	    	m_selDistID = cursor.getString(0);
	    	cursor.close();            
       }
        if( strCode.equals("102")){
       	 
            m_SelPstoffText = data.getExtras().getString(THIRD_COLUMN);
            m_postoffButton.setText( m_SelPstoffText );
            String strQuery = "SELECT pincode from pincode_table WHERE state_id='";
	    	strQuery += m_selStateID + "' AND district_id='" + m_selDistID + "' AND postoffice='" + m_SelPstoffText +"'";
	    	Cursor cursor = m_DBHelper.QueryRawSQL( strQuery );
	    	cursor.moveToFirst();
	    	m_outPincode = cursor.getString(0);
	    	cursor.close();           
       }
 
    }
}

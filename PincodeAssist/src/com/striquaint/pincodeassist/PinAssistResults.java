package com.striquaint.pincodeassist;

import static com.striquaint.pincodeassist.Constants.FIRST_COLUMN;
import static com.striquaint.pincodeassist.Constants.SECOND_COLUMN;
import static com.striquaint.pincodeassist.Constants.THIRD_COLUMN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class PinAssistResults extends Activity {

	private ArrayList<HashMap<String,String>> list;
	private DataBaseHelper m_DBHelper = new DataBaseHelper( this);
	String m_strPincode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin_assist_results);
		m_strPincode = getIntent().getExtras().getString("SEARCHBYPINCODE");
		TextView strSrchHeader =  (TextView) findViewById(R.id.SearchHeader);
		strSrchHeader.setText( "Search Results for Pincode:-" + m_strPincode );
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
        populateList();
        ListView lview = (ListView) findViewById(R.id.ResultListView);
        listviewAdapter adapter = new listviewAdapter(this, list);
        lview.setAdapter(adapter);
	}

	private void populateList() {
		// TODO Auto-generated method stub
		list = new ArrayList<HashMap<String,String>>();
		String strQuery = "SELECT state_id,district_id,postoffice FROM pincode_table where pincode='" + m_strPincode + "'";
		Cursor cursor = m_DBHelper.QueryRawSQL( strQuery);
		int nCount = cursor.getCount();
		cursor.moveToFirst();
	    String strTemp;
        for( int nIdx = 0; nIdx< nCount; nIdx++ )
	    {
	        	HashMap<String,String> temp = new HashMap<String,String>();
	        	strTemp = cursor.getString(0);
	            Cursor stateCrsor = m_DBHelper.QueryRawSQL("SELECT state_name from state_table where state_id='" + strTemp +"'" );
	            stateCrsor.moveToFirst();
	            temp.put(FIRST_COLUMN,stateCrsor.getString(0) );
	            stateCrsor.close();
	            strTemp = cursor.getString(1);
	            Cursor distCrsor = m_DBHelper.QueryRawSQL("SELECT district_name from district_table where district_id='" + strTemp +"'" );
	            distCrsor.moveToFirst();
	            temp.put(SECOND_COLUMN,distCrsor.getString(0) );
	            distCrsor.close();
	            temp.put(THIRD_COLUMN, cursor.getString(2));
	            list.add(temp);
	            cursor.moveToNext();
	            }
	        cursor.close();
	}
	@Override
    protected void onDestroy() {
     // TODO Auto-generated method stub
     super.onDestroy();
     m_DBHelper.close();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pin_assist_results, menu);
		return true;
	}

}

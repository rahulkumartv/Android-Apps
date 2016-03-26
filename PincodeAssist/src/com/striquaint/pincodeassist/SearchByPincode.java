package com.striquaint.pincodeassist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SearchByPincode extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_by_pincode);
		Button btnGo = (Button)findViewById(R.id.ButtonGo);
		//Start Button Go clicks handled
		btnGo.setOnClickListener( new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText PncodeText = (EditText) findViewById(R.id.PincodeEditText);
				String strText = PncodeText.getText().toString();
				Intent searchListIntent =  new Intent( SearchByPincode.this , PinAssistResults.class);
				searchListIntent.putExtra( "SEARCHBYPINCODE", strText);
				startActivity( searchListIntent);
			}
		});
		//End Button Go clicks handled
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_by_pincode, menu);
		return true;
	}

}

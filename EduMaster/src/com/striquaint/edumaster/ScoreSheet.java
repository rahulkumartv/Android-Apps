package com.striquaint.edumaster;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class ScoreSheet extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scoresheet);
		String strText;
		TextView textview;
		strText = getIntent().getExtras().getString("TotalCount");
		textview = (TextView)findViewById(R.id.TotalCount);
		textview.setText(strText);
		strText = getIntent().getExtras().getString("CorrectAns");
		textview = (TextView)findViewById(R.id.CorrectCount);
		textview.setText(strText);
		strText = getIntent().getExtras().getString("WrongAns");
		textview = (TextView)findViewById(R.id.WrongCount);
		textview.setText(strText);
		strText = getIntent().getExtras().getString("UnAnswQues");
		textview = (TextView)findViewById(R.id.UnAnsweredCount);
		textview.setText(strText);
	}
}

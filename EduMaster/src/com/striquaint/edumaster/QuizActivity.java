package com.striquaint.edumaster;


import java.io.IOException;
import java.util.Random;

import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class QuizActivity  extends Activity implements View.OnTouchListener, OnClickListener{

	private DBHelper m_DBhelper;
	private int m_QuesIdMax = 1003;
	private int m_QuesIdMin = 1000;
	private int m_QuestId;
	private int m_QuesCount = 0;
	private int m_TotalQuestionCnt;
	private int m_startQuesId;
	private int m_nSectionID;
	private int m_nTimer = 20;
	Random random = new Random(); 
	private ClockButton m_TimerAnimation;
	private GestureDetector m_gestureDetector;
	//private FlipViewController flipView;
	private boolean m_bTimerRunning = false;
	private CountDownTimer m_ClockTimer;
	private RadioButton m_r1;
	private RadioButton m_r2;
	private RadioButton m_r3;
	private RadioButton m_r4;
	private RadioButton m_r5;
	private RadioButton m_BlinkRadio;
	private int m_nResultAnsCnt = 0;
	private int m_nResultWrongAns = 0;
	private int m_nResultUnAnsQues = 0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_DBhelper = new DBHelper(this);
		m_QuestId = random.nextInt( m_QuesIdMax - m_QuesIdMin + 1) + m_QuesIdMin;
		m_startQuesId = m_QuestId;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String value = extras.getString("QUESTSECTION");
		    m_nSectionID = Integer.parseInt(value);
		    value = extras.getString("QUESTCNT");
		    m_TotalQuestionCnt = Integer.parseInt(value);
		    value = extras.getString("TIMERVALUE");
		    m_nTimer = Integer.parseInt(value);
		}
		else
		{
			return;
		}
		setContentView(R.layout.question_page);
		m_TimerAnimation  = (ClockButton) findViewById(R.id.TimerAnimation);
		m_TimerAnimation.getBackground().setAlpha(0);
		m_TimerAnimation.SetTimerMax(m_nTimer);
		m_gestureDetector = new GestureDetector(this, new MyGestureDetector(this));
		try {
			m_DBhelper.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_DBhelper.openDataBase();
		
		


		RadioGroup rg = (RadioGroup)findViewById(R.id.RadioGroup);
		rg.setScrollContainer(true);
		//flipView = new FlipViewController(this, false);

		//flipView.setAdapter(new MyBaseAdapter(this));

		//setContentView(flipView);
		
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//	WindowManager.LayoutParams.FLAG_FULLSCREEN);
		DisplayQuestions();
		ImageButton btnNext = (ImageButton) findViewById(R.id.Next);
		btnNext.setOnClickListener( new OnClickListener(){
			 public void onClick(View v)
			 {
				 SwipeRight();
			 }
		});
	/*	LinearLayout linearlayout = (LinearLayout)findViewById(R.id.mainlayout);
		linearlayout.setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				m_gestureDetector.onTouchEvent(event);
				return true;
			}
		});*/
		ScrollView scrollView = (ScrollView)findViewById(R.id.ScrolView);
		scrollView.setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				m_gestureDetector.onTouchEvent(event);
				return true;
			}
		});
		m_r1 = (RadioButton) findViewById(R.id.Answer_First);
        m_r2 = (RadioButton) findViewById(R.id.Answer_Second);
        m_r3 = (RadioButton) findViewById(R.id.Answer_Third);
        m_r4 = (RadioButton) findViewById(R.id.Answer_Fourth);
        m_r5 = (RadioButton) findViewById(R.id.Answer_Fifth);
        m_r1.setOnClickListener( this);
        m_r2.setOnClickListener( this);
        m_r3.setOnClickListener( this);
        m_r4.setOnClickListener( this);
        m_r5.setOnClickListener(this);
	}
	
	private void DisplayQuestions(  ) {
		// TODO Auto-generated method stub
		ScrollView layout = (ScrollView)findViewById(R.id.ScrolView);
		layout.scrollTo(0,5);
		if( m_QuesCount > m_QuesIdMax - m_QuesIdMin )
		{
			Intent ScoreActivity =  new Intent( QuizActivity.this, ScoreSheet.class);
			ScoreActivity.putExtra("TotalCount", Integer.toString(m_QuesCount));
			ScoreActivity.putExtra("CorrectAns", Integer.toString(m_nResultAnsCnt));
			ScoreActivity.putExtra("WrongAns", Integer.toString(m_nResultWrongAns));
			ScoreActivity.putExtra("UnAnswQues", Integer.toString(m_nResultUnAnsQues));
			finish();
			startActivity( ScoreActivity );
		}
		else
		{
			if( m_QuestId > m_QuesIdMax )
			{
			 m_QuestId = m_QuesIdMin;
			 }	
			Cursor cursor = m_DBhelper.QueryRawSQL("SELECT question_text FROM question_table WHERE question_id="+m_QuestId);
			 if( cursor.moveToFirst())
			 {
				 String strText = cursor.getString(0);
				 TextView QuestionText = (TextView)findViewById(R.id.Question__Text);
				 QuestionText.setText(strText);
			 }
			 cursor.close();
			 cursor = m_DBhelper.QueryRawSQL("SELECT * FROM answers_table where question_id=" + m_QuestId );
			 if( cursor.moveToFirst())
			 {
				 ScrollView scrollView = (ScrollView)findViewById(R.id.ScrolView);
				 int nColor = scrollView.getSolidColor();
				 String strText = cursor.getString(2);
				 RadioButton AnswerButton = (RadioButton)findViewById(R.id.Answer_First);
				 if( strText.equals("") )
				 {
					 AnswerButton.setVisibility(View.GONE);
				 }
				 else
				 {
					 AnswerButton.setVisibility(View.VISIBLE);
					 AnswerButton.setChecked(false);
					 AnswerButton.setBackgroundColor( nColor );
					 AnswerButton.setClickable(true);
					 AnswerButton.setText(strText);
				 }
				 strText = cursor.getString(3);
				 AnswerButton = (RadioButton)findViewById(R.id.Answer_Second);
				 if( strText.equals("") )
				 {
					 AnswerButton.setVisibility(View.GONE);
				 }
				 else
				 {
					 AnswerButton.setVisibility(View.VISIBLE);
					 AnswerButton.setChecked(false);
					 AnswerButton.setBackgroundColor( nColor );
					 AnswerButton.setClickable(true);
					 AnswerButton.setText(strText);
				 }
				 strText = cursor.getString(4);
				 AnswerButton = (RadioButton)findViewById(R.id.Answer_Third);
				 if( strText.equals("") )
				 {
					 AnswerButton.setVisibility(View.GONE);
				 }
				 else
				 {
					 AnswerButton.setVisibility(View.VISIBLE);
					 AnswerButton.setChecked(false);
					 AnswerButton.setBackgroundColor( nColor );
					 AnswerButton.setClickable(true);
					 AnswerButton.setText(strText);
				 }
				 strText = cursor.getString(5);
				 AnswerButton = (RadioButton)findViewById(R.id.Answer_Fourth);
				 if( strText.equals("") )
				 {
					 AnswerButton.setVisibility(View.GONE);
					 
				 }
				 else
				 {
					 AnswerButton.setVisibility(View.VISIBLE);
					 AnswerButton.setChecked(false);
					 AnswerButton.setBackgroundColor( nColor );
					 AnswerButton.setClickable(true);
					 AnswerButton.setText(strText);
				 }
				 strText = cursor.getString(6);
				 AnswerButton = (RadioButton)findViewById(R.id.Answer_Fifth);
				 if( strText.equals("") )
				 {
					 AnswerButton.setVisibility(View.GONE);
				 }
				 else
				 {
					 AnswerButton.setVisibility(View.VISIBLE);
					 AnswerButton.setChecked(false);
					 AnswerButton.setBackgroundColor( nColor );
					 AnswerButton.setClickable(true);
					 AnswerButton.setText(strText);
				 }
			 }
			 cursor.close();
			 m_bTimerRunning = true;
			 m_ClockTimer = new CountDownTimer( m_nTimer*1000, 1000) { 
			        public void onTick(long millisUntilFinished) {    
			        	m_TimerAnimation.NotifyDraw();
			        }            
			        public void onFinish() {
			        	m_bTimerRunning = false;
			        	m_TimerAnimation.NotifyDraw();
			        	BlinkButton();
			        	if( !m_r1.isChecked() && !m_r2.isChecked() && !m_r3.isChecked()  && !m_r4.isChecked() && !m_r5.isChecked() )
			        	{
			        		Cursor cursor = m_DBhelper.QueryRawSQL("SELECT answer FROM answers_table where question_id=" + m_QuestId );
			        		int nAnswer = 0;
			        		if( cursor.moveToFirst())
			        		{
			        			String strText = cursor.getString(0);
			        			nAnswer = Integer.parseInt(strText);
			        		}
			        		cursor.close();
			        		if( 1 == nAnswer )
			        		{  
			        			m_r1.setChecked(true);
			        			m_r1.setBackgroundResource( R.drawable.button_highlight_true  );
			        			m_BlinkRadio = m_r1;
			        		}
			        		else if( 2 == nAnswer )
			        		{  
			        			m_r2.setChecked(true);
			        			m_r2.setBackgroundResource( R.drawable.button_highlight_true  );
			        			m_BlinkRadio = m_r2; 
			        		}
			        		else if( 3 == nAnswer )
			        		{  
			        			m_r3.setChecked(true);
			        			m_r3.setBackgroundResource( R.drawable.button_highlight_true  );
			        			m_BlinkRadio = m_r3; 
			        		}
			        		else if( 4 == nAnswer )
			        		{  
			        			m_r4.setChecked(true);
			        			m_r4.setBackgroundResource( R.drawable.button_highlight_true  );
			        			m_BlinkRadio = m_r4; 
			        		}
			        		else if( 5 == nAnswer )
			        		{  
			        			m_r5.setChecked(true);
			        			m_r5.setBackgroundResource( R.drawable.button_highlight_true  );
			        			m_BlinkRadio = m_r5; 
			        		}
			        		m_nResultUnAnsQues++;
			        		m_r1.setClickable(false);
			        		m_r2.setClickable(false);
			        		m_r3.setClickable(false);
			        		m_r4.setClickable(false);
			        		m_r5.setClickable(false);
			        		//BlinkRadioButton();
			        	}
			       }
			   };
			   m_ClockTimer.start();
		}
	}
	@Override
	protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
       // m_DBhelper.close();
	}
	@Override
	protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
      //  m_DBhelper.close();
	}
	@Override
	protected void onResume() {
		super.onResume();
		//flipView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//flipView.onPause();
	}
	public void SwipeRight()
	{
		if( !m_bTimerRunning )
		{
			m_QuesCount++;
			m_QuestId++; 
			m_TimerAnimation.Clear();
			DisplayQuestions();
			m_BlinkRadio.clearAnimation();
		}
	}
	private void BlinkButton()
	{
		final ImageButton Next = (ImageButton)findViewById(R.id.Next);
		Animation mAnimation = new AlphaAnimation(1, 0);
	    mAnimation.setDuration(200);
	    mAnimation.setInterpolator(new LinearInterpolator());
	    mAnimation.setRepeatCount(Animation.INFINITE);
	    mAnimation.setRepeatMode(Animation.REVERSE); 
	    Next.startAnimation(mAnimation);
	    new CountDownTimer( 5000, 1000) { 
	        public void onTick(long millisUntilFinished) {    
	        	
	        }            
	        public void onFinish() {
	        	Next.clearAnimation();
	       }
	   }.start();
	}
	private void BlinkRadioButton()
	{
		Animation mAnimation = new AlphaAnimation(1, (float) 0.75 );
	    mAnimation.setDuration(1000);
	    mAnimation.setInterpolator(new LinearInterpolator());
	    mAnimation.setRepeatCount(Animation.INFINITE);
	    mAnimation.setRepeatMode(Animation.REVERSE); 
	    m_BlinkRadio.startAnimation(mAnimation);
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent event ) {
		// TODO Auto-generated method stub
		 if ( m_gestureDetector.onTouchEvent(event))
		        return true;
		    else
		        return false;
	}
	class MyGestureDetector extends SimpleOnGestureListener {
		private final float SWIPE_MIN_DISTANCE;
	    private final int SWIPE_THRESHOLD_VELOCITY;
	    private final float SWIPE_MAX_OFF_PATH;
	    Context m_Context;
        public MyGestureDetector(Context context) {
			// TODO Auto-generated constructor stub
        	final ViewConfiguration vc = ViewConfiguration.get(context);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            SWIPE_MIN_DISTANCE = vc.getScaledPagingTouchSlop() * dm.density;
            SWIPE_THRESHOLD_VELOCITY = vc.getScaledMinimumFlingVelocity();
            SWIPE_MAX_OFF_PATH = SWIPE_MIN_DISTANCE * 2; 
		}

		@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	SwipeRight();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		m_ClockTimer.cancel();
		m_bTimerRunning = false;
		m_TimerAnimation.Clear();
		int RbtnId = v.getId();
		Cursor cursor = m_DBhelper.QueryRawSQL("SELECT answer FROM answers_table where question_id=" + m_QuestId );
		int nAnswer = 0;
		if( cursor.moveToFirst())
		{
			String strText = cursor.getString(0);
			nAnswer = Integer.parseInt(strText);
		}
		cursor.close();
		if( RbtnId == R.id.Answer_First)
		{
			if( 1 == nAnswer )
			{
				m_r1.setBackgroundResource( R.drawable.button_highlight_true  );
				m_nResultAnsCnt++;
			}
			else
			{
				m_nResultWrongAns++;
				m_r1.setBackgroundResource( R.drawable.button_highlight_false  );
				if( 2 == nAnswer )
				{
					m_r2.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 3 == nAnswer )
				{
					m_r3.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 4 == nAnswer )
				{
					m_r4.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 5 == nAnswer )
				{
					m_r5.setBackgroundResource(R.drawable.button_highlight_true);
				}
			}
			m_BlinkRadio = m_r1; 
		}
		else if ( RbtnId == R.id.Answer_Second)
		{
			if( 2 == nAnswer )
			{
				m_r2.setBackgroundResource( R.drawable.button_highlight_true  );
				m_nResultAnsCnt++;
			}
			else
			{
				m_nResultWrongAns++;
				m_r2.setBackgroundResource( R.drawable.button_highlight_false  );
				if( 1 == nAnswer )
				{
					m_r1.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 3 == nAnswer )
				{
					m_r3.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 4 == nAnswer )
				{
					m_r4.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 5 == nAnswer )
				{
					m_r5.setBackgroundResource(R.drawable.button_highlight_true);
				}
			}
			m_BlinkRadio = m_r2; 
		}
		else if ( RbtnId == R.id.Answer_Third)
		{
			if( 3 == nAnswer )
			{
				m_nResultAnsCnt++;
				m_r3.setBackgroundResource( R.drawable.button_highlight_true  );
			}
			else
			{
				m_nResultWrongAns++;
				m_r3.setBackgroundResource( R.drawable.button_highlight_false  );
				if( 1 == nAnswer )
				{
					m_r1.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 2 == nAnswer )
				{
					m_r2.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 4 == nAnswer )
				{
					m_r4.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 5 == nAnswer )
				{
					m_r5.setBackgroundResource(R.drawable.button_highlight_true);
				}
			}
			m_BlinkRadio = m_r3; 
		}
		else if ( RbtnId == R.id.Answer_Fourth)
		{
			if( 4 == nAnswer )
			{
				m_nResultAnsCnt++;
				m_r4.setBackgroundResource( R.drawable.button_highlight_true  );
			}
			else
			{
				m_nResultWrongAns++;
				m_r4.setBackgroundResource( R.drawable.button_highlight_false  );
				if( 1 == nAnswer )
				{
					m_r1.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 2 == nAnswer )
				{
					m_r2.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 3 == nAnswer )
				{
					m_r3.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 5 == nAnswer )
				{
					m_r5.setBackgroundResource(R.drawable.button_highlight_true);
				}
			}
			m_BlinkRadio = m_r4; 
		}
		else if ( RbtnId == R.id.Answer_Fifth)
		{
			if( 5 == nAnswer )
			{
				m_nResultAnsCnt++;
				m_r5.setBackgroundResource( R.drawable.button_highlight_true  );
			}
			else
			{
				m_nResultWrongAns++;
				m_r5.setBackgroundResource( R.drawable.button_highlight_false  );
				if( 1 == nAnswer )
				{
					m_r1.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 2 == nAnswer )
				{
					m_r2.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 3 == nAnswer )
				{
					m_r3.setBackgroundResource(R.drawable.button_highlight_true);
				}
				else if( 4 == nAnswer )
				{
					m_r4.setBackgroundResource(R.drawable.button_highlight_true);
				}
			}
			m_BlinkRadio = m_r5; 
		}
		m_r1.setClickable(false);
		m_r2.setClickable(false);
		m_r3.setClickable(false);
		m_r4.setClickable(false);
		m_r5.setClickable(false);
		//BlinkRadioButton();
		BlinkButton();
		
	}
}

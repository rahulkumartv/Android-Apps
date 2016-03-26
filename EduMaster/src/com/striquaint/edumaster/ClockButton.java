package com.striquaint.edumaster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.Toast;

public class ClockButton extends ImageButton {
	private int m_Degree;
	private int m_nAngle = 0;
	private boolean m_bClear = false;
	public ClockButton(Context context) {
		super(context);
		m_Degree = 18;
		// TODO Auto-generated constructor stub
	}
	public ClockButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Degree = 18;
	}
	public void SetIntervel( int nIntv)
	{
		if( nIntv<= 0 )
		{
			nIntv = 1;
		}
		m_Degree = m_Degree * nIntv;
	}
	public void SetTimerMax( int nMax )
	{
		m_nAngle =0;
		m_Degree = 360/nMax;
	}
	public void NotifyDraw( )
	{ 
		invalidate();
	}
	public void Clear( )
	{ 
		m_bClear = true;
		invalidate();
		m_nAngle = 0;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(  m_bClear || m_nAngle > 360)
		{
			m_bClear = false;
			return;
		}
		float width = (float)getWidth();
		float height = (float)getHeight();
		float radius = height/2;			
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(5);	
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		float center_x, center_y;
		center_x = width/2;
		center_y = height/2;
		final RectF oval = new RectF();
		oval.set(center_x - radius, 
				center_y - radius, 
				center_x + radius, 
				center_y + radius);
		m_nAngle += m_Degree;
		canvas.drawArc(oval, 270, m_nAngle, true, paint);
	}
}
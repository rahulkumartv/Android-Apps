package com.striquaint.pincodeassist;
import static com.striquaint.pincodeassist.Constants.FIRST_COLUMN;
import static com.striquaint.pincodeassist.Constants.SECOND_COLUMN;
import static com.striquaint.pincodeassist.Constants.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class AlphabeticSectionListView extends Activity
{
    private GestureDetector mGestureDetector;

    // x and y coordinates within our side index
    private static float sideIndexX;
    private static float sideIndexY;

    // height of side index
    private int sideIndexHeight;

    // number of items in the side index
    private int indexListSize;

    // list with items for side index
    private ArrayList<Object[]> indexList = null;
    String[] m_listItems;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);
        final String key = getIntent().getExtras().getString("key");
        Bundle bundle=this.getIntent().getExtras();
    	m_listItems = bundle.getStringArray(key);
        // don't forget to sort our array (in case it's not sorted)
        Arrays.sort(m_listItems);

        final ListView lv1 = (ListView) findViewById(R.id.ListView01);
        lv1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, m_listItems));
        lv1.setChoiceMode(1);
        lv1.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String selectedFromList = lv1.getAdapter().getItem(arg2).toString();
				Intent intent = new Intent();
				// Sending param key as 'website' and value as 'androidhive.info'
				intent.putExtra("key", key );
				if( key.equals(FIRST_COLUMN))
				{
					intent.putExtra(FIRST_COLUMN, selectedFromList );
					setResult(100,intent);
				}
				if( key.equals(SECOND_COLUMN))
				{
					intent.putExtra(SECOND_COLUMN, selectedFromList );
					setResult(101,intent);
				}
				if( key.equals(THIRD_COLUMN))
				{
					intent.putExtra(THIRD_COLUMN, selectedFromList );
					setResult(102,intent);
				}
				finish();
			}
        	
        });
        mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mGestureDetector.onTouchEvent(event))
        {
            return true;
        } else
        {
            return false;
        }
    }

    private ArrayList<Object[]> createIndex(String[] strArr)
    {
        ArrayList<Object[]> tmpIndexList = new ArrayList<Object[]>();
        Object[] tmpIndexItem = null;

        int tmpPos = 0;
        String tmpLetter = "";
        String currentLetter = null;
        String strItem = null;

        for (int j = 0; j < strArr.length; j++)
        {
            strItem = strArr[j];
            currentLetter = strItem.substring(0, 1);

            // every time new letters comes
            // save it to index list
            if (!currentLetter.equals(tmpLetter))
            {
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = tmpLetter;
                tmpIndexItem[1] = tmpPos - 1;
                tmpIndexItem[2] = j - 1;

                tmpLetter = currentLetter;
                tmpPos = j + 1;

                tmpIndexList.add(tmpIndexItem);
            }
        }

        // save also last letter
        tmpIndexItem = new Object[3];
        tmpIndexItem[0] = tmpLetter;
        tmpIndexItem[1] = tmpPos - 1;
        tmpIndexItem[2] = strArr.length - 1;
        tmpIndexList.add(tmpIndexItem);

        // and remove first temporary empty entry
        if (tmpIndexList != null && tmpIndexList.size() > 0)
        {
            tmpIndexList.remove(0);
        }

        return tmpIndexList;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        sideIndex.removeAllViews();

        // TextView for every visible item
        TextView tmpTV = null;

        // we'll create the index list
        indexList = createIndex(m_listItems);

        // number of items in the index List
        indexListSize = indexList.size();

        // maximal number of item, which could be displayed
        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);

        int tmpIndexListSize = indexListSize;

        // handling that case when indexListSize > indexMaxSize
        while (tmpIndexListSize > indexMaxSize)
        {
            tmpIndexListSize = tmpIndexListSize / 2;
        }

        // computing delta (only a part of items will be displayed to save a
        // place)
        double delta = indexListSize / tmpIndexListSize;

        String tmpLetter = null;
        Object[] tmpIndexItem = null;

        // show every m-th letter
        for (double i = 1; i <= indexListSize; i = i + delta)
        {
            tmpIndexItem = indexList.get((int) i - 1);
            tmpLetter = tmpIndexItem[0].toString();
            tmpTV = new TextView(this);
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(20);
            tmpTV.setClickable(true);
            tmpTV.setId(1);
            tmpTV.setFocusable(true);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        // and set a touch listener for it
        sideIndex.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();

                return false;
            }
        });
    }

    class SideIndexGestureListener extends
            GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY)
        {
            // we know already coordinates of first touch
            // we know as well a scroll distance
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            // when the user scrolls within our side index
            // we can show for every position in it a proper
            // item in the country list
            if (sideIndexX >= 0 && sideIndexY >= 0)
            {
                displayListItem();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void displayListItem()
    {
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // compute minimal position for the item in the list
        int minPosition = (int) (itemPosition * pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        Object[] indexItem = indexList.get(itemPosition);

        // and compute the proper item in the country list
        int indexMin = Integer.parseInt(indexItem[1].toString());
        int indexMax = Integer.parseInt(indexItem[2].toString());
        int indexDelta = Math.max(1, indexMax - indexMin);

        double pixelPerSubitem = pixelPerIndexItem / indexDelta;
        int subitemPosition = (int) (indexMin + (sideIndexY - minPosition) / pixelPerSubitem);

        ListView listView = (ListView) findViewById(R.id.ListView01);
        listView.setSelection(subitemPosition);
    }
}
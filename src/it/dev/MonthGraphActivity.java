package it.dev;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MonthGraphActivity extends Activity 
{
	private LinearLayout month_LL;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.month_graph_activity);
        
        month_LL = (LinearLayout) findViewById(R.id.month_LL);
        
        callServerForMonthData();
    }
	
	private void callServerForMonthData()
	{
		//do something...
	}
}

package it.dev;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class YearGraphActivity extends Activity 
{
	private LinearLayout year_LL;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.year_graph_activity);
        
        year_LL = (LinearLayout) findViewById(R.id.year_LL);
        
        callServerForYearData();
    }
	
	private void callServerForYearData()
	{
		//do something...
	}
}

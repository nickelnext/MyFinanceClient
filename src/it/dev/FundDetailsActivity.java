package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class FundDetailsActivity extends Activity 
{
	private TextView fundReferenceTextView;
	private String fundIsin;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fund_details);
		
		fundReferenceTextView = (TextView) findViewById(R.id.fundReferenceTextView);
		
		Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        fundIsin = (String) intent.getStringExtra(pkg+".fundIsin");        
    	fundReferenceTextView.setText(fundIsin);
    }
}

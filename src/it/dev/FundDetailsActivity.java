package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class FundDetailsActivity extends Activity 
{
	private TextView fundReferenceTextView;
	private String fundIsin;
	
	private TextView fundisinTextView;
	private TextView fundnameTextView;
	private TextView fundmanagerTextView;
	private TextView fundcategoryTextView;
	private TextView fundbenchmarkTextView;
	private TextView fundlastPrizeTextView;
	private TextView fundlastPriceDateTextView;
	private TextView fundprecPrizeTextView;
	private TextView fundcurrencyTextView;
	private TextView fundpercVariationTextView;
	private TextView fundvariationTextView;
	private TextView fundperformance1MonthTextView;
	private TextView fundperformance3MonthTextView;
	private TextView fundperformance1YearTextView;
	private TextView fundperformance3YearTextView;
	private TextView fundlastUpdateTextView;
	
	private MyFinanceDatabase db;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fund_details);
		
		fundReferenceTextView = (TextView) findViewById(R.id.fundReferenceTextView);
		
		getViews();
		
		Intent intent = getIntent();
        String pkg = getPackageName();
        
        fundIsin = (String) intent.getStringExtra(pkg+".fundIsin");        
    	fundReferenceTextView.setText(fundIsin);
    	
    	db = new MyFinanceDatabase(this);
    }
	
	public void onResume()
	{
		super.onResume();
		updateView();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
    {
    	getMenuInflater().inflate(R.menu.tool_detail_menu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.menu_forced_update:
    		//do forced update...
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	private void getViews()
	{
		fundisinTextView = (TextView) findViewById(R.id.fundisinTextView);
		fundnameTextView = (TextView) findViewById(R.id.fundnameTextView);
		fundmanagerTextView = (TextView) findViewById(R.id.fundmanagerTextView);
		fundcategoryTextView = (TextView) findViewById(R.id.fundcategoryTextView);
		fundbenchmarkTextView = (TextView) findViewById(R.id.fundbenchmarkTextView);
		fundlastPrizeTextView = (TextView) findViewById(R.id.fundlastPrizeTextView);
		fundlastPriceDateTextView = (TextView) findViewById(R.id.fundlastPriceDateTextView);
		fundprecPrizeTextView = (TextView) findViewById(R.id.fundprecPrizeTextView);
		fundcurrencyTextView = (TextView) findViewById(R.id.fundcurrencyTextView);
		fundpercVariationTextView = (TextView) findViewById(R.id.fundpercVariationTextView);
		fundvariationTextView = (TextView) findViewById(R.id.fundvariationTextView);
		fundperformance1MonthTextView = (TextView) findViewById(R.id.fundperformance1MonthTextView);
		fundperformance3MonthTextView = (TextView) findViewById(R.id.fundperformance3MonthTextView);
		fundperformance1YearTextView = (TextView) findViewById(R.id.fundperformance1YearTextView);
		fundperformance3YearTextView = (TextView) findViewById(R.id.fundperformance3YearTextView);
		fundlastUpdateTextView = (TextView) findViewById(R.id.fundlastUpdateTextView);
	}
	
	private void updateView()
    {
		db.open();
		
		/*
		 * Cursor fundDetails = db.getDetailsOfFund(fundISIN);
		 * startManagingCursor(fundDetails);
		 * 
		 * if(fundDetails.getCount()==1)
		 * {
		 * 		fundDetails.moveToFirst();
		 * 		fundisinTextView.setText(fundDetails.getString(1));
		 * 		...
		 * 		...
		 * 		fundlastUpdateTextView.setText(fundDetails.getString(...));
		 * }
		 * 
		 * */ 
		
		
		db.close();
    }
}

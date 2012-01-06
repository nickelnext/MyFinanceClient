package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class FundDetailsActivity extends Activity 
{
	private TextView fundReferenceTextView;
	private String fundIsin;
	private String fundPurchaseDate;
	private String fundPurchasePrize;
	private String fundRoundLot;
	
	private TextView fund_purchaseDate_TV;
	private TextView fund_purchasePrize_TV;
	private TextView fund_roundLot_TV;
	
	private TextView sourceSite;
	private TextView isin;
	private TextView name;
	private TextView manager;
	private TextView category;
	private TextView benchmark;
	private TextView lastPrize;
	private TextView lastPriceDate;
	private TextView precPrize;
	private TextView currency;
	private TextView percentualVariation;
	private TextView variation;
	private TextView performance1Month;
	private TextView performance3Month;
	private TextView performance1Year;
	private TextView performance3Year;
	private TextView lastUpdateDate;
	
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
        fundPurchaseDate = (String) intent.getStringExtra(pkg+".fundPurchaseDate");
        fundPurchasePrize = (String) intent.getStringExtra(pkg+".fundPurchasePrize");
        fundRoundLot = (String) intent.getStringExtra(pkg+".fundRoundLot");
    	fundReferenceTextView.setText(fundIsin);
    	
    	db = new MyFinanceDatabase(this);
    	
    	fund_purchaseDate_TV.setText(fundPurchaseDate);
    	fund_purchasePrize_TV.setText(fundPurchasePrize);
    	fund_roundLot_TV.setText(fundRoundLot);
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
		fund_purchaseDate_TV = (TextView) findViewById(R.id.fund_purchaseDate_TV);
		fund_purchasePrize_TV = (TextView) findViewById(R.id.fund_purchasePrize_TV);
		fund_roundLot_TV = (TextView) findViewById(R.id.fund_roundLot_TV);
		
		sourceSite = (TextView) findViewById(R.id.fundsourceSiteTextView);
		isin = (TextView) findViewById(R.id.fundisinTextView);
		name = (TextView) findViewById(R.id.fundnameTextView);
		manager = (TextView) findViewById(R.id.fundmanagerTextView);
		category = (TextView) findViewById(R.id.fundcategoryTextView);
		benchmark = (TextView) findViewById(R.id.fundbenchmarkTextView);
		lastPrize = (TextView) findViewById(R.id.fundlastPrizeTextView);
		lastPriceDate = (TextView) findViewById(R.id.fundlastPriceDateTextView);
		precPrize = (TextView) findViewById(R.id.fundprecPrizeTextView);
		currency = (TextView) findViewById(R.id.fundcurrencyTextView);
		percentualVariation = (TextView) findViewById(R.id.fundpercVariationTextView);
		variation = (TextView) findViewById(R.id.fundvariationTextView);
		performance1Month = (TextView) findViewById(R.id.fundperformance1MonthTextView);
		performance3Month = (TextView) findViewById(R.id.fundperformance3MonthTextView);
		performance1Year = (TextView) findViewById(R.id.fundperformance1YearTextView);
		performance3Year = (TextView) findViewById(R.id.fundperformance3YearTextView);
		lastUpdateDate = (TextView) findViewById(R.id.fundlastUpdateTextView);
	}	
	private void updateView()
    {
		db.open();
		
    	Cursor details = db.getFondDetails(fundIsin);
    	startManagingCursor(details);
    	
    	details.moveToFirst();
    	
    	sourceSite.setText(details.getString(17));
    	isin.setText(details.getString(1));
    	name.setText(details.getString(2));
		manager.setText(details.getString(3));
		category.setText(details.getString(4));
		benchmark.setText(details.getString(5));
		lastPrize.setText(String.valueOf(details.getString(6)));
		lastPriceDate.setText(details.getString(7));
		precPrize.setText(String.valueOf(details.getString(8)));
		currency.setText(details.getString(9));
		percentualVariation.setText(String.valueOf(details.getString(10)));
		variation.setText(String.valueOf(details.getString(11)));
		performance1Month.setText(String.valueOf(details.getString(12)));
		performance3Month.setText(String.valueOf(details.getString(13)));
		performance1Year.setText(String.valueOf(details.getString(14)));
		performance3Year.setText(String.valueOf(details.getString(15)));
		lastUpdateDate.setText(details.getString(16));
    				
		db.close();
    }
}

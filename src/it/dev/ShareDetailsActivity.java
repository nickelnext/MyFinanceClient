package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class ShareDetailsActivity extends Activity 
{
	private TextView shareReferenceTextView;
	private String shareIsin;
	
	private TextView code;
	private TextView isin;
	private TextView name;
	private TextView minRoundLot;
	private TextView marketPhase;
	private TextView lastContractPrice;
	private TextView percentualVariation;
	private TextView variation;
	private TextView lastContractDate;
	private TextView buyPrice;
	private TextView sellPrice;
	private TextView lastAmount;
	private TextView buyAmount;
	private TextView sellAmount;
	private TextView totalAmount;
	private TextView maxToday;
	private TextView minToday;
	private TextView maxYear;
	private TextView minYear;
	private TextView maxYearDate;
	private TextView minYearDate;
	private TextView lastClose;
	private TextView lastUpdateDate;
	
	private MyFinanceDatabase db;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_details);
		
		shareReferenceTextView = (TextView) findViewById(R.id.shareReferenceTextView);
		
        Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        shareIsin = (String) intent.getStringExtra(pkg+".shareIsin");        
    	shareReferenceTextView.setText(shareIsin);
    	
    	getViews();
    	
    	db = new MyFinanceDatabase(this);
    }
	
	public void onResume()
	{
		super.onResume();
		updateView();
	}
	
	private void getViews()
	{
		code = (TextView) findViewById(R.id.shareCodeTextView);
		isin = (TextView) findViewById(R.id.shareIsinTextView);
		name = (TextView) findViewById(R.id.shareNameTextView);
		minRoundLot = (TextView) findViewById(R.id.shareMinRoundLotTextView);
		marketPhase = (TextView) findViewById(R.id.shareMarketPhaseTextView);
		lastContractPrice = (TextView) findViewById(R.id.shareLastContractPriceTextView);
		percentualVariation = (TextView) findViewById(R.id.sharePercVarTextView);
		variation = (TextView) findViewById(R.id.shareVariationTextView);
		lastContractDate = (TextView) findViewById(R.id.shareLastContractDateTextView);
		buyPrice = (TextView) findViewById(R.id.shareBuyPriceTextView);
		sellPrice = (TextView) findViewById(R.id.shareSellPriceView);
		lastAmount = (TextView) findViewById(R.id.shareLastAmountTextView);
		buyAmount = (TextView) findViewById(R.id.shareBuyAmountTextView);
		sellAmount = (TextView) findViewById(R.id.shareSellAmountTextView);
		totalAmount = (TextView) findViewById(R.id.shareTotalAmountTextView);
		maxToday = (TextView) findViewById(R.id.shareMaxTodayTextView);
		minToday = (TextView) findViewById(R.id.shareMinTodayTextView);
		maxYear = (TextView) findViewById(R.id.shareMaxYearTextView);
		minYear = (TextView) findViewById(R.id.shareMinYearTextView);
		maxYearDate = (TextView) findViewById(R.id.shareMaxYearDateTextView);
		minYearDate = (TextView) findViewById(R.id.shareMinYearDateTextView);
		lastClose = (TextView) findViewById(R.id.shareLastCloseTextView);
		lastUpdateDate = (TextView) findViewById(R.id.shareLastUpDateTextView);
	}
	
	private void updateView()
	{
db.open();
    	
    	Cursor details = db.getShareDetails(shareIsin);
    	startManagingCursor(details);
    	
    	code.setText(details.getString(1));
    	isin.setText(details.getString(2));
    	minRoundLot.setText(details.getString(3));
		marketPhase.setText(details.getString(4));
		lastContractPrice.setText(String.valueOf(details.getString(5)));
		percentualVariation.setText(String.valueOf(details.getString(6)));
		variation.setText(String.valueOf(details.getString(7)));
		lastContractDate.setText(details.getString(8));
		buyPrice.setText(String.valueOf(details.getString(9)));
		sellPrice.setText(String.valueOf(details.getString(10)));
		lastAmount.setText(details.getString(11));
		buyAmount.setText(details.getString(12));
		sellAmount.setText(details.getString(13));
		totalAmount.setText(details.getString(14));
		maxToday.setText(String.valueOf(details.getString(15)));
		minToday.setText(String.valueOf(details.getString(16)));
		maxYear.setText(String.valueOf(details.getString(17)));
		minYear.setText(String.valueOf(details.getString(18)));
		maxYearDate.setText(details.getString(19));
		minYearDate.setText(details.getString(20));
		lastClose.setText(String.valueOf(details.getString(21)));
		lastClose.setText(details.getString(22));
		lastUpdateDate.setText(details.getString(23));
    		
    	db.close();
	}
}

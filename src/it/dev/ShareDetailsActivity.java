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
	private String sharePurchaseDate;
	private String sharePurchasePrize;
	private String shareRoundLot;
	
	private TextView share_purchaseDate_TV;
	private TextView share_purchasePrize_TV;
	private TextView share_roundLot_TV;
	
	private TextView sourceSite;
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
		
        Intent intent = getIntent();
        String pkg = getPackageName();
        
        shareIsin = (String) intent.getStringExtra(pkg+".shareIsin");        
        sharePurchaseDate = (String) intent.getStringExtra(pkg+".sharePurchaseDate");
        sharePurchasePrize = (String) intent.getStringExtra(pkg+".sharePurchasePrize");
        shareRoundLot = (String) intent.getStringExtra(pkg+".shareRoundLot");
        shareReferenceTextView.setText(shareIsin);
    	
    	getViews();
    	
    	db = new MyFinanceDatabase(this);
    	
    	share_purchaseDate_TV.setText(sharePurchaseDate);
    	share_purchasePrize_TV.setText(sharePurchasePrize);
    	share_roundLot_TV.setText(shareRoundLot);
    }
	
	public void onResume()
	{
		super.onResume();
		updateView();
	}
	
	private void getViews()
	{
		share_purchaseDate_TV = (TextView) findViewById(R.id.share_purchaseDate_TV);
		share_purchasePrize_TV = (TextView) findViewById(R.id.share_purchasePrize_TV);
		share_roundLot_TV = (TextView) findViewById(R.id.share_roundLot_TV);
		
		sourceSite = (TextView) findViewById(R.id.sharesourceSiteTextView);
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
    	
    	details.moveToFirst();
    	
    	sourceSite.setText(details.getString(24));
    	code.setText(details.getString(1));
    	isin.setText(details.getString(2));
    	name.setText(details.getString(3));
    	minRoundLot.setText(String.valueOf(details.getString(4)));
		marketPhase.setText(details.getString(5));
		lastContractPrice.setText(String.valueOf(details.getString(6)));
		percentualVariation.setText(String.valueOf(details.getString(7)));
		variation.setText(String.valueOf(details.getString(8)));
		lastContractDate.setText(details.getString(9));
		buyPrice.setText(String.valueOf(details.getString(10)));
		sellPrice.setText(String.valueOf(details.getString(11)));
		lastAmount.setText(String.valueOf(details.getString(12)));
		buyAmount.setText(String.valueOf(details.getString(13)));
		sellAmount.setText(String.valueOf(details.getString(14)));
		totalAmount.setText(String.valueOf(details.getString(15)));
		maxToday.setText(String.valueOf(details.getString(16)));
		minToday.setText(String.valueOf(details.getString(17)));
		maxYear.setText(String.valueOf(details.getString(18)));
		minYear.setText(String.valueOf(details.getString(19)));
		maxYearDate.setText(details.getString(20));
		minYearDate.setText(details.getString(21));
		lastClose.setText(String.valueOf(details.getString(22)));
		lastUpdateDate.setText(details.getString(23));
    		
    	db.close();
	}
}

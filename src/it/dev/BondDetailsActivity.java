package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class BondDetailsActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private TextView bondReferenceTextView;
	private String bondIsin;
	private String bondPurchaseDate;
	private String bondPurchasePrize;
	private String bondRoundLot;
	
	private TextView purchaseDate_TV;
	private TextView purchasePrize_TV;
	private TextView roundLot_TV;
	
	private TextView isin;
	private TextView name;
	private TextView currency;
	private TextView market;
	private TextView marketPhase;
	private TextView lastContractPrice;
	private TextView percentualVariation;
	private TextView variation;
	private TextView lastContractDate;
	private TextView lastVolume;
	private TextView buyVolume;
	private TextView sellVolume;
	private TextView totalVolume;
	private TextView buyPrice;
	private TextView sellPrice;
	private TextView maxToday;
	private TextView minToday;
	private TextView maxYear;
	private TextView minYear;
	private TextView maxYearDate;
	private TextView minYearDate;
	private TextView lastClose;
	private TextView expirationDate;
	private TextView couponDate;
	private TextView coupon;
	private TextView minRoundLot;
	private TextView lastUpdateDate;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bond_details);
		
		bondReferenceTextView = (TextView) findViewById(R.id.bondReferenceTextView);
		
		Intent intent = getIntent();
        String pkg = getPackageName();
        
        bondIsin = (String) intent.getStringExtra(pkg+".bondIsin");
        bondPurchaseDate = (String) intent.getStringExtra(pkg+".bondPurchaseDate");
        bondPurchasePrize = (String) intent.getStringExtra(pkg+".bondPurchasePrize");
        bondRoundLot = (String) intent.getStringExtra(pkg+".bondRoundLot");
    	bondReferenceTextView.setText(bondIsin);
    	
    	getViews();
    	
    	db = new MyFinanceDatabase(this);
    	
    	purchaseDate_TV.setText(bondPurchaseDate);
    	purchasePrize_TV.setText(bondPurchasePrize);
    	roundLot_TV.setText(bondRoundLot);
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
	
	private void updateView()
    {    	    	
    	db.open();
    	
    	Cursor details = db.getBondDetails(bondIsin);
    	startManagingCursor(details);
    	
    	if(details.getCount()==1)
    	{
    		details.moveToFirst();
        	
        	isin.setText(details.getString(1));
        	name.setText(details.getString(2));
    		currency.setText(details.getString(3));
    		market.setText(details.getString(4));
    		marketPhase.setText(details.getString(5));
    		lastContractPrice.setText(String.valueOf(details.getString(6)));
    		percentualVariation.setText(String.valueOf(details.getString(7)));
    		variation.setText(String.valueOf(details.getString(8)));
    		lastContractDate.setText(details.getString(9));
    		lastVolume.setText(String.valueOf(details.getString(10)));
    		buyVolume.setText(String.valueOf(details.getString(11)));		
    		sellVolume.setText(String.valueOf(details.getString(12)));
    		totalVolume.setText(String.valueOf(details.getString(13)));
    		buyPrice.setText(String.valueOf(details.getString(14)));
    		sellPrice.setText(String.valueOf(details.getString(15)));
    		maxToday.setText(String.valueOf(details.getString(16)));
    		minToday.setText(String.valueOf(details.getString(17)));
    		maxYear.setText(String.valueOf(details.getString(18)));
    		minYear.setText(String.valueOf(details.getString(19)));
    		maxYearDate.setText(details.getString(20));
    		minYearDate.setText(details.getString(21));
    		lastClose.setText(String.valueOf(details.getString(22)));
    		expirationDate.setText(details.getString(23));
    		couponDate.setText(details.getString(24));
    		coupon.setText(String.valueOf(details.getString(25)));
    		minRoundLot.setText(String.valueOf(details.getString(26)));
    		lastUpdateDate.setText(details.getString(27));
    	}
    		
    	db.close();
    }
	
	private void getViews()
	{
		purchaseDate_TV = (TextView) findViewById(R.id.purchaseDate_TV);
		purchasePrize_TV = (TextView) findViewById(R.id.purchasePrize_TV);
		roundLot_TV = (TextView) findViewById(R.id.roundLot_TV);
		
		isin = (TextView) findViewById(R.id.bondIsinTextView);
		name = (TextView) findViewById(R.id.bondNameTextView);
		currency = (TextView) findViewById(R.id.bondCurrencyTextView);
		market = (TextView) findViewById(R.id.bondMarketTextView);
		marketPhase = (TextView) findViewById(R.id.bondMarketPhaseTextView);
		lastContractPrice = (TextView) findViewById(R.id.bondLastContractPrizeTextView);
		percentualVariation = (TextView) findViewById(R.id.bondPercVarTextView);
		variation = (TextView) findViewById(R.id.bondVariationTextView);
		lastContractDate = (TextView) findViewById(R.id.bondLastContractDateTextView);
		lastVolume = (TextView) findViewById(R.id.bondLastVolumeTextView);
		buyVolume = (TextView) findViewById(R.id.bondBuyVolumeTextView);
		sellVolume = (TextView) findViewById(R.id.bondSellVolumeTextView);
		totalVolume = (TextView) findViewById(R.id.bondTotalVolumeTextView);
		buyPrice = (TextView) findViewById(R.id.bondBuyPriceTextView);
		sellPrice = (TextView) findViewById(R.id.bondSellPriceTextView);
		maxToday = (TextView) findViewById(R.id.bondMaxTodayTextView);
		minToday = (TextView) findViewById(R.id.bondMinTodayTextView);
		maxYear = (TextView) findViewById(R.id.bondMaxYearTextView);
		minYear = (TextView) findViewById(R.id.bondMinYearTextView);
		maxYearDate = (TextView) findViewById(R.id.bondMaxYearDateTextView);
		minYearDate = (TextView) findViewById(R.id.bondMinYearDateTextView);
		lastClose = (TextView) findViewById(R.id.bondLastCloseTextView);
		expirationDate = (TextView) findViewById(R.id.bondExpirationDateTextView);
		couponDate = (TextView) findViewById(R.id.bondCouponDateTextView);
		coupon = (TextView) findViewById(R.id.bondCouponTextView);
		minRoundLot = (TextView) findViewById(R.id.bondMinRoundLotTextView);
		lastUpdateDate = (TextView) findViewById(R.id.bondLastUpDateTextView);
	}
}

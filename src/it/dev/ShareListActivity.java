package it.dev;

import it.dev.MyFinanceDatabase.ShareMetaData;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ShareListActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private String portfolioName;
	
	private ArrayList<String> shareIsinArrayList = new ArrayList<String>();
	private ArrayList<String> shareTypeArrayList = new ArrayList<String>();
	
	private TextView portfolioReferenceTextView;
	private TextView nameCol;
	private TextView variationCol;
	private TextView percVarCol;
	private TextView prizeCol;
	private ListView shareListView;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.share_list);
        
        portfolioReferenceTextView = (TextView) findViewById(R.id.portfolioReferenceTextView);
        nameCol = (TextView) findViewById(R.id.nameCol);
        variationCol = (TextView) findViewById(R.id.variationCol);
        percVarCol = (TextView) findViewById(R.id.percVarCol);
        prizeCol = (TextView) findViewById(R.id.prizeCol);
        shareListView = (ListView) findViewById(R.id.shareListView);
        
        nameCol.setVisibility(View.INVISIBLE);
        variationCol.setVisibility(View.INVISIBLE);
        percVarCol.setVisibility(View.INVISIBLE);
        prizeCol.setVisibility(View.INVISIBLE);
        
        Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        portfolioName = (String) intent.getStringExtra(pkg+".portfolioName");
        portfolioReferenceTextView.setText((String) intent.getStringExtra(pkg+".portfolioName"));
        
        db = new MyFinanceDatabase(this);
    }
	
	public void onResume()
    {
		super.onResume();
		updateView();
    }
	
	public boolean onCreateOptionsMenu(Menu menu)
    {
    	getMenuInflater().inflate(R.menu.add_share_menu, menu);
    	return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.menu_add_share:
    		goToAddNewShareActivity(portfolioName);
    		break;
    	case R.id.menu_manual_update:
    		//manual update...
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	private void goToAddNewShareActivity(String name)
    {
		Intent i = new Intent(this, AddNewShareActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".portfolioName", name);
		startActivity(i);
    }
	
	/////////////////////////////////////////////////////////////////////////////
	//-------------------------------UPDATE the view when:---------------------//
	//--------------------------------a. a context menu is closed--------------//
	//--------------------------------b. the Activity is resumed---------------//
	/////////////////////////////////////////////////////////////////////////////
	private void updateView()
    {
		//inizialize variables...
    	shareListView.setAdapter(null);
    	shareIsinArrayList.clear();
    	shareTypeArrayList.clear();
    	
    	db.open();
    	
    	Cursor c_bond = db.getAllBondOverviewInPortfolio(portfolioName);
    	startManagingCursor(c_bond);
    	saveSharesFromCursor(c_bond, "bond");
    	    	
    	Cursor c_fund = db.getAllFundOverviewInPortfolio(portfolioName);
    	startManagingCursor(c_fund);
    	saveSharesFromCursor(c_fund, "fund");
    	
    	Cursor c_share = db.getAllShareOverviewInPortfolio(portfolioName);
    	startManagingCursor(c_share);
    	saveSharesFromCursor(c_share, "share");
    	
    	
    	Cursor[] mCursor = new Cursor[3];
    	mCursor[0] = c_bond;
    	mCursor[1] = c_fund;
    	mCursor[2] = c_share;
    	
    	MergeCursor c_merged = new MergeCursor(mCursor);
    	startManagingCursor(c_merged);
    	
    	if(c_merged.getCount()!=0)
    	{
    		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.share_listview_items, c_merged, 
    				new String[] {"isin", ShareMetaData.SHARE_VARIATION_KEY, ShareMetaData.SHARE_PERCVAR_KEY, "prezzo"}, 
    				new int[] {R.id.isinTextView, R.id.variationTextView, R.id.percVarTextView, R.id.lastPrizeTextView});
    		shareListView.setAdapter(adapter);
    		shareListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
    		{
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    			{
    				if(shareTypeArrayList.get(position).equals("bond"))
    				{
    					goToBondDetailsActivity(shareIsinArrayList.get(position));
    				}
    				else if(shareTypeArrayList.get(position).equals("fund"))
    				{
    					goToFundDetailsActivity(shareIsinArrayList.get(position));
    				}
    				else if(shareTypeArrayList.get(position).equals("share"))
    				{
    					goToShareDetailsActivity(shareIsinArrayList.get(position));
    				}
    			}
    		});
    	}
    	db.close();
    }
	
	private void goToBondDetailsActivity(String bondIsin)
    {
    	Intent i = new Intent(this, BondDetailsActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".bondIsin", bondIsin);		
		startActivity(i);
    }
	
	private void goToFundDetailsActivity(String fundIsin)
    {
    	Intent i = new Intent(this, FundDetailsActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".fundIsin", fundIsin);		
		startActivity(i);
    }
	
	private void goToShareDetailsActivity(String shareIsin)
    {
    	Intent i = new Intent(this, ShareDetailsActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".shareIsin", shareIsin);		
		startActivity(i);
    }
	
	private void saveSharesFromCursor(Cursor c, String type)
	{
		if(c.getCount()!=0)
		{
			c.moveToFirst();
			do {
				shareIsinArrayList.add(c.getString(2));
				shareTypeArrayList.add(type);
			} while (c.moveToNext());
		}
	}
	
}

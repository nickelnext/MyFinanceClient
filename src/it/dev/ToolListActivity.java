package it.dev;

import it.dev.MyFinanceDatabase.PortfolioBondMetadata;
import it.dev.MyFinanceDatabase.ShareMetaData;
import it.util.ConnectionUtils;
import it.util.ResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;

import Quotes.QuotationContainer;
import Quotes.Quotation_Bond;
import Quotes.Quotation_Fund;
import Quotes.Quotation_Share;
import Requests.Request;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

public class ToolListActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private String portfolioName;
	
	private ArrayList<String> shareIsinArrayList = new ArrayList<String>();
	private ArrayList<String> shareTypeArrayList = new ArrayList<String>();
	private ArrayList<String> sharePurchaseDateArrayList = new ArrayList<String>();
	private ArrayList<String> sharePurchasePrizeArrayList = new ArrayList<String>();
	private ArrayList<String> shareRoundLotArrayList = new ArrayList<String>();
	
	private TextView portfolioReferenceTextView;
	private ListView toolListView;
	
	//liste di supporto per salvare i dati temporanei prima di scriverli nel database
	private ArrayList<String> listaIsinTmp = new ArrayList<String>();
	private ArrayList<String> listaDataAcqTmp = new ArrayList<String>();
	private ArrayList<String> listaPrezzoAcqTmp = new ArrayList<String>();
	private ArrayList<String> listaLottoTmp = new ArrayList<String>();
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_list_activity);
        
        portfolioReferenceTextView = (TextView) findViewById(R.id.portfolioReferenceTextView);
        toolListView = (ListView) findViewById(R.id.toolListView);
        
        Intent intent = getIntent();
        String pkg = getPackageName();
        
        portfolioName = (String) intent.getStringExtra(pkg+".portfolioName");
        portfolioReferenceTextView.setText(portfolioName);
        
        db = new MyFinanceDatabase(this);
    }
	
	public void onResume()
    {
		super.onResume();
		updateView();
    }
	
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.edit_item:
        	//fare
        	return true;            
        case R.id.remove_item:        	
        	deleteSelectedTool(shareIsinArrayList.get(info.position), shareTypeArrayList.get(info.position));        	
        	return true;        
        }
        return false;
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
    		showAddNewToolDialog();
    		break;
    	case R.id.menu_manual_update:
    		//manual update...
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	//Open the custom alert dialog where it is possible to add a new tool.
	private void showAddNewToolDialog()
	{
		//initilize arraylists...
		listaIsinTmp.clear();
		listaDataAcqTmp.clear();
		listaPrezzoAcqTmp.clear();
		listaLottoTmp.clear();
		
		final Dialog addToolDialog = new Dialog(ToolListActivity.this);
		addToolDialog.setContentView(R.layout.custom_add_new_tool);
		addToolDialog.setTitle("Add a new Tool");
		addToolDialog.setCancelable(true);
		final EditText shareISINEditText = (EditText) addToolDialog.findViewById(R.id.shareISINEditText);
		final DatePicker purchaseDateDatePicker = (DatePicker) addToolDialog.findViewById(R.id.purchaseDateDatePicker);
		final EditText buyPriceEditText = (EditText) addToolDialog.findViewById(R.id.buyPriceEditText);
		final EditText roundLotEditText = (EditText) addToolDialog.findViewById(R.id.roundLotEditText);
		Button undoNewShareButton = (Button) addToolDialog.findViewById(R.id.undoNewToolButton);
		Button saveNewToolButton = (Button) addToolDialog.findViewById(R.id.saveNewToolButton);
		Button finishAddToolsButton = (Button) addToolDialog.findViewById(R.id.finishAddToolsButton);
		
		undoNewShareButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addToolDialog.dismiss();
			}
		});
		
		saveNewToolButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(shareISINEditText.getText().length()!=0 && buyPriceEditText.getText().length()!=0 && roundLotEditText.getText().length()!=0)
				{
					//save temporary data....[USING ARRAYLIST<STRING>]
					String purchaseDate = String.valueOf(purchaseDateDatePicker.getDayOfMonth()) + "/" + String.valueOf(purchaseDateDatePicker.getMonth()) + "/" + String.valueOf(purchaseDateDatePicker.getYear());
					listaIsinTmp.add(shareISINEditText.getText().toString());
					listaDataAcqTmp.add(purchaseDate);
					listaPrezzoAcqTmp.add(buyPriceEditText.getText().toString());
					listaLottoTmp.add(roundLotEditText.getText().toString());
					
					//initialize view...
					final Calendar c = Calendar.getInstance();
					shareISINEditText.setText("");
					purchaseDateDatePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
					buyPriceEditText.setText("");
					roundLotEditText.setText("");
				}
				else
				{
					showMessage("Error", "Control that you have insert all the data.");
				}			
			}
		});
		
		finishAddToolsButton.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				if(shareISINEditText.getText().length()!=0 && buyPriceEditText.getText().length()!=0 && roundLotEditText.getText().length()!=0)
				{
					
					//0. add last tool in ArrayList<String>...
					String purchaseDate = String.valueOf(purchaseDateDatePicker.getDayOfMonth()) + "/" + String.valueOf(purchaseDateDatePicker.getMonth()+1) + "/" + String.valueOf(purchaseDateDatePicker.getYear());
					listaIsinTmp.add(shareISINEditText.getText().toString());
					listaDataAcqTmp.add(purchaseDate);
					listaPrezzoAcqTmp.add(buyPriceEditText.getText().toString());
					listaLottoTmp.add(roundLotEditText.getText().toString());
					
					//1. create arrayList of Quotation Request....
					ArrayList<Request> array = new ArrayList<Request>();
					for (int i = 0; i < listaIsinTmp.size(); i++) 
					{
						array.add(new Request(listaIsinTmp.get(i)));
					}
					
					//2. CALL ASYNCTASK TO GET DATA FROM SERVER....
					QuotationRequestAsyncTask asyncTask1 = new QuotationRequestAsyncTask(ToolListActivity.this);
					asyncTask1.execute(array);
					
					//3.dismiss dialog...
					addToolDialog.dismiss();
				}
				else
				{
					showMessage("Error", "Control that you have insert all the data.");
				}
			}
		});
		
		addToolDialog.show();
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//-------------------------------UPDATE the view when:---------------------//
	//--------------------------------a. a context menu is closed--------------//
	//--------------------------------b. the Activity is resumed---------------//
	//--------------------------------c. the add new tool dialog dismiss-------//
	/////////////////////////////////////////////////////////////////////////////
	private void updateView()
    {
		//inizialize variables...
		toolListView.setAdapter(null);
    	shareIsinArrayList.clear();
    	shareTypeArrayList.clear();
    	sharePurchaseDateArrayList.clear();
    	sharePurchasePrizeArrayList.clear();
    	shareRoundLotArrayList.clear();
    	
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
    		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.tool_listview_items, c_merged, 
    				new String[] {"isin", PortfolioBondMetadata.BOND_BUYDATE_KEY, ShareMetaData.SHARE_VARIATION_KEY, ShareMetaData.SHARE_PERCVAR_KEY, "prezzo"}, 
    				new int[] {R.id.isinTextView, R.id.dateTextView, R.id.variationTextView, R.id.percVarTextView, R.id.lastPrizeTextView});
    		toolListView.setAdapter(adapter);
    		toolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
    		{
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    			{
    				if(shareTypeArrayList.get(position).equals("bond"))
    				{
    					goToBondDetailsActivity(shareIsinArrayList.get(position), sharePurchaseDateArrayList.get(position), sharePurchasePrizeArrayList.get(position), shareRoundLotArrayList.get(position));
    				}
    				else if(shareTypeArrayList.get(position).equals("fund"))
    				{
    					goToFundDetailsActivity(shareIsinArrayList.get(position), sharePurchaseDateArrayList.get(position), sharePurchasePrizeArrayList.get(position), shareRoundLotArrayList.get(position));
    				}
    				else if(shareTypeArrayList.get(position).equals("share"))
    				{
    					goToShareDetailsActivity(shareIsinArrayList.get(position), sharePurchaseDateArrayList.get(position), sharePurchasePrizeArrayList.get(position), shareRoundLotArrayList.get(position));
    				}
    			}
    		});
    	}
    	db.close();
    }
	
	//function that control if all the isin requested are returned...
	private boolean allIsinRequestedAreReturned(ArrayList<String> isinList, QuotationContainer container)
	{
		boolean result = false;
		
		ArrayList<String> support = new ArrayList<String>();
		for(Quotation_Bond qb : container.getBondList())
		{
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList())
		{
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList())
		{
			support.add(qs.getISIN());
		}
		
		for (int i = 0; i < isinList.size(); i++) 
		{
			if(support.contains(isinList.get(i)))
			{
				result = true;
			}
		}
		
		return result;
	}
	
	private void goToBondDetailsActivity(String bondIsin, String bondPurchaseDate, String bondPurchasePrize, String bondRoundLot)
    {
    	Intent i = new Intent(this, BondDetailsActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".bondIsin", bondIsin);
		i.putExtra(pkg+".bondPurchaseDate", bondPurchaseDate);
		i.putExtra(pkg+".bondPurchasePrize", bondPurchasePrize);
		i.putExtra(pkg+".bondRoundLot", bondRoundLot);
		startActivity(i);
    }
	
	private void goToFundDetailsActivity(String fundIsin, String fundPurchaseDate, String fundPurchasePrize, String fundRoundLot)
    {
    	Intent i = new Intent(this, FundDetailsActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".fundIsin", fundIsin);
		i.putExtra(pkg+".fundPurchaseDate", fundPurchaseDate);
		i.putExtra(pkg+".fundPurchasePrize", fundPurchasePrize);
		i.putExtra(pkg+".fundRoundLot", fundRoundLot);
		startActivity(i);
    }
	
	private void goToShareDetailsActivity(String shareIsin, String sharePurchaseDate, String sharePurchasePrize, String shareRoundLot)
    {
    	Intent i = new Intent(this, ShareDetailsActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".shareIsin", shareIsin);
		i.putExtra(pkg+".sharePurchaseDate", sharePurchaseDate);
		i.putExtra(pkg+".sharePurchasePrize", sharePurchasePrize);
		i.putExtra(pkg+".shareRoundLot", shareRoundLot);
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
				sharePurchaseDateArrayList.add(c.getString(3));
				sharePurchasePrizeArrayList.add(String.valueOf(c.getFloat(4)));
				shareRoundLotArrayList.add(String.valueOf(c.getInt(5)));
			} while (c.moveToNext());
		}
	}
	
	private void showMessage(String type, String message)
	{
		AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
    	alert_builder.setTitle(type);
    	alert_builder.setMessage(message);
    	alert_builder.setCancelable(false);
    	alert_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
    	AlertDialog message_empty = alert_builder.create();
    	message_empty.show();
	}
	
	private String getTodaysDate() 
	{
	    final Calendar c = Calendar.getInstance();
	    return(new StringBuilder()
	            .append(c.get(Calendar.MONTH) + 1).append("/")
	            .append(c.get(Calendar.DAY_OF_MONTH)).append("/")
	            .append(c.get(Calendar.YEAR)).append(" ")
	            .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
	            .append(c.get(Calendar.MINUTE)).append(":")
	            .append(c.get(Calendar.SECOND)).append(" ")).toString();
	}
	
	private void deleteSelectedTool(String ISIN, String type)
	{
		db.open();
		if(type.equals("bond")){
			db.deleteBondInTransitionTable(portfolioName, ISIN);			
		}
		else if (type.equals("fund")){
			db.deleteFundInTransitionTable(portfolioName, ISIN);
		}
		else if (type.equals("share")){
			db.deleteShareInTransitionTable(portfolioName, ISIN);
		}
		else{
			//error
		}
		db.close();
		return;
	}
	
	private class QuotationRequestAsyncTask extends
	AsyncTask<ArrayList<Request>, Void, QuotationContainer> {
		private ProgressDialog dialog;
		private Context context;
		
		public QuotationRequestAsyncTask(Context ctx)
		{
			this.context = ctx;
		}
		
		@Override
		protected QuotationContainer doInBackground(ArrayList<Request>... params) 
		{
			try {
				QuotationContainer quotCont = new QuotationContainer();
				
				Gson converter = new Gson();
				String jsonReq = converter.toJson(params[0]);
				String jsonResponse = ConnectionUtils.postData(jsonReq);
				if(jsonResponse != null)
				{
					quotCont = ResponseHandler.decodeQuotations(jsonResponse);
					return quotCont;
				}
				else
				{
					return null;
				}
			} catch (Exception e) {
				System.out.println("connection ERROR");
			}
			
			return null;
		}
		
		@Override
		protected void onPreExecute()
		{
			//load progress dialog....
			dialog = new ProgressDialog(this.context);
			dialog.setMessage("Loading, please wait...");
			dialog.show();
		}
		
		@Override
		protected void onPostExecute(QuotationContainer container)
		{
			db.open();
			
			
			//dismiss progress dialog....
			if(dialog.isShowing())
			{
				dialog.dismiss();
			}
			
			if(container!=null)
			{
				//2. control number of isin returned equals number of isin requested....
				int totalQuotationReturned = container.getBondList().size() + container.getFundList().size() + container.getShareList().size();
				if(totalQuotationReturned == listaIsinTmp.size())
				{
					//2.1 control that all isin requested are returned....
					if(allIsinRequestedAreReturned(listaIsinTmp, container))
					{
						//3. for all BOND returned...
						for(Quotation_Bond qb : container.getBondList())
						{
							//3.1 control if bond already exist in database --> UPDATE
							if(db.bondAlreadyInDatabase(qb.getISIN()))
							{
								//UPDATE
								
								try {
									db.updateSelectedBondByQuotationObject(qb, getTodaysDate());
								} catch (Exception e) {
									System.out.println("Database update error");
								}
							}
							else
							{
								//INSERT
								
								try {
									db.addNewBondByQuotationObject(qb, getTodaysDate());
								} catch (Exception e) {
									System.out.println("Database insert error");
								}	
							}
							
							//3.2 INSERT bond in transition table
							int index = listaIsinTmp.indexOf(qb.getISIN());
							try {
								db.addNewBondInTransitionTable(portfolioName, listaIsinTmp.get(index), 
										listaDataAcqTmp.get(index), Float.parseFloat(listaPrezzoAcqTmp.get(index)), Integer.parseInt(listaLottoTmp.get(index)));
							} catch (Exception e) {
								System.out.println("Database insert error [transition table]");
							}
						}
						
						//4. for all FUND returned...
						for(Quotation_Fund qf : container.getFundList())
						{
							//4.1 control if fund already exist in database --> UPDATE
							if(db.fundAlreadyInDatabase(qf.getISIN()))
							{
								//UPDATE
							}
							else
							{
								//INSERT
							}
							
							//4.2 INSERT fund in transition table
						}
						
						//5. for all SHARE returned...
						for(Quotation_Share qs : container.getShareList())
						{
							//5.1 control if share already exist in database --> UPDATE
							if(db.shareAlreadyInDatabase(qs.getISIN()))
							{
								//UPDATE
							}
							else
							{
								//INSERT
							}
							
							//5.2 INSERT share in transition table
						}
					}
					else
					{
						//error: all requested !are returned
						showMessage("Error", "Some tool requested is not returned.");
					}
				}
				else
				{
					//error: #returned != #requested
					showMessage("Error", "The number of tools returned is different from the ones requested.");
				}
			}
			else
			{
				//connection error!
				showMessage("Error", "There were errors during connection with server. Please try again.");
			}
			
			
			//save in database...
			//update view....
			updateView();
			
			
			db.close();
		}
	}
	
}


package it.dev;

import it.dev.MyFinanceDatabase.PortfolioBondMetadata;
import it.dev.MyFinanceDatabase.ShareMetaData;
import it.util.ConnectionUtils;
import it.util.ResponseHandler;
import it.util.UpdateTimeTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import Quotes.QuotationContainer;
import Quotes.QuotationType;
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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
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
	
	private ArrayList<ToolObject> toolLoadedByDatabase = new ArrayList<ToolObject>();
	
	private ArrayList<ToolObject> toolTmpToAddInDatabase = new ArrayList<ToolObject>();
	
	private TextView portfolioReferenceTextView;
	private TextView portfolioLastUpdate_TV;
	private ListView toolListView;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_list_activity);
        
        portfolioReferenceTextView = (TextView) findViewById(R.id.portfolioReferenceTextView);
        portfolioLastUpdate_TV = (TextView) findViewById(R.id.portfolioLastUpdate_TV);
        toolListView = (ListView) findViewById(R.id.toolListView);
        registerForContextMenu(toolListView);
        
        Intent intent = getIntent();
        String pkg = getPackageName();
        
        portfolioName = (String) intent.getStringExtra(pkg+".portfolioName");
        portfolioReferenceTextView.setText(portfolioName);
        
        db = new MyFinanceDatabase(this);
        
        setPortfolioLastUpdate();
        
        updateView();
		
		//CALL ASYNCTASK FOR UPDATE REQUEST...(when activity starts)
		if(toolLoadedByDatabase.size()!=0)
		{
			if(portfolioToUpdated(portfolioName)){
				updateToolsInPortfolio();
			}
			
			UpdateTimeTask.add(portfolioName);
		}
        
        
    }
	
	private void setPortfolioLastUpdate()
	{
		db.open();
		
		Cursor portfolio = db.getDetailsOfPortfolio(portfolioName);
		startManagingCursor(portfolio);
		
		if(portfolio.getCount()==1)
		{
			portfolio.moveToFirst();
			portfolioLastUpdate_TV.setText(portfolio.getString(4));
		}
		
		
		db.close();
	}
	
	public void onResume()
    {
		super.onResume();
		updateView();
    }
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(toolLoadedByDatabase.get(info.position).getISIN());
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_portfolio_context_menu, menu);
    }
	
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.edit_item:
        	showEditToolDialog(toolLoadedByDatabase.get(info.position).getISIN(), toolLoadedByDatabase.get(info.position).getType(), toolLoadedByDatabase.get(info.position).getPurchaseDate());
        	return true;            
        case R.id.remove_item:        	
        	deleteSelectedTool(toolLoadedByDatabase.get(info.position).getISIN(), toolLoadedByDatabase.get(info.position).getType(), toolLoadedByDatabase.get(info.position).getPurchaseDate());        	
        	return true;        
        }
        return false;
    }
	
	public void onContextMenuClosed(Menu menu)
    {    	
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
    		showAddNewToolDialog();
    		break;
    	case R.id.menu_manual_update:
    		//manual update...
    		if(portfolioToUpdated(portfolioName)){
    			updateToolsInPortfolio();
    		}
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	@SuppressWarnings("unchecked")
	private void updateToolsInPortfolio()
	{
		//0. create arrayList of QuotationType to generate the update request...
		ArrayList<QuotationType> typeArray = new ArrayList<QuotationType>();
		for (int i = 0; i < toolLoadedByDatabase.size(); i++) 
		{
			if(toolLoadedByDatabase.get(i).getType().equals("bond"))
			{
				typeArray.add(QuotationType.BOND);
			}
			else if(toolLoadedByDatabase.get(i).getType().equals("fund"))
			{
				typeArray.add(QuotationType.FUND);
			}
			else if(toolLoadedByDatabase.get(i).getType().equals("share"))
			{
				typeArray.add(QuotationType.SHARE);
			}
			else
			{
				System.out.println("Type error.");
			}
		}
		
		//1. create arrayList of Quotation Request....
		ArrayList<Request> array = new ArrayList<Request>();
		for (int i = 0; i < toolLoadedByDatabase.size(); i++) 
		{
			array.add(new Request(toolLoadedByDatabase.get(i).getISIN(), typeArray.get(i), toolLoadedByDatabase.get(i).getPreferredSite(), toolLoadedByDatabase.get(i).getIgnoredSites()));
		}
		
		//2. CALL ASYNCTASK TO GET DATA FROM SERVER....
		UpdateRequestAsyncTask asyncTask0 = new UpdateRequestAsyncTask(ToolListActivity.this);
		asyncTask0.execute(array);
	}
	
	//Open the custom alert dialog where it is possible to edit a saved tool.
	private void showEditToolDialog(String isin, String type, String purchaseDate)
	{
		final Dialog editToolDialog = new Dialog(ToolListActivity.this);
		editToolDialog.setContentView(R.layout.custom_edit_selected_tool_dialog);
		editToolDialog.setTitle(isin);
		editToolDialog.setCancelable(true);
		
		final String risin = isin;
		final String rtype = type;
		
		final TextView isinRef_TV = (TextView) editToolDialog.findViewById(R.id.isinRef_TV);
		final TextView previousDate_TV = (TextView) editToolDialog.findViewById(R.id.previousDate_TV);
		final DatePicker edit_purchaseDateDatePicker = (DatePicker) editToolDialog.findViewById(R.id.edit_purchaseDateDatePicker);
		final EditText edit_buyPriceEditText = (EditText) editToolDialog.findViewById(R.id.edit_buyPriceEditText);
		final EditText edit_roundLotEditText = (EditText) editToolDialog.findViewById(R.id.edit_roundLotEditText);
		Button undoEditToolButton = (Button) editToolDialog.findViewById(R.id.undoEditToolButton);
		Button finishEditToolButton = (Button) editToolDialog.findViewById(R.id.finishEditToolButton);
		
		isinRef_TV.setText(isin);
		previousDate_TV.setText(purchaseDate);
		
		undoEditToolButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				editToolDialog.dismiss();
			}
		});
		
		finishEditToolButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(edit_buyPriceEditText.getText().length()!=0 && edit_roundLotEditText.getText().length()!=0)
				{
					db.open();
					
					String purchaseDate = String.valueOf(edit_purchaseDateDatePicker.getDayOfMonth()) + "/" + String.valueOf(edit_purchaseDateDatePicker.getMonth()) + "/" + String.valueOf(edit_purchaseDateDatePicker.getYear());
					
					if(rtype.equals("bond"))
					{
						db.updateSelectedBondInTransitionTable(portfolioName, risin, previousDate_TV.getText().toString(), purchaseDate, Float.parseFloat(edit_buyPriceEditText.getText().toString()), Integer.parseInt(edit_roundLotEditText.getText().toString()));
					}
					else if(rtype.equals("fund"))
					{
						db.updateSelectedFundInTransitionTable(portfolioName, risin, previousDate_TV.getText().toString(), purchaseDate, Float.parseFloat(edit_buyPriceEditText.getText().toString()), Integer.parseInt(edit_roundLotEditText.getText().toString()));
					}
					else if(rtype.equals("share"))
					{
						db.updateSelectedShareInTransitionTable(portfolioName, risin, previousDate_TV.getText().toString(), purchaseDate, Float.parseFloat(edit_buyPriceEditText.getText().toString()), Integer.parseInt(edit_roundLotEditText.getText().toString()));
					}
					
					db.close();
					editToolDialog.dismiss();
				}
				else
				{
					showMessage("Error", "Control that you have insert all the data.");
				}
			}
		});
		
		db.open();
		Cursor toolOverview;
		
		if(type.equals("bond"))
		{
			toolOverview = db.getSpecificBondOverviewInPortfolio(portfolioName, isin, purchaseDate);
		}
		else if(type.equals("fund"))
		{
			toolOverview = db.getSpecificFundOverviewInPortfolio(portfolioName, isin, purchaseDate);
		}
		else if(type.equals("share"))
		{
			toolOverview = db.getSpecificShareOverviewInPortfolio(portfolioName, isin, purchaseDate);
		}
		else
		{
			toolOverview = null;
			System.out.println("type error");
			showMessage("Error", "Non dovrebbe mai accadere...");
			editToolDialog.dismiss();
		}
		
		startManagingCursor(toolOverview);
		if(toolOverview!=null)
		{
			if(toolOverview.getCount()==1)
			{
				toolOverview.moveToFirst();
				edit_buyPriceEditText.setText(String.valueOf(toolOverview.getFloat(4)));
				edit_roundLotEditText.setText(String.valueOf(toolOverview.getInt(5)));
			}
		}
		db.close();
		
		editToolDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				updateView();
			}
		});
		
		
		editToolDialog.show();
		
	}
	
	//Open the custom alert dialog where it is possible to add a new tool.
	private void showAddNewToolDialog()
	{
		//initilize arraylists...
		toolTmpToAddInDatabase.clear();
		
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
					// String -> Uppercase -> cut spaces and get first element
					String purchaseDate = String.valueOf(purchaseDateDatePicker.getDayOfMonth()) + "/" + String.valueOf(purchaseDateDatePicker.getMonth()) + "/" + String.valueOf(purchaseDateDatePicker.getYear());
					
					toolTmpToAddInDatabase.add(new ToolObject(shareISINEditText.getText().toString().toUpperCase().split(" ")[0], 
							"", purchaseDate, buyPriceEditText.getText().toString(), roundLotEditText.getText().toString()));
					
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
					// String -> Uppercase -> cut spaces and get first element
					String purchaseDate = String.valueOf(purchaseDateDatePicker.getDayOfMonth()) + "/" + String.valueOf(purchaseDateDatePicker.getMonth()+1) + "/" + String.valueOf(purchaseDateDatePicker.getYear());
					
					toolTmpToAddInDatabase.add(new ToolObject(shareISINEditText.getText().toString().toUpperCase().split(" ")[0], 
							"", purchaseDate, buyPriceEditText.getText().toString(), roundLotEditText.getText().toString()));
					
					//1. create arrayList of Quotation Request....
					ArrayList<Request> array = new ArrayList<Request>();
					for (int i = 0; i < toolTmpToAddInDatabase.size(); i++) 
					{
						array.add(new Request(toolTmpToAddInDatabase.get(i).getISIN()));
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
		toolLoadedByDatabase.clear();
		
    	db.open();
    	
    	Cursor c_bond = db.getAllBondOverviewInPortfolio(portfolioName);
    	startManagingCursor(c_bond);
    	c_bond.moveToFirst();
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
    				goToToolDetailsActivity(toolLoadedByDatabase.get(position).getISIN(), toolLoadedByDatabase.get(position).getType(), toolLoadedByDatabase.get(position).getPurchaseDate(), toolLoadedByDatabase.get(position).getPurchasePrice(), toolLoadedByDatabase.get(position).getRoundLot());
    			}
    		});
    	}
    	
    	Cursor details;
    	
    	ArrayList<String> ignoredSites = new ArrayList<String>();
    	
    	for (int i = 0; i < toolLoadedByDatabase.size(); i++) 
    	{
    		ignoredSites.clear();
    		
			if(toolLoadedByDatabase.get(i).getType().equals("bond"))
			{
				details = db.getBondDetails(toolLoadedByDatabase.get(i).getISIN());
				startManagingCursor(details);
				if(details.getCount()==1)
				{
					details.moveToFirst();
					toolLoadedByDatabase.get(i).setPreferredSite(details.getString(29));
					String[] array = details.getString(30).split(" ");
					
					for (String string : array) 
					{
						ignoredSites.add(string);
					}
					
					toolLoadedByDatabase.get(i).setIgnoredSites(ignoredSites);
					
				}
			}
			else if(toolLoadedByDatabase.get(i).getType().equals("fund"))
			{
				details = db.getFundDetails(toolLoadedByDatabase.get(i).getISIN());
				startManagingCursor(details);
				if(details.getCount()==1)
				{
					details.moveToFirst();
					toolLoadedByDatabase.get(i).setPreferredSite(details.getString(18));
					String[] array = details.getString(19).split(" ");
					
					for (String string : array) 
					{
						ignoredSites.add(string);
					}
					
					toolLoadedByDatabase.get(i).setIgnoredSites(ignoredSites);
				}
			}
			else if(toolLoadedByDatabase.get(i).getType().equals("share"))
			{
				details = db.getShareDetails(toolLoadedByDatabase.get(i).getISIN());
				startManagingCursor(details);
				if(details.getCount()==1)
				{
					details.moveToFirst();
					toolLoadedByDatabase.get(i).setPreferredSite(details.getString(25));
					String[] array = details.getString(26).split(" ");
					
					for (String string : array) 
					{
						ignoredSites.add(string);
					}
					
					toolLoadedByDatabase.get(i).setIgnoredSites(ignoredSites);
				}
			}
		}
    	
    	
    	db.close();
    }
	
	//function that control if all the isin requested are returned...
	private boolean allIsinRequestedAreReturned(ArrayList<ToolObject> toolList, QuotationContainer container)
	{
		boolean result = false;
		
		ArrayList<String> support = new ArrayList<String>();
		ArrayList<String> isinList = new ArrayList<String>();
		
		for(ToolObject obj : toolList)
		{
			isinList.add(obj.getISIN());
		}
		
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
	
	//function that returns an array list of the Isin returned by server but not requested by client...
	private ArrayList<String> searchIsinNotRequested(ArrayList<ToolObject> toolList, QuotationContainer container)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		ArrayList<String> support = new ArrayList<String>();
		ArrayList<String> isinList = new ArrayList<String>();
		
		for(ToolObject obj : toolList)
		{
			isinList.add(obj.getISIN());
		}
		
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
		
		for (int i = 0; i < support.size(); i++) 
		{
			if(!isinList.contains(support.get(i)))
			{
				result.add(support.get(i));
			}
		}
		
		return result;
	}
	
	//function that returns an array list of the Isin not returned by server but requested by client...
	private ArrayList<ToolObject> searchIsinNotReturned(ArrayList<ToolObject> toolList, QuotationContainer container)
	{
		ArrayList<ToolObject> result = new ArrayList<ToolObject>();
		
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
		
		for (int i = 0; i < toolList.size(); i++) 
		{
			if(!support.contains(toolList.get(i).getISIN()))
			{
				result.add(toolList.get(i));
			}
		}
		
		return result;
	}
	
	private void goToToolDetailsActivity(String toolIsin, String toolType, String toolPurchaseDate, String toolPurchasePrize, String toolRoundLot)
	{
		Intent i = new Intent(this, ToolDetailsActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".toolIsin", toolIsin);
		i.putExtra(pkg+".toolType", toolType);
		i.putExtra(pkg+".toolPurchaseDate", toolPurchaseDate);
		i.putExtra(pkg+".toolPurchasePrize", toolPurchasePrize);
		i.putExtra(pkg+".toolRoundLot", toolRoundLot);
		startActivity(i);
		
	}
	
	private void saveSharesFromCursor(Cursor c, String type)
	{
		if(c.getCount()!=0)
		{
			c.moveToFirst();
			do {
				
				toolLoadedByDatabase.add(new ToolObject(c.getString(2), type, c.getString(3), String.valueOf(c.getFloat(4)), String.valueOf(c.getInt(5))));
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
	
	private void deleteSelectedTool(String ISIN, String type, String purchaseDate)
	{
		db.open();
		if(type.equals("bond")){
			db.deleteBondInTransitionTable(portfolioName, ISIN, purchaseDate);
			if(!db.bondAlreadyInDatabase(ISIN))db.deleteBond(ISIN);
		}
		else if (type.equals("fund")){
			db.deleteFundInTransitionTable(portfolioName, ISIN, purchaseDate);
			if(!db.fundAlreadyInDatabase(ISIN))db.deleteFund(ISIN);
		}
		else if (type.equals("share")){
			db.deleteShareInTransitionTable(portfolioName, ISIN, purchaseDate);
			if(!db.shareAlreadyInDatabase(ISIN))db.deleteShare(ISIN);
		}
		else{
			System.out.println("type error");
		}
		db.close();
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
					System.out.println("risposta non nulla....");
					quotCont = ResponseHandler.decodeQuotations(jsonResponse);
					return quotCont;
				}
				else
				{
					System.out.println("risposta nulla");
					return null;
				}
			} catch (Exception e) {
				System.out.println("connection ERROR");
			}
			System.out.println("ritorno null");
			return null;
		}
		
		@Override
		protected void onPreExecute()
		{
			//load progress dialog....
			dialog = new ProgressDialog(this.context);
			dialog.setMessage("Loading, contacting server for data");
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
				int totalQuotationReturned = container.getBondList().size() + container.getFundList().size() + container.getShareList().size();
				
				if(allIsinRequestedAreReturned(toolTmpToAddInDatabase, container))
				{
					if(totalQuotationReturned != toolTmpToAddInDatabase.size())
					{
						//ne ho ricevuti di più rispetto a quelli richiesti....
						System.out.println("ne ho ricevuti di più rispetto a quelli richiesti....");
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(toolTmpToAddInDatabase, container);
						for (int i = 0; i < listaIsinNotRequested.size(); i++) 
						{
							System.out.println(listaIsinNotRequested.get(i));							
						}
						
						//elimino quelli non richiesti dal container...
						System.out.println("elimino quelli non richiesti dal container...");
						for (int i = 0; i < listaIsinNotRequested.size(); i++) 
						{
							for(Quotation_Bond qb : container.getBondList())
							{
								if(qb.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getBondList().remove(qb);
								}
							}
							for(Quotation_Fund qf : container.getFundList())
							{
								if(qf.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getFundList().remove(qf);
								}
							}
							for(Quotation_Share qs : container.getShareList())
							{
								if(qs.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getShareList().remove(qs);
								}
							}
						}
					}
				}
				else
				{
					//alcuni di quelli richiesti non sono stati tornati....
					System.out.println("alcuni di quelli richiesti non sono stati tornati....");
					ArrayList<ToolObject> listaIsinNotReturned = searchIsinNotReturned(toolTmpToAddInDatabase, container);
					for (int i = 0; i < listaIsinNotReturned.size(); i++) 
					{
						System.out.println(listaIsinNotReturned.get(i));
						showMessage("Info", listaIsinNotReturned.get(i)+" is not returned by Server");
					}
					
					//rimuovo dalla lista dei tool che devo inserire nel DB quei tool che non vengono restituiti...
					for(ToolObject obj : listaIsinNotReturned)
					{
						toolTmpToAddInDatabase.remove(obj);
					}
					
					
					
				}
				
				
				//SAVE IN DATABASE <BOND/FUND/SHARE> OF 'container'
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
				}
				
				//4. for all FUND returned...
				for(Quotation_Fund qf : container.getFundList())
				{
					//4.1 control if fund already exist in database --> UPDATE
					if(db.fundAlreadyInDatabase(qf.getISIN()))
					{
//						//UPDATE
						try {
							db.updateSelectedFundByQuotationObject(qf, getTodaysDate());
						} catch (Exception e) {
							System.out.println("Database update error");
						}
					}
					else
					{
//						//INSERT
						try {
							db.addNewFundByQuotationObject(qf, getTodaysDate());
						} catch (Exception e) {
							System.out.println("Database insert error");
						}	
					}
				}
				
				//5. for all SHARE returned...
				for(Quotation_Share qs : container.getShareList())
				{
					//5.1 control if share already exist in database --> UPDATE
					if(db.shareAlreadyInDatabase(qs.getISIN()))
					{
//						//UPDATE
						try {
							db.updateSelectedShareByQuotationObject(qs, getTodaysDate());
						} catch (Exception e) {
							System.out.println("Database update error");
						}
					}
					else
					{
//						//INSERT
						try {
							db.addNewShareByQuotationObject(qs, getTodaysDate());
						} catch (Exception e) {
							System.out.println("Database insert error");
						}	
					}
				}
				
				System.out.println("total to add: "+toolTmpToAddInDatabase.size());
				
				//6. save all returned BOND/FUND/SHARE in transition table...
				for (int i = 0; i < toolTmpToAddInDatabase.size(); i++) 
				{
					for(Quotation_Bond qb : container.getBondList())
					{
						if(toolTmpToAddInDatabase.get(i).getISIN().equals(qb.getISIN()))
						{
							toolTmpToAddInDatabase.get(i).setType("bond");
						}
					}
					
					for(Quotation_Fund qf : container.getFundList())
					{
						if(toolTmpToAddInDatabase.get(i).getISIN().equals(qf.getISIN()))
						{
							toolTmpToAddInDatabase.get(i).setType("fund");
						}
					}
					
					for(Quotation_Share qs : container.getShareList())
					{
						if(toolTmpToAddInDatabase.get(i).getISIN().equals(qs.getISIN()))
						{
							toolTmpToAddInDatabase.get(i).setType("share");
						}
					}
					
					
					if(toolTmpToAddInDatabase.get(i).getType().equals("bond"))
					{
						db.addNewBondInTransitionTable(portfolioName, toolTmpToAddInDatabase.get(i).getISIN(), toolTmpToAddInDatabase.get(i).getPurchaseDate(), 
								Float.parseFloat(toolTmpToAddInDatabase.get(i).getPurchasePrice()), Integer.parseInt(toolTmpToAddInDatabase.get(i).getRoundLot()));
					}
					else if(toolTmpToAddInDatabase.get(i).getType().equals("fund"))
					{
						db.addNewFundInTransitionTable(portfolioName, toolTmpToAddInDatabase.get(i).getISIN(), toolTmpToAddInDatabase.get(i).getPurchaseDate(), 
								Float.parseFloat(toolTmpToAddInDatabase.get(i).getPurchasePrice()), Integer.parseInt(toolTmpToAddInDatabase.get(i).getRoundLot()));
					}
					else if(toolTmpToAddInDatabase.get(i).getType().equals("share"))
					{
						db.addNewShareInTransitionTable(portfolioName, toolTmpToAddInDatabase.get(i).getISIN(), toolTmpToAddInDatabase.get(i).getPurchaseDate(), 
								Float.parseFloat(toolTmpToAddInDatabase.get(i).getPurchasePrice()), Integer.parseInt(toolTmpToAddInDatabase.get(i).getRoundLot()));
					}
					else
					{
						System.out.println("non dovrebbe mai accadere che non trovo il tipo...");
					}
					
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
	
	
	
	
	//IP. chiamata asincrona per effettuare una request UPDATE! 
	private class UpdateRequestAsyncTask extends AsyncTask<ArrayList<Request>, Void, QuotationContainer>
	{
		private ProgressDialog dialog;
		private Context context;
		
		public UpdateRequestAsyncTask(Context ctx)
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
			dialog.setMessage("Loading, update data from server");
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
				
				int totalQuotationReturned = container.getBondList().size() + container.getFundList().size() + container.getShareList().size();
				
				if(allIsinRequestedAreReturned(toolLoadedByDatabase, container))
				{
					if(totalQuotationReturned != toolLoadedByDatabase.size())
					{
						//ne ho ricevuti di più rispetto a quelli richiesti....
						System.out.println("ne ho ricevuti di più rispetto a quelli richiesti....");
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(toolLoadedByDatabase, container);
						for (int i = 0; i < listaIsinNotRequested.size(); i++) 
						{
							System.out.println(listaIsinNotRequested.get(i));							
						}
						
						//elimino quelli non richiesti dal container...
						System.out.println("elimino quelli non richiesti dal container...");
						for (int i = 0; i < listaIsinNotRequested.size(); i++) 
						{
							for(Quotation_Bond qb : container.getBondList())
							{
								if(qb.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getBondList().remove(qb);
								}
							}
							for(Quotation_Fund qf : container.getFundList())
							{
								if(qf.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getFundList().remove(qf);
								}
							}
							for(Quotation_Share qs : container.getShareList())
							{
								if(qs.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getShareList().remove(qs);
								}
							}
						}
					}
				}
				else
				{
					//alcuni di quelli richiesti non sono stati tornati....
					System.out.println("alcuni di quelli richiesti non sono stati tornati....");
					ArrayList<ToolObject> listaIsinNotReturned = searchIsinNotReturned(toolLoadedByDatabase, container);
					for (int i = 0; i < listaIsinNotReturned.size(); i++) 
					{
						System.out.println(listaIsinNotReturned.get(i));
						showMessage("Info", listaIsinNotReturned.get(i)+" is not returned by Server");
					}
				}
				
				//UPDATE IN DATABASE <BOND/FUND/SHARE> OF 'container'
				for(Quotation_Bond qb : container.getBondList())
				{
					try {
						db.updateSelectedBondByQuotationObject(qb, getTodaysDate());
					} catch (Exception e) {
						System.out.println("Database update error");
					}
				}
				
				//4. for all FUND returned...
				for(Quotation_Fund qf : container.getFundList())
				{
//					//4.1 control if fund already exist in database --> UPDATE
//					//UPDATE
				}
				
				//5. for all SHARE returned...
				for(Quotation_Share qs : container.getShareList())
				{
//					//5.1 control if share already exist in database --> UPDATE
//					//UPDATE
				}
				
				//update portfolio lastupdate field...
				db.updateSelectedPortfolioLastUpdate(portfolioName, getTodaysDate());
				
			}
			else
			{
				//connection error!
				showMessage("Error", "There were errors during connection with server. Please try again.");
			}
			
			
			updateView();
			
			setPortfolioLastUpdate();
			
			db.close();
		}
	}
	
	private boolean portfolioToUpdated(String portfolioName)
	{
		db.open();
		GregorianCalendar today = (GregorianCalendar) Calendar.getInstance();
		GregorianCalendar upDate = (GregorianCalendar) Calendar.getInstance();
		today.add(Calendar.MINUTE, -30);
		Cursor c = db.getDetailsOfPortfolio(portfolioName);
		c.moveToFirst();
		String updateDate = c.getString(4);
		c.close();
		String[] updateString	= updateDate.split("[/: ]");
		upDate.set(Integer.parseInt(updateString[2]), Integer.parseInt(updateString[1])-1, Integer.parseInt(updateString[0]), Integer.parseInt(updateString[3]), Integer.parseInt(updateString[4]), Integer.parseInt(updateString[5]));
		db.close();
		if(today.after(upDate))return true;
		else return false;
		
	}
	
}


package it.dev;

import it.dev.MyFinanceDatabase.PortfolioBondMetadata;
import it.dev.MyFinanceDatabase.ShareMetaData;
import it.util.ConnectionUtils;
import it.util.ResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;

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
	
	private ArrayList<String> shareIsinArrayList = new ArrayList<String>();
	private ArrayList<String> shareTypeArrayList = new ArrayList<String>();
	private ArrayList<String> sharePurchaseDateArrayList = new ArrayList<String>();
	private ArrayList<String> sharePurchasePrizeArrayList = new ArrayList<String>();
	private ArrayList<String> shareRoundLotArrayList = new ArrayList<String>();
	
	private TextView portfolioReferenceTextView;
	private TextView portfolioLastUpdate_TV;
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
		if(shareIsinArrayList.size()!=0)
		{
			updateToolsInPortfolio();
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
        menu.setHeaderTitle(shareIsinArrayList.get(info.position));
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_portfolio_context_menu, menu);
    }
	
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.edit_item:
        	showEditToolDialog(shareIsinArrayList.get(info.position), shareTypeArrayList.get(info.position), sharePurchaseDateArrayList.get(info.position));
        	return true;            
        case R.id.remove_item:        	
        	deleteSelectedTool(shareIsinArrayList.get(info.position), shareTypeArrayList.get(info.position), sharePurchaseDateArrayList.get(info.position));        	
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
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	@SuppressWarnings("unchecked")
	private void updateToolsInPortfolio()
	{
		//0. create arrayList of QuotationType to generate the update request...
		ArrayList<QuotationType> typeArray = new ArrayList<QuotationType>();
		for (int i = 0; i < shareTypeArrayList.size(); i++) 
		{
			if(shareTypeArrayList.get(i).equals("bond"))
			{
				typeArray.add(QuotationType.BOND);
			}
			else if(shareTypeArrayList.get(i).equals("fund"))
			{
				typeArray.add(QuotationType.FUND);
			}
			else if(shareTypeArrayList.get(i).equals("share"))
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
		for (int i = 0; i < shareIsinArrayList.size(); i++) 
		{
			array.add(new Request(shareIsinArrayList.get(i), typeArray.get(i), "__NONE__"));
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
    					goToToolDetailsActivity(shareIsinArrayList.get(position), shareTypeArrayList.get(position), sharePurchaseDateArrayList.get(position), sharePurchasePrizeArrayList.get(position), shareRoundLotArrayList.get(position));
    					//goToBondDetailsActivity(shareIsinArrayList.get(position), sharePurchaseDateArrayList.get(position), sharePurchasePrizeArrayList.get(position), shareRoundLotArrayList.get(position));
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
	
	//function that returns an array list of the Isin returned by server but not requested by client...
	private ArrayList<String> searchIsinNotRequested(ArrayList<String> isinList, QuotationContainer container)
	{
		ArrayList<String> result = new ArrayList<String>();
		
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
	private ArrayList<String> searchIsinNotReturned(ArrayList<String> isinList, QuotationContainer container)
	{
		ArrayList<String> result = new ArrayList<String>();
		
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
			if(!support.contains(isinList.get(i)))
			{
				result.add(isinList.get(i));
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
	
	private void deleteSelectedTool(String ISIN, String type, String purchaseDate)
	{
		db.open();
		if(type.equals("bond")){
			db.deleteBondInTransitionTable(portfolioName, ISIN, purchaseDate);			
		}
		else if (type.equals("fund")){
			db.deleteFundInTransitionTable(portfolioName, ISIN, purchaseDate);
		}
		else if (type.equals("share")){
			db.deleteShareInTransitionTable(portfolioName, ISIN, purchaseDate);
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
				
				if(allIsinRequestedAreReturned(listaIsinTmp, container))
				{
					if(totalQuotationReturned != listaIsinTmp.size())
					{
						//ne ho ricevuti di più rispetto a quelli richiesti....
						System.out.println("ne ho ricevuti di più rispetto a quelli richiesti....");
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(listaIsinTmp, container);
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
					ArrayList<String> listaIsinNotReturned = searchIsinNotReturned(listaIsinTmp, container);
					for (int i = 0; i < listaIsinNotReturned.size(); i++) 
					{
						System.out.println(listaIsinNotReturned.get(i));
						showMessage("Info", listaIsinNotReturned.get(i)+" is not returned by Server");
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
				
				if(allIsinRequestedAreReturned(shareIsinArrayList, container))
				{
					if(totalQuotationReturned != shareIsinArrayList.size())
					{
						//ne ho ricevuti di più rispetto a quelli richiesti....
						System.out.println("ne ho ricevuti di più rispetto a quelli richiesti....");
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(shareIsinArrayList, container);
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
					ArrayList<String> listaIsinNotReturned = searchIsinNotReturned(shareIsinArrayList, container);
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
					//4.1 control if fund already exist in database --> UPDATE
					//UPDATE
				}
				
				//5. for all SHARE returned...
				for(Quotation_Share qs : container.getShareList())
				{
					//5.1 control if share already exist in database --> UPDATE
					//UPDATE
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
	
}


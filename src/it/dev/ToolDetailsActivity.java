package it.dev;

import it.util.ConnectionUtils;
import it.util.ResponseHandler;

import java.io.IOException;
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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

public class ToolDetailsActivity extends Activity 
{
	private MyFinanceDatabase db;
	private SupportDatabaseHelper supportDatabase = new SupportDatabaseHelper(this);

	private TextView toolReferenceTextView;
	private TextView tool_purchaseDate_TV;
	private TextView tool_purchasePrize_TV;
	private TextView tool_roundLot_TV;
	private TableLayout dynamic_detail_table;

	private String toolIsin;
	private String toolType;
	
	private String preferredSite;
	private ArrayList<String> ignoredSites = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_details);

		toolReferenceTextView = (TextView) findViewById(R.id.toolReferenceTextView);
		tool_purchaseDate_TV = (TextView) findViewById(R.id.tool_purchaseDate_TV);
		tool_purchasePrize_TV = (TextView) findViewById(R.id.tool_purchasePrize_TV);
		tool_roundLot_TV = (TextView) findViewById(R.id.tool_roundLot_TV);
		dynamic_detail_table = (TableLayout) findViewById(R.id.dynamic_detail_table);

		Intent intent = getIntent();
		String pkg = getPackageName();

		toolIsin = (String) intent.getStringExtra(pkg+".toolIsin");
		toolType = (String) intent.getStringExtra(pkg+".toolType");

		toolReferenceTextView.setText(toolIsin);
		tool_purchaseDate_TV.setText((String) intent.getStringExtra(pkg+".toolPurchaseDate"));
		tool_purchasePrize_TV.setText((String) intent.getStringExtra(pkg+".toolPurchasePrize"));
		tool_roundLot_TV.setText((String) intent.getStringExtra(pkg+".toolRoundLot"));

		db = new MyFinanceDatabase(this);
		
		try 
        {
        	supportDatabase.createDataBase();
 
        } catch (IOException ioe) 
        {
        	throw new Error("Unable to create database");
        }

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
			callForcedUpdate();
			break;
		case R.id.menu_advanced_settings:
			showAdvancedSettingsDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	//load data from database and create layout...
	private void updateView()
	{
		db.open();
		supportDatabase.openDataBase();

		Cursor toolDetails;
		Cursor toolTranslate;

		if(toolType.equals("bond"))
		{
			toolDetails = db.getBondDetails(toolIsin);
			toolTranslate = supportDatabase.getBondTranslation("italiano");
		}
		else if(toolType.equals("fund"))
		{
			toolDetails = db.getFundDetails(toolIsin);
			toolTranslate = supportDatabase.getFundTranslation("italiano");
		}
		else if(toolType.equals("share"))
		{
			toolDetails = db.getShareDetails(toolIsin);
			toolTranslate = supportDatabase.getShareTranslation("italiano");
		}
		else
		{
			return;
		}

		startManagingCursor(toolDetails);
		startManagingCursor(toolTranslate);

		if(toolDetails.getCount()==1)
		{
			toolDetails.moveToFirst();
			toolTranslate.moveToFirst();
			int j = 2;
			for (int i = 1; i < toolDetails.getColumnCount(); i++) 
			{
				LayoutInflater inflater = getLayoutInflater();

				TableRow newRow = (TableRow) inflater.inflate(R.layout.tool_details_row, dynamic_detail_table, false);
				if(i%2==0);
				else
				{
					newRow.setBackgroundColor(Color.parseColor("#BDEEF9"));
				}

				TextView key = (TextView) newRow.findViewById(R.id.key_entry);
				TextView value = (TextView) newRow.findViewById(R.id.value_entry);

				key.setTextColor(Color.BLACK);
				key.setText(toolTranslate.getString(j));
				
				try	{
					value.setText(toolDetails.getString(i));
				}
				catch(Exception e){
					try{
						value.setText(""+toolDetails.getInt(i));
					}
					catch(Exception e1)	{
						try{
						value.setText(""+toolDetails.getFloat(i));
						}
						catch(Exception e2){
							System.out.println("error");
						}
					}
				}
				value.setTextColor(Color.BLACK);
				
				dynamic_detail_table.addView(newRow);
				j++;
			}
		}
		db.close();
		supportDatabase.close();
	}
	
	@SuppressWarnings("unchecked")
	private void callForcedUpdate()
	{
		ignoredSites.clear();
		db.open();
		QuotationType qType;
		Cursor details;
		
		if(toolType.equals("bond"))
		{
			qType = QuotationType.BOND;
			details = db.getBondDetails(toolIsin);
		}
		else if(toolType.equals("fund"))
		{
			qType = QuotationType.FUND;
			details = db.getFundDetails(toolIsin);
		}
		else if(toolType.equals("share"))
		{
			qType = QuotationType.SHARE;
			details = db.getShareDetails(toolIsin);
		}
		else
		{
			return;
		}
		
		startManagingCursor(details);
		if(details.getCount()==1)
		{
			details.moveToFirst();
			
			//add preferred site...
			String tmp = details.getString(details.getColumnIndex("sitoPreferito"));
			if(tmp.equals(""))
			{
				preferredSite = "__NONE__";
			}
			else
			{
				preferredSite = details.getString(details.getColumnIndex("sitoPreferito"));;
			}
			 
			
			//add ignored sites already saved in database...
			String[] array = details.getString(details.getColumnIndex("sitiIgnorati")).split(" ");
			
			for (String string : array) 
			{
				if(!string.equals(""))
				{
					ignoredSites.add(string);
					System.out.println("sito ignorato: "+string);
				}
			}
			
			//add source site...
			ignoredSites.add(details.getString(details.getColumnIndex("sitoSorgente")));
		}
		
		//1. create arrayList of Quotation Request....
		ArrayList<Request> array = new ArrayList<Request>();
		array.add(new Request(toolIsin, qType, preferredSite, ignoredSites));
		
		
		//2. CALL ASYNCTASK TO GET DATA FROM SERVER....
		ForcedRequestAsyncTask asyncTask1 = new ForcedRequestAsyncTask(ToolDetailsActivity.this);
		asyncTask1.execute(array);
		
		
		
		db.close();
	}
	
	//this method open the dialog for advanced settings...
	private void showAdvancedSettingsDialog()
	{
		final Dialog advancedOptionsDialog = new Dialog(ToolDetailsActivity.this);
		advancedOptionsDialog.setContentView(R.layout.custom_advanced_options_dialog);
		advancedOptionsDialog.setTitle(toolIsin);
		advancedOptionsDialog.setCancelable(true);
		
		final CheckBox prefSite_CB = (CheckBox) advancedOptionsDialog.findViewById(R.id.prefSite_CB);
		final TextView preferredSiteRef = (TextView) advancedOptionsDialog.findViewById(R.id.preferredSiteRef);
		final TableLayout dynamic_ignoredSites_table = (TableLayout) advancedOptionsDialog.findViewById(R.id.dynamic_ignoredSites_table);
		
		db.open();
		
		Cursor toolDetails;
		if(toolType.equals("bond"))
		{
			toolDetails = db.getBondDetails(toolIsin);
		}
		else if(toolType.equals("fund"))
		{
			toolDetails = db.getFundDetails(toolIsin);
		}
		else if(toolType.equals("share"))
		{
			toolDetails = db.getShareDetails(toolIsin);
		}
		else
		{
			toolDetails = null;
		}
		startManagingCursor(toolDetails);
		
		
		//add preferred site...
		if(toolDetails!=null)
		{
			if(toolDetails.getCount()==1)
			{
				toolDetails.moveToFirst();
				preferredSiteRef.setText(toolDetails.getString(toolDetails.getColumnIndex("sitoSorgente")));
			}
		}
		
		
		
		//add rows for ignored sites...
		//1. all sites that do not find this type of tools...
		
		
		
		
		//2. all sites saved in database for this tool...
		
		String[] array = null;
		
		if(toolDetails!=null)
		{
			if(toolDetails.getCount()==1)
			{
				toolDetails.moveToFirst();
				array = toolDetails.getString(toolDetails.getColumnIndex("sitiIgnorati")).split(" ");
			}
		}
		
		if(array!=null)
		{
			for (String string : array) 
			{
				LayoutInflater inflater = getLayoutInflater();
				
				TableRow newRow = (TableRow) inflater.inflate(R.layout.advanced_options_row, dynamic_ignoredSites_table, false);
				
				CheckBox ignored_cb = (CheckBox) newRow.findViewById(R.id.ignoredSite_CB);
				TextView ignored_tv = (TextView) newRow.findViewById(R.id.ignoredSite_TV);
				
				ignored_tv.setText(string);
				
				
				dynamic_ignoredSites_table.addView(newRow);
			}
		}
		
		
		
		
		
		
		
		
		
		db.close();
		advancedOptionsDialog.show();
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
	
	private boolean isinRequestedIsReturned(String isinRequested, QuotationContainer container)
	{
		boolean result = false;
		
		for(Quotation_Bond qb : container.getBondList())
		{
			if(qb.getISIN().equals(isinRequested))
			{
				result = true;
			}
		}
		for(Quotation_Fund qf : container.getFundList())
		{
			if(qf.getISIN().equals(isinRequested))
			{
				result = true;
			}
		}
		for(Quotation_Share qs : container.getShareList())
		{
			if(qs.getISIN().equals(isinRequested))
			{
				result = true;
			}
		}
		
		return result;
		
	}
	
	private ArrayList<String> searchIsinNotRequested(String toolIsin, QuotationContainer container)
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
			if(!support.get(i).equals(toolIsin))
			{
				result.add(support.get(i));
			}
		}
		
		return result;
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
	
	private class ForcedRequestAsyncTask extends AsyncTask<ArrayList<Request>, Void, QuotationContainer>
	{
		private ProgressDialog dialog;
		private Context context;
		
		public ForcedRequestAsyncTask(Context ctx)
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
				System.out.println(""+jsonReq);
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
			dialog.setMessage("Loading, forced update");
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
				
				System.out.println("total returned: "+totalQuotationReturned);
				
				if(isinRequestedIsReturned(toolIsin, container))
				{
					if(totalQuotationReturned > 1)
					{
						//ne ho ricevuti di più rispetto a quello richiesto....
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(toolIsin, container);
						
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
					
					
					String ignoredSitesString = null;
					for (int i = 0; i < ignoredSites.size(); i++) 
					{
						if(i==0)
						{
							ignoredSitesString = ignoredSites.get(i);
						}
						else
						{
							ignoredSitesString = ignoredSitesString+" "+ignoredSites.get(i);
						}
					}
					System.out.println("siti ignorati:"+ignoredSitesString);
					
					//procedo all'update dei dati del titolo...
					for(Quotation_Bond qb : container.getBondList())
					{
						try {
							db.updateSelectedBondByQuotationObject(qb, getTodaysDate());
							db.updateSelectedBondIgnoredSites(qb.getISIN(), ignoredSitesString);
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
				}
				else
				{
					showMessage("Info", "The "+toolIsin+" tool is not found from other sites");
				}
			}
			else
			{
				//connection error!
				showMessage("Error", "There were errors during connection with server. Please try again.");
			}
			
			dynamic_detail_table.removeAllViews();
			
			updateView();
			
			db.close();
		}
	}
}

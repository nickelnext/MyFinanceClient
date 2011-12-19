package it.dev;

import it.util.ConnectionUtils;
import it.util.Request;

import java.util.ArrayList;
import java.util.Calendar;

import mainpackage.RequestHandler;
import Quotes.QuotationContainer;
import Quotes.Quotation_Bond;
import Quotes.Quotation_Fund;
import Quotes.Quotation_Share;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;

public class AddNewShareActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private EditText shareISINEditText;
	private DatePicker buyDateDatePicker;
	private EditText buyPriceEditText;
	private EditText roundLotEditText;
	private EditText capitalGainTaxEditText;
	private EditText couponTaxEditText;
	
	private Button undoNewShareButton;
	private Button saveNewShareButton;
	
	private String portfolioName;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_share);
        
        shareISINEditText = (EditText) findViewById(R.id.shareISINEditText);
        buyDateDatePicker = (DatePicker) findViewById(R.id.buyDateDatePicker);
        buyPriceEditText = (EditText) findViewById(R.id.buyPriceEditText);
        roundLotEditText = (EditText) findViewById(R.id.roundLotEditText);
        capitalGainTaxEditText = (EditText) findViewById(R.id.capitalGainTaxEditText);
        couponTaxEditText = (EditText) findViewById(R.id.couponTaxEditText);
        
        undoNewShareButton = (Button) findViewById(R.id.undoNewShareButton);
        saveNewShareButton = (Button) findViewById(R.id.saveNewShareButton);
        
        Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        portfolioName = (String) intent.getStringExtra(pkg+".portfolioName");
        
        db = new MyFinanceDatabase(this);
        
        View.OnClickListener gestore = new View.OnClickListener() {
	    	  public void onClick(View view) { 
	    	    
	    	    switch(view.getId()){
	    	    case R.id.undoNewShareButton:
	    	    	finish();	    	    	
	    	        break;
	    	    case R.id.saveNewShareButton:
	    	    	if(notNullValues())
	  				{
	    	    		//Conn conn = new Conn();
	    	    		//JSONObject json = new JSONObject();
	    	    		//conn.getInputStreamFromUrl(json);
	    	    		saveNewShare();	    	    		
	  					finish();  					
	  				}
	  				else
	  				{
	  					showErrorMessage();
	  				}	  				
	    	        break;  
	    	    }	
	    	  }
	    };
	    
	    undoNewShareButton.setOnClickListener(gestore);
	    saveNewShareButton.setOnClickListener(gestore);
    }
	
	private boolean notNullValues()
	{
		if(shareISINEditText.getText().length()!=0 && buyPriceEditText.getText().length()!=0 && roundLotEditText.getText().length()!=0 && capitalGainTaxEditText.getText().length()!=0 && couponTaxEditText.getText().length()!=0)
			return true;
		else
			return false;
	}
	
	private void showErrorMessage()
	{
		AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
    	alert_builder.setTitle("Error");
    	alert_builder.setMessage("Control that you have insert all the data.");
    	alert_builder.setCancelable(false);
    	alert_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
    	AlertDialog message_empty = alert_builder.create();
    	message_empty.show();
	}
	
	private void saveNewShare()
	{						
		if(shareISINEditText.getText().length()==12)
		{
			Log.d(getPackageName(), "entrato");
			String buyDate = String.valueOf(buyDateDatePicker.getMonth())+"/"+String.valueOf(buyDateDatePicker.getDayOfMonth())+"/"+String.valueOf(buyDateDatePicker.getYear());
			ArrayList<Request> array = new ArrayList<Request>();
			array.add(new Request(shareISINEditText.getText().toString()));
			
			Gson converter = new Gson();
			String jsonReq = converter.toJson(array);
			Log.d(getPackageName(), "faccio la post data: "+jsonReq);
			String jsonResponse = ConnectionUtils.postData(jsonReq);
			QuotationContainer quotCont = RequestHandler.decodeQuotations(jsonResponse);
			
			Log.d(getPackageName(), "post data fatta. stampo la response: "+jsonResponse);
			
			for (Quotation_Bond qb : quotCont.getBondList()) {
				db.addNewBondByQuotationObject(qb, getTodaysDate());
				db.addNewBondInTransitionTable(portfolioName, shareISINEditText.getText().toString(), buyDate, 
						Integer.parseInt(buyPriceEditText.getText().toString()), Integer.parseInt(roundLotEditText.getText().toString()), 
						Integer.parseInt(capitalGainTaxEditText.getText().toString()), Integer.parseInt(couponTaxEditText.getText().toString()));
			}			
			for (Quotation_Share qs : quotCont.getShareList()) {
				db.addNewShareByQuotationObject(qs, getTodaysDate());
			}
			for (Quotation_Fund qf : quotCont.getFundList()) {
				db.addNewFundByQuotationObject(qf, getTodaysDate());
			}
	    				
		}
		else
		{
			Log.d(getPackageCodePath(), "else");
		}
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
}

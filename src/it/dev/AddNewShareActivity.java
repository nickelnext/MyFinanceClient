package it.dev;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewShareActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private EditText shareISINEditText;
	private DatePicker buyDateDatePicker;
	private EditText buyPriceEditText;
	private EditText roundLotEditText;
	
	private Button undoNewShareButton;
	private Button saveNewShareButton;
	
	private String portfolioName;
	private String shareType;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_share);
        
        shareISINEditText = (EditText) findViewById(R.id.shareISINEditText);
        buyDateDatePicker = (DatePicker) findViewById(R.id.buyDateDatePicker);
        buyPriceEditText = (EditText) findViewById(R.id.buyPriceEditText);
        roundLotEditText = (EditText) findViewById(R.id.roundLotEditText);
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
	    	    		//saveNewShare();
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
		if(shareISINEditText.getText().length()!=0 && buyPriceEditText.getText().length()!=0 && roundLotEditText.getText().length()!=0)
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
			
			@Override
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
			//richiesta isin
		}
		else
		{
			//richiesta codice
		}
	}
}

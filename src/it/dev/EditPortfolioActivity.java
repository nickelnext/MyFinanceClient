package it.dev;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditPortfolioActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private EditText modifyPortfolioNameEditText;
	private EditText modifyPortfolioDescriptionEditText;
	private Button undoModifyPortfolioButton;
	private Button modifyPortfolioButton;
	
	private String previousName;
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_portfolio);
        
        modifyPortfolioNameEditText = (EditText) findViewById(R.id.modifyPortfolioNameEditText);
        modifyPortfolioDescriptionEditText = (EditText) findViewById(R.id.modifyPortfolioDescriptionEditText);
        undoModifyPortfolioButton = (Button) findViewById(R.id.undoModifyPortfolioButton);
        modifyPortfolioButton = (Button) findViewById(R.id.modifyPortfolioButton);
        
        Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        previousName = (String) intent.getStringExtra(pkg+".previousName");
        modifyPortfolioNameEditText.setText((String) intent.getStringExtra(pkg+".previousName"));
        modifyPortfolioDescriptionEditText.setText((String) intent.getStringExtra(pkg+".previousDescription"));
        
        db = new MyFinanceDatabase(getApplicationContext());
        
        View.OnClickListener gestore = new View.OnClickListener() {
	    	  public void onClick(View view) { 
	    	    
	    	    switch(view.getId()){
	    	    case R.id.undoModifyPortfolioButton:
	    	    	finish();	    	    	
	    	        break;
	    	    case R.id.modifyPortfolioButton:
	    	    	if(notNullValues())
	  				{
	    	    		if(!portfolioNameAlreadyChoosen(modifyPortfolioNameEditText.getText().toString()) || 
	    	    				modifyPortfolioNameEditText.getText().toString().equals(previousName))
	    	    		{
	    	    			modifyPortfolio();
		  					finish();
	    	    		}
	    	    		else
	    	    		{
	    	    			showNameAlreadyChosenErrorMessage();
	    	    		}	  					
	  				}
	  				else
	  				{
	  					showErrorMessage();
	  				}	  				
	    	        break;  
	    	    }	
	    	  }
	    };
        
	    undoModifyPortfolioButton.setOnClickListener(gestore);
	    modifyPortfolioButton.setOnClickListener(gestore);
    }
	
	private boolean notNullValues()
	{
		if(modifyPortfolioNameEditText.getText().length()!=0 && modifyPortfolioDescriptionEditText.getText().length()!=0)
			return true;
		else
			return false;
	}
	
	private boolean portfolioNameAlreadyChoosen(String name)
	{
		db.open();
		Cursor c = db.getAllSavedPortfolio();
		startManagingCursor(c);
		if(c.getCount()!=0)
    	{
			c.moveToFirst();
			do {
				if(c.getString(1).equals(name))
				{
					return true;
				}
			} while (c.moveToNext());
    	}
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
	
	private void showNameAlreadyChosenErrorMessage()
	{
		AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
    	alert_builder.setTitle("Error");
    	alert_builder.setMessage("There is already a Portfolio with this name. Choose another name.");
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
	
	//-------------------------open DB, write in DB, close DB-------------------------------------//
		private void modifyPortfolio()
		{
			db.open();
			String creationDate = getTodaysDate();
			db.updateSelectedPortfolio(previousName, modifyPortfolioNameEditText.getText().toString(), 
					modifyPortfolioDescriptionEditText.getText().toString(), creationDate);
			db.close();
		}
		
		//-------------------------RETURNS the current date in format:-------------------------------//
		//-------------------------MM/DD/YYYY hh:mm:ss-----------------------------------------------//
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

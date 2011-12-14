package it.dev;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewPortfolioActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private EditText portfolioNameEditText;
	private EditText portfolioDescriptionEditText;
	private Button undoNewPortfolioButton;
	private Button saveNewPortfolioButton;
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_portfolio);
        
        portfolioNameEditText = (EditText) findViewById(R.id.portfolioNameEditText);
        portfolioDescriptionEditText = (EditText) findViewById(R.id.portfolioDescriptionEditText);
        undoNewPortfolioButton = (Button) findViewById(R.id.undoNewPortfolioButton);
        saveNewPortfolioButton = (Button) findViewById(R.id.saveNewPortfolioButton);
        
        db = new MyFinanceDatabase(getApplicationContext());
        
        View.OnClickListener gestore = new View.OnClickListener() {
	    	  public void onClick(View view) { 
	    	    
	    	    switch(view.getId()){
	    	    case R.id.undoNewPortfolioButton:
	    	    	finish();	    	    	
	    	        break;
	    	    case R.id.saveNewPortfolioButton:
	    	    	if(notNullValues())
	  				{
	    	    		if(portfolioNameAlreadyChoosen(portfolioNameEditText.getText().toString()))
	    	    		{
	    	    			showNameAlreadyChosenErrorMessage();
	    	    		}
	    	    		else
	    	    		{
	    	    			saveNewPortfolio();
		  					finish();
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
	    
	    undoNewPortfolioButton.setOnClickListener(gestore);
	    saveNewPortfolioButton.setOnClickListener(gestore);
    }
	
	private boolean notNullValues()
	{
		if(portfolioNameEditText.getText().length()!=0 && portfolioDescriptionEditText.getText().length()!=0)
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
			
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
    	AlertDialog message_empty = alert_builder.create();
    	message_empty.show();
	}
	
	//-------------------------open DB, write in DB, close DB-------------------------------------//
	private void saveNewPortfolio()
	{
		db.open();
		String creationDate = getTodaysDate();
		db.addNewPortfolio(1, portfolioNameEditText.getText().toString(), portfolioDescriptionEditText.getText().toString(), creationDate);
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

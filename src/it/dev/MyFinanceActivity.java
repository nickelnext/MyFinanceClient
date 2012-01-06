package it.dev;

import it.dev.MyFinanceDatabase.PortfolioMetaData;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class MyFinanceActivity extends Activity 
{
	private MyFinanceDatabase db;
	
	private ListView portfolioListView;
	private ArrayList<String> portfolioNameArrayList = new ArrayList<String>();
	private ArrayList<String> portfolioDescriptionArrayList = new ArrayList<String>();
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        portfolioListView = (ListView) findViewById(R.id.portfolioListView);
        registerForContextMenu(portfolioListView);
        
        db = new MyFinanceDatabase(this);
    }
    
    public void onResume()
    {
    	super.onResume();
    	updateView();
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(portfolioNameArrayList.get(info.position));
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_portfolio_context_menu, menu);
    }
    
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.edit_item:
        	showEditPortfolioDialog(portfolioNameArrayList.get(info.position), portfolioDescriptionArrayList.get(info.position));
        	return true;            
        case R.id.remove_item:        	
        	deleteSelectedPortfolio(portfolioNameArrayList.get(info.position));        	
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
    	getMenuInflater().inflate(R.menu.add_portfolio_menu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.menu_add_portfolio:
    		showAddNewPortfolioDialog();
    		break;
    	case R.id.menu_update_option:
    		showUpdateOptionDialog();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    //Open the custom alert dialog where it is possible to edit the selected portfolio.
    private void showEditPortfolioDialog(String name, String description)
    {
    	final String previousName = name;
    	final Dialog editPortfolioDialog = new Dialog(MyFinanceActivity.this);
    	editPortfolioDialog.setContentView(R.layout.custom_edit_portfolio_dialog);
    	editPortfolioDialog.setTitle("Edit portfolio");
    	editPortfolioDialog.setCancelable(true);
    	final EditText editedPortfolioName_ET = (EditText) editPortfolioDialog.findViewById(R.id.editedPortfolioName_ET);
		final EditText editedPortfolioDescription_ET = (EditText) editPortfolioDialog.findViewById(R.id.editedPortfolioDescription_ET);
		editedPortfolioName_ET.setText(name);
		editedPortfolioDescription_ET.setText(description);
		Button cancelEditPortfolio_btn = (Button) editPortfolioDialog.findViewById(R.id.cancelEditPortfolio_btn);
		Button editPortfolio_btn = (Button) editPortfolioDialog.findViewById(R.id.editPortfolio_btn);
		
		cancelEditPortfolio_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				editPortfolioDialog.dismiss();
			}
		});
		
		editPortfolio_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(editedPortfolioName_ET.getText().length()!=0 && editedPortfolioDescription_ET.getText().length()!=0)
				{
					if(!portfolioNameAlreadyChoosen(editedPortfolioName_ET.getText().toString()) || 
							editedPortfolioName_ET.getText().toString().equals(previousName))
					{
						db.open();
						String creationDate = getTodaysDate();
						db.updateSelectedPortfolio(previousName, editedPortfolioName_ET.getText().toString(), 
								editedPortfolioDescription_ET.getText().toString(), creationDate);
						db.close();
						editPortfolioDialog.dismiss();
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
			}
		});
		
		editPortfolioDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				updateView();
			}
		});
		
		editPortfolioDialog.show();
    }
    
    //Open the custom alert dialog where it is possible to add a new portfolio.    
    private void showAddNewPortfolioDialog()
    {
    	final Dialog addPortfolioDialog = new Dialog(MyFinanceActivity.this);
    	addPortfolioDialog.setContentView(R.layout.custom_add_new_portfolio_dialog);
    	addPortfolioDialog.setTitle("Add new portfolio");
    	addPortfolioDialog.setCancelable(true);
		final EditText portfolioName_ET = (EditText) addPortfolioDialog.findViewById(R.id.portfolioName_ET);
		final EditText portfolioDescription_ET = (EditText) addPortfolioDialog.findViewById(R.id.portfolioDescription_ET);
		Button cancelAddPortfolio_btn = (Button) addPortfolioDialog.findViewById(R.id.cancelAddPortfolio_btn);
		Button addPortfolio_btn = (Button) addPortfolioDialog.findViewById(R.id.addPortfolio_btn);
		
		cancelAddPortfolio_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addPortfolioDialog.dismiss();
			}
		});
		
		addPortfolio_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(portfolioName_ET.getText().length()!=0 && portfolioDescription_ET.getText().length()!=0)
				{
					if(!portfolioNameAlreadyChoosen(portfolioName_ET.getText().toString()))
					{
						db.open();
						String creationDate = getTodaysDate();
						db.addNewPortfolio(1, portfolioName_ET.getText().toString(), portfolioDescription_ET.getText().toString(), creationDate);
						db.close();
						addPortfolioDialog.dismiss();
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
			}
		});
		
		addPortfolioDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				updateView();
			}
		});
		
		addPortfolioDialog.show();
    }
    
    //Open the custom alert dialog where it is possible to select the automatic update.
    private void showUpdateOptionDialog()
    {
    	final Dialog updateOptionDialog = new Dialog(MyFinanceActivity.this);
    	updateOptionDialog.setContentView(R.layout.custom_update_option_dialog);
    	updateOptionDialog.setTitle("Automatic update");
    	updateOptionDialog.setCancelable(true);
    	
    	
    	final CheckBox enableAutoUpdateCheckBox = (CheckBox) updateOptionDialog.findViewById(R.id.enableAutoUpdateCheckBox);
    	final Spinner updateTimeSpinner = (Spinner) updateOptionDialog.findViewById(R.id.updateTimeSpinner);
    	final Button undoSavePreferencesButton = (Button) updateOptionDialog.findViewById(R.id.undoSavePreferencesButton);
    	final Button saveUpdatePreferencesButton = (Button) updateOptionDialog.findViewById(R.id.saveUpdatePreferencesButton);
    	
    	enableAutoUpdateCheckBox.setChecked(false);   
        updateTimeSpinner.setEnabled(false);
        saveUpdatePreferencesButton.setEnabled(false);
    	
        ArrayAdapter<CharSequence> TimeAdapter = ArrayAdapter.createFromResource(this, R.array.update_time_array, android.R.layout.simple_spinner_item);
        TimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateTimeSpinner.setAdapter(TimeAdapter);
        
        enableAutoUpdateCheckBox.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
				if (((CheckBox) v).isChecked())
				{
					updateTimeSpinner.setEnabled(true);
					saveUpdatePreferencesButton.setEnabled(true);
				}
				else
				{
					updateTimeSpinner.setEnabled(false);
					saveUpdatePreferencesButton.setEnabled(false);
				}
			}
		});
        
        View.OnClickListener gestore = new View.OnClickListener() {
	    	  public void onClick(View view) { 
	    	    
	    	    switch(view.getId()){
	    	    case R.id.undoSavePreferencesButton:
	    	    	updateOptionDialog.dismiss();   	    	
	    	        break;
	    	    case R.id.saveUpdatePreferencesButton:
	    	    	//save preferences....
	    	    	updateOptionDialog.dismiss();
	    	        break;  
	    	    }	
	    	  }
	    };
	    
	    undoSavePreferencesButton.setOnClickListener(gestore);
	    saveUpdatePreferencesButton.setOnClickListener(gestore);
	    
	    updateOptionDialog.show();
    }
    
    private void goToToolListActivity(String portfolioName)
    {
    	Intent i = new Intent(this, ToolListActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".portfolioName", portfolioName);		
		startActivity(i);
    }
    
    private void deleteSelectedPortfolio(String name)
    {
    	db.open();
    	db.deletePortfolioByName(name);
    	db.close();
    }
    
    /////////////////////////////////////////////////////////////////////////////
    //-------------------------------UPDATE the view when:---------------------//
    //--------------------------------a. a context menu is closed--------------//
    //--------------------------------b. the Activity is resumed---------------//
    //--------------------------------c. the Add Dialog dismiss----------------//
    //--------------------------------d. the Edit Dialog dismiss---------------//
    /////////////////////////////////////////////////////////////////////////////
    private void updateView()
    {
    	portfolioListView.setAdapter(null);
    	db.open();
    	Cursor c = db.getAllSavedPortfolio();
    	startManagingCursor(c);
    	if(c.getCount()!=0)
    	{
    		c.moveToFirst();
    		portfolioNameArrayList.clear();
    		portfolioDescriptionArrayList.clear();
        	do 
        	{
        		portfolioNameArrayList.add(c.getString(1));
        		portfolioDescriptionArrayList.add(c.getString(2));
    		} while (c.moveToNext());
        	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.portfolio_listview_items, c, 
        			new String[] {PortfolioMetaData.PORTFOLIO_NAME_KEY, PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY}, 
        			new int[] {R.id.nameTextView, R.id.descriptionTextView});
        	portfolioListView.setAdapter(adapter);
        	portfolioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
            {
            	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            	{                    
                    goToToolListActivity(portfolioNameArrayList.get(position));
                }
            });
    	}
    	db.close();
    }
    
    /////////////////////////////////////////////////////////////////////////////
    //------------------------------UTILS FUNCTIONS----------------------------//
    /////////////////////////////////////////////////////////////////////////////
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
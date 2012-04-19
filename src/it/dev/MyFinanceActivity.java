package it.dev;

import it.dev.MyFinanceDatabase.PortfolioMetaData;
import it.util.UpdateTimeTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import myUtils.UtilFuncs;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
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
import android.widget.TextView;

public class MyFinanceActivity extends Activity 
{
	private MyFinanceDatabase db;
	private SupportDatabaseHelper supportDatabase = new SupportDatabaseHelper(this);

	private TextView addPortfolioTextView;
	private TextView portfolioNameTextView;
	private TextView portfolioDescription;
	private ListView portfolioListView;

	private ArrayList<String> portfolioNameArrayList = new ArrayList<String>();
	private ArrayList<String> portfolioDescriptionArrayList = new ArrayList<String>();

	private static Timer timer = null;
	private static UpdateTimeTask up = null;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		addPortfolioTextView = (TextView) findViewById(R.id.addPortfolioTextView);
		portfolioNameTextView = (TextView) findViewById(R.id.portfolioNameTextView);
		portfolioDescription = (TextView) findViewById(R.id.portfolioDescription);
		portfolioListView = (ListView) findViewById(R.id.portfolioListView);
		registerForContextMenu(portfolioListView);

		db = new MyFinanceDatabase(this);

		try 
		{
			supportDatabase.createDataBase();

		} catch (IOException ioe) 
		{
			throw new Error("Unable to create database");
		}

		initializeLabels();

		startCountingForAutomaticUpdates();
		
		
		
		System.out.println(ConnectivityManager.EXTRA_EXTRA_INFO);
	}

	private void startCountingForAutomaticUpdates() {
		supportDatabase.openDataBase();
		
		int updateTime = supportDatabase.getUserSelectedAutoUpdate();
		
		
		if(updateTime==0)
				stopAutomaticUpdate();
		else
			startAutomaticUpdate(updateTime);
		supportDatabase.close();
	}

	private void initializeLabels()
	{
		supportDatabase.openDataBase();

		String language = supportDatabase.getUserSelectedLanguage();

		addPortfolioTextView.setText(supportDatabase.getTextFromTable("Label_MyFinanceActivity", "addPortfolio", language));
		portfolioNameTextView.setText(supportDatabase.getTextFromTable("Label_MyFinanceActivity", "portfolioName", language));
		portfolioDescription.setText(supportDatabase.getTextFromTable("Label_MyFinanceActivity", "portfolioDescription", language));

		supportDatabase.close();
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
		MenuItem editItem = menu.findItem(R.id.edit_item);
		MenuItem removeItem = menu.findItem(R.id.remove_item);

		supportDatabase.openDataBase();

		String language = supportDatabase.getUserSelectedLanguage();

		editItem.setTitle(supportDatabase.getTextFromTable("Label_select_portfolio_context_menu", "edit_item", language));
		removeItem.setTitle(supportDatabase.getTextFromTable("Label_select_portfolio_context_menu", "remove_item", language));

		supportDatabase.close();
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
		supportDatabase.openDataBase();

		String language = supportDatabase.getUserSelectedLanguage();

		getMenuInflater().inflate(R.menu.add_portfolio_menu, menu);
		MenuItem addPortfolio = menu.findItem(R.id.menu_add_portfolio);
		MenuItem updateOptions = menu.findItem(R.id.menu_update_option);
		MenuItem aboutPage = menu.findItem(R.id.menu_about_page);
		MenuItem helpPage = menu.findItem(R.id.menu_help_page);

		addPortfolio.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_add_portfolio", language));
		updateOptions.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_update_option", language));
		aboutPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_about_page", language));
		helpPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_help_page", language));

		supportDatabase.close();

		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		supportDatabase.openDataBase();

		String language = supportDatabase.getUserSelectedLanguage();

		menu.clear();

		getMenuInflater().inflate(R.menu.add_portfolio_menu, menu);
		MenuItem addPortfolio = menu.findItem(R.id.menu_add_portfolio);
		MenuItem updateOptions = menu.findItem(R.id.menu_update_option);
		MenuItem aboutPage = menu.findItem(R.id.menu_about_page);
		MenuItem helpPage = menu.findItem(R.id.menu_help_page);

		addPortfolio.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_add_portfolio", language));
		updateOptions.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_update_option", language));
		aboutPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_about_page", language));
		helpPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_help_page", language));

		supportDatabase.close();

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
		case R.id.menu_about_page:
			Intent i = new Intent(this, AboutActivity.class);
			startActivity(i);
			break;
		case R.id.menu_help_page:
			Intent i1 = new Intent(this, HelpActivity.class);
			startActivity(i1);
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
						String creationDate = UtilFuncs.getTodaysDate();
						db.updateSelectedPortfolio(previousName, editedPortfolioName_ET.getText().toString(), 
								editedPortfolioDescription_ET.getText().toString(), creationDate, creationDate);
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
		supportDatabase.openDataBase();

		String language = supportDatabase.getUserSelectedLanguage();

		final Dialog addPortfolioDialog = new Dialog(MyFinanceActivity.this);
		addPortfolioDialog.setContentView(R.layout.custom_add_new_portfolio_dialog);
		addPortfolioDialog.setTitle(supportDatabase.getTextFromTable("Label_custom_add_new_portfolio_dialog", "addPortfolioTitle", language));
		addPortfolioDialog.setCancelable(true);

		final TextView portfolioNameTextView = (TextView) addPortfolioDialog.findViewById(R.id.portfolioNameTextView);
		final TextView portfolioDescriptionTextView = (TextView) addPortfolioDialog.findViewById(R.id.portfolioDescriptionTextView);

		final EditText portfolioName_ET = (EditText) addPortfolioDialog.findViewById(R.id.portfolioName_ET);
		final EditText portfolioDescription_ET = (EditText) addPortfolioDialog.findViewById(R.id.portfolioDescription_ET);
		Button cancelAddPortfolio_btn = (Button) addPortfolioDialog.findViewById(R.id.cancelPortfolio_btn);
		Button addPortfolio_btn = (Button) addPortfolioDialog.findViewById(R.id.addPortfolio_btn);

		//add labels...
		portfolioNameTextView.setText(supportDatabase.getTextFromTable("Label_custom_add_new_portfolio_dialog", "portfolioName", language));
		portfolioDescriptionTextView.setText(supportDatabase.getTextFromTable("Label_custom_add_new_portfolio_dialog", "porfolioDescription", language));

		portfolioName_ET.setHint(supportDatabase.getTextFromTable("Label_custom_add_new_portfolio_dialog", "portfolioName_ET", language));
		portfolioDescription_ET.setHint(supportDatabase.getTextFromTable("Label_custom_add_new_portfolio_dialog", "portfolioDescription_ET", language));

		cancelAddPortfolio_btn.setText(supportDatabase.getTextFromTable("Label_custom_add_new_portfolio_dialog", "cancelPortfolio_btn", language));
		addPortfolio_btn.setText(supportDatabase.getTextFromTable("Label_custom_add_new_portfolio_dialog", "addPortfolio_btn", language));

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
						String creationDate = UtilFuncs.getTodaysDate();
						db.addNewPortfolio(1, portfolioName_ET.getText().toString(), portfolioDescription_ET.getText().toString(), creationDate, creationDate);
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

		supportDatabase.close();

		addPortfolioDialog.show();
	}

	//Open the custom alert dialog where it is possible to select the automatic update.
	private void showUpdateOptionDialog()
	{
		try 
		{
			supportDatabase.createDataBase();
			supportDatabase.openDataBase();

		} catch (IOException ioe) 
		{
			throw new Error("Unable to create database");
		}

		String language = supportDatabase.getUserSelectedLanguage();

		final Dialog updateOptionDialog = new Dialog(MyFinanceActivity.this);
		updateOptionDialog.setContentView(R.layout.custom_update_option_dialog);
		updateOptionDialog.setTitle(supportDatabase.getTextFromTable("Label_custom_update_option_dialog", "dialog_title", language));
		updateOptionDialog.setCancelable(true);

		TextView updateLanguageTextView = (TextView) updateOptionDialog.findViewById(R.id.updateLanguageTextView);
		TextView updateTimeTextView = (TextView) updateOptionDialog.findViewById(R.id.updateTimeTextView);

		final CheckBox enableAutoUpdateCheckBox = (CheckBox) updateOptionDialog.findViewById(R.id.enableAutoUpdateCheckBox);
		final Spinner updateTimeSpinner = (Spinner) updateOptionDialog.findViewById(R.id.updateTimeSpinner);
		Button undoSavePreferencesButton = (Button) updateOptionDialog.findViewById(R.id.undoSavePreferencesButton);
		Button saveUpdatePreferencesButton = (Button) updateOptionDialog.findViewById(R.id.saveUpdatePreferencesButton);
		final Spinner updateLanguageSpinner = (Spinner) updateOptionDialog.findViewById(R.id.updateLanguageSpinner);

		updateLanguageTextView.setText(supportDatabase.getTextFromTable("Label_custom_update_option_dialog", "update_language_TV", language));
		enableAutoUpdateCheckBox.setText(supportDatabase.getTextFromTable("Label_custom_update_option_dialog", "enable_auto_update_CB", language));
		updateTimeTextView.setText(supportDatabase.getTextFromTable("Label_custom_update_option_dialog", "update_time_TV", language));
		undoSavePreferencesButton.setText(supportDatabase.getTextFromTable("Label_custom_update_option_dialog", "undo_save_preferences_button", language));
		saveUpdatePreferencesButton.setText(supportDatabase.getTextFromTable("Label_custom_update_option_dialog", "save_preferences_button", language));

		ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this, R.array.update_time_array, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this, R.array.update_language_array, android.R.layout.simple_spinner_item);

		timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		updateTimeSpinner.setAdapter(timeAdapter);
		updateLanguageSpinner.setAdapter(languageAdapter);

		//checks which are the user preferences
		int userSelectedAutoUpdate = supportDatabase.getUserSelectedAutoUpdate();
		String userSelectedLanguage = supportDatabase.getUserSelectedLanguage();

		System.out.println("userSelectedAutoUpdate " + userSelectedAutoUpdate);
		System.out.println("userSelectedAutoUpdateLanguage " + userSelectedLanguage);

		if(supportDatabase.getUserSelectedAutoUpdate()==0)
		{
			enableAutoUpdateCheckBox.setChecked(false);
			updateTimeSpinner.setEnabled(false);
		}
		else
		{
			enableAutoUpdateCheckBox.setChecked(true);
			updateTimeSpinner.setEnabled(true);
		}
		
		for(int i=0; i<timeAdapter.getCount();i++)
		{
			if(Integer.valueOf(timeAdapter.getItem(i).toString())==userSelectedAutoUpdate)
				updateTimeSpinner.setSelection(i);
		}
		for(int i=0; i<languageAdapter.getCount();i++)
		{
			if(languageAdapter.getItem(i).toString().equals(userSelectedLanguage))
				updateLanguageSpinner.setSelection(i);
		}
		
		enableAutoUpdateCheckBox.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				if (((CheckBox) v).isChecked())
					updateTimeSpinner.setEnabled(true);
				else
					updateTimeSpinner.setEnabled(false);
			}
		});

		
		View.OnClickListener gestore = new View.OnClickListener() {
			public void onClick(View view) { 


				switch(view.getId()){
				case R.id.undoSavePreferencesButton:
					updateOptionDialog.dismiss();   	    	
					break;
				case R.id.saveUpdatePreferencesButton:
					supportDatabase.openDataBase();
					int newUpdateTime = Integer.valueOf(updateTimeSpinner.getSelectedItem().toString());
					boolean newAutoUpdate = enableAutoUpdateCheckBox.isChecked();
					String newLanguage =  (String)updateLanguageSpinner.getSelectedItem();
					if(newAutoUpdate==false)
						{
							newUpdateTime = 0; //0 is for NoAutoUpdate
							stopAutomaticUpdate();
						}
					else
						startAutomaticUpdate(newUpdateTime);
					
					supportDatabase.setConfigParameters(newLanguage, newUpdateTime);
					

					supportDatabase.close();
					updateOptionDialog.dismiss();
					break;  
				}

			}
		};

		updateOptionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				initializeLabels();
			}
		});
		
		undoSavePreferencesButton.setOnClickListener(gestore);
		saveUpdatePreferencesButton.setOnClickListener(gestore);
		
		supportDatabase.close();
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
		
		//salvo tutti gli isin presenti nel portafoglio
		ArrayList<String> listaBondDelPortafoglio = new ArrayList<String>();
		ArrayList<String> listaFundDelPortafoglio = new ArrayList<String>();
		ArrayList<String> listaShareDelPortafoglio = new ArrayList<String>();
		listaBondDelPortafoglio.clear();
		listaFundDelPortafoglio.clear();
		listaShareDelPortafoglio.clear();
		
		Cursor allBonds = db.getAllBondsForPortfolio(name);
		Cursor allFunds = db.getAllFundsForPortfolio(name);
		Cursor allShares = db.getAllSharesForPortfolio(name);
		
		startManagingCursor(allBonds);
		startManagingCursor(allFunds);
		startManagingCursor(allShares);
		
		if(allBonds.getCount()!=0)
		{
			allBonds.moveToFirst();
			do {
				listaBondDelPortafoglio.add(allBonds.getString(2));
			} while (allBonds.moveToNext());
		}
		
		if(allFunds.getCount()!=0)
		{
			allFunds.moveToFirst();
			do {
				listaFundDelPortafoglio.add(allFunds.getString(2));
			} while (allFunds.moveToNext());
		}
		
		if(allShares.getCount()!=0)
		{
			allShares.moveToFirst();
			do {
				listaShareDelPortafoglio.add(allShares.getString(2));
			} while (allShares.moveToNext());
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		//elimino tutte le tuple nella tabella di transizione PORTFOLIO <--> BOND
		db.deleteAllBondsInTransitionTableForPortfolio(name);
		
		//elimino tutte le tuple nella tabella di transizione PORTFOLIO <--> FUND
		db.deleteAllFundsInTransitionTableForPortfolio(name);
		
		//elimino tutte le tuple nella tabella di transizione PORTFOLIO <--> SHARE
		db.deleteAllSharesInTransitionTableForPortfolio(name);
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		//per tutti i bond, fund e share contenuti nel portafoglio devo controllare che non siano contenuti 
		//in altri portafogli; in caso negativo posso cancellarli dalle tabelle QUOTATION....
		for (int i = 0; i < listaBondDelPortafoglio.size(); i++) 
		{
			if(!db.bondInOtherPortfolios(listaBondDelPortafoglio.get(i), name))
			{
				db.deleteBond(listaBondDelPortafoglio.get(i));
				db.deleteTOOLHistoricalData(listaBondDelPortafoglio.get(i));
			}
		}
		
		for (int i = 0; i < listaFundDelPortafoglio.size(); i++) 
		{
			if(!db.fundInOtherPortfolios(listaFundDelPortafoglio.get(i), name))
			{
				db.deleteBond(listaFundDelPortafoglio.get(i));
				db.deleteTOOLHistoricalData(listaFundDelPortafoglio.get(i));
			}
		}
		
		for (int i = 0; i < listaShareDelPortafoglio.size(); i++) 
		{
			if(!db.shareInOtherPortfolios(listaShareDelPortafoglio.get(i), name))
			{
				db.deleteBond(listaShareDelPortafoglio.get(i));
				db.deleteTOOLHistoricalData(listaShareDelPortafoglio.get(i));
			}
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
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

	private void startAutomaticUpdate(int time){
		timer = new Timer();
//		timer.cancel();
		up = new UpdateTimeTask(MyFinanceActivity.this);
		timer.schedule(up, 100, time*60000);
	}

	private void stopAutomaticUpdate()
	{
		if(timer!= null)
			timer.cancel();
	}
}
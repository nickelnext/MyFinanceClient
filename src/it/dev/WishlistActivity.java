package it.dev;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class WishlistActivity extends Activity 
{
	private MyFinanceDatabase db;
	private SupportDatabaseHelper supportDatabase = new SupportDatabaseHelper(this);
	
	private TextView wishlistLastUpdate_TV;
	private TextView addTitle_TV;
	
	private TextView nameCol;
	private TextView variationCol;
	private TextView percVarCol;
	private TextView priceCol;
	
	private ListView wishlist_toolListView;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.wishlist_activity);
        
        wishlistLastUpdate_TV = (TextView) findViewById(R.id.wishlistLastUpdate_TV);
        addTitle_TV = (TextView) findViewById(R.id.addTitle_TV);
        
        nameCol = (TextView) findViewById(R.id.nameCol);
        variationCol = (TextView) findViewById(R.id.variationCol);
        percVarCol = (TextView) findViewById(R.id.percVarCol);
        priceCol = (TextView) findViewById(R.id.priceCol);
        
        wishlist_toolListView = (ListView) findViewById(R.id.wishlist_toolListView);
        
        db = new MyFinanceDatabase(this);
        
        try 
        {
        	supportDatabase.createDataBase();
 
        } catch (IOException ioe) 
        {
        	throw new Error("Unable to create database");
        }
        
        initializeLabels();
    }
	
	private void initializeLabels()
	{
		supportDatabase.openDataBase();
		
		String language = supportDatabase.getUserSelectedLanguage();
		
		wishlistLastUpdate_TV.setText(supportDatabase.getTextFromTable("Label_ToolListActivity", "portfolioLastUpdate_TV", language)+": ");
		addTitle_TV.setText(supportDatabase.getTextFromTable("Label_ToolListActivity", "addTitle", language));
		
		nameCol.setText(supportDatabase.getTextFromTable("Label_ToolListActivity", "nameCol", language));
		variationCol.setText(supportDatabase.getTextFromTable("Label_ToolListActivity", "variationCol", language));
		percVarCol.setText(supportDatabase.getTextFromTable("Label_ToolListActivity", "percVariationCol", language));
		priceCol.setText(supportDatabase.getTextFromTable("Label_ToolListActivity", "price", language));
		
		supportDatabase.close();
	}
	
	public void onResume()
    {
		super.onResume();
		//updateView();
    }
	
	public boolean onCreateOptionsMenu(Menu menu)
    {
		supportDatabase.openDataBase();
		
		String language = supportDatabase.getUserSelectedLanguage();
		
		getMenuInflater().inflate(R.menu.wishlist_menu, menu);
	
		MenuItem menuAddShare = menu.findItem(R.id.menu_add_wish);
		MenuItem menuManualUpdate = menu.findItem(R.id.menu_manual_update);
		MenuItem aboutPage = menu.findItem(R.id.menu_about_page);
		MenuItem helpPage = menu.findItem(R.id.menu_help_page);
	
		menuAddShare.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_add_share", language));
		menuManualUpdate.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_update_tools", language));
		aboutPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_about_page", language));
		helpPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_help_page", language));
	
    	supportDatabase.close();
    	return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.menu_add_wish:
    		//add new wish...
    		break;
    	case R.id.menu_manual_update:
    		//manual update...
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
}

package it.dev;

import it.dev.MyFinanceDatabase.PortfolioMetaData;

import java.util.ArrayList;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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
        	goToEditPortfolioActivity(portfolioNameArrayList.get(info.position), portfolioDescriptionArrayList.get(info.position));
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
    		goToAddNewPortfolioActivity();
    		break;
    	case R.id.menu_update_option:
    		goToUpdateOptionActivity();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    private void goToAddNewPortfolioActivity()
    {
    	Intent i = new Intent(this, AddNewPortfolioActivity.class);
    	startActivity(i);
    }
    
    private void goToUpdateOptionActivity()
    {
    	Intent i = new Intent(this, UpdateOptionActivity.class);
    	startActivity(i);
    }
    
    private void goToEditPortfolioActivity(String name, String description)
    {
    	Intent i = new Intent(this, EditPortfolioActivity.class);
		String pkg = getPackageName();
		i.putExtra(pkg+".previousName", name);
		i.putExtra(pkg+".previousDescription", description);
		startActivity(i);
    }
    
    private void goToShareListActivity(String portfolioName)
    {
    	Intent i = new Intent(this, ShareListActivity.class);
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
        			new String[] {PortfolioMetaData.PORTFOLIO_NAME_KEY, PortfolioMetaData.PORTFOLIO_DESCRIPTION_KEY, PortfolioMetaData.PORTFOLIO_CREATION_DATE_KEY}, 
        			new int[] {R.id.nameTextView, R.id.descriptionTextView, R.id.creationDateTextView});
        	portfolioListView.setAdapter(adapter);
        	portfolioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
            {
            	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            	{                    
                    goToShareListActivity(portfolioNameArrayList.get(position));
                }
            });
    	}
    	db.close();
    }
}
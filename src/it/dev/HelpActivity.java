package it.dev;

import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends Activity {

	private TextView helpHeaderTextView;
	private TextView addPortfolioHeaderTextView;
	private TextView addPortfolioTextView;
	private TextView modifyPortfolioHeaderTextView;
	private TextView modifyPortfolioTextView;
	private TextView viewPortfolioHeaderTextView;
	private TextView viewPortfolioTextView;
	private TextView addTitleHeaderTextView;
	private TextView addTitleTextView;
	private TextView modifyTitleHeaderTextView;
	private TextView modifyTitleTextView;
	private TextView detailsTitleHeaderTextView;
	private TextView detailsTitleTextView;

	private SupportDatabaseHelper supportDatabase = new SupportDatabaseHelper(this);

	public void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
		
		helpHeaderTextView = (TextView) findViewById(R.id.helpHeaderTextView);
		addPortfolioHeaderTextView = (TextView) findViewById(R.id.addPortfolioHeaderTextView);
		addPortfolioTextView = (TextView) findViewById(R.id.addPortfolioTextView);
		modifyPortfolioHeaderTextView = (TextView) findViewById(R.id.modifyPortfolioHeaderTextView);
		modifyPortfolioTextView = (TextView) findViewById(R.id.modifyPortfolioTextView);
		viewPortfolioHeaderTextView = (TextView) findViewById(R.id.viewPortfolioHeaderTextView);
		viewPortfolioTextView = (TextView) findViewById(R.id.viewPortfolioTextView);
		addTitleHeaderTextView = (TextView) findViewById(R.id.addTitleHeaderTextView);
		addTitleTextView = (TextView) findViewById(R.id.addTitleTextView);
		modifyTitleHeaderTextView = (TextView) findViewById(R.id.modifyTitleHeaderTextView);
		modifyTitleTextView = (TextView) findViewById(R.id.modifyTitleTextView);
		detailsTitleHeaderTextView = (TextView) findViewById(R.id.detailsTitleHeaderTextView);
		detailsTitleTextView = (TextView) findViewById(R.id.detailsTitleTextView);
		
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
	
	//load data from support database and fill textview...
	private void updateView()
	{
		supportDatabase.openDataBase();
		
		String language = supportDatabase.getUserSelectedLanguage();
		
		Cursor help_info = supportDatabase.getInfoInLanguage("Help_About", language);
		startManagingCursor(help_info);
		
		if(help_info.getCount()==1)
		{
			help_info.moveToFirst();
			helpHeaderTextView.setText(help_info.getString(help_info.getColumnIndex("helpHeader")));
			addPortfolioHeaderTextView.setText(help_info.getString(help_info.getColumnIndex("addPortfolioHeader")));
			addPortfolioTextView.setText(help_info.getString(help_info.getColumnIndex("addPortfolio")));
			modifyPortfolioHeaderTextView.setText(help_info.getString(help_info.getColumnIndex("modifyPortfolioHeader")));
			modifyPortfolioTextView.setText(help_info.getString(help_info.getColumnIndex("modifyPortfolio")));
			viewPortfolioHeaderTextView.setText(help_info.getString(help_info.getColumnIndex("viewPortfolioHeader")));
			viewPortfolioTextView.setText(help_info.getString(help_info.getColumnIndex("viewPortfolio")));
			addTitleHeaderTextView.setText(help_info.getString(help_info.getColumnIndex("addTitleHeader")));
			addTitleTextView.setText(help_info.getString(help_info.getColumnIndex("addTitle")));
			modifyTitleHeaderTextView.setText(help_info.getString(help_info.getColumnIndex("modifyTitleHeader")));
			modifyTitleTextView.setText(help_info.getString(help_info.getColumnIndex("modifyTitle")));
			detailsTitleHeaderTextView.setText(help_info.getString(help_info.getColumnIndex("detailsTitleHeader")));
			detailsTitleTextView.setText(help_info.getString(help_info.getColumnIndex("detailsTitle")));
			
		}
//TODO			
			supportDatabase.close();
		
	}
}

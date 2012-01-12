package it.dev;

import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity 
{
	private TextView aboutHeaderTextView;
	private TextView versionTextView;
	private TextView copyrightTextView;
	private TextView creditsTextView;
	
	private SupportDatabaseHelper supportDatabase = new SupportDatabaseHelper(this);
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        
        aboutHeaderTextView = (TextView) findViewById(R.id.aboutHeaderTextView);
        versionTextView = (TextView) findViewById(R.id.versionTextView);
        copyrightTextView = (TextView) findViewById(R.id.copyrightTextView);
        creditsTextView = (TextView) findViewById(R.id.creditsTextView);
        
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
		
		Cursor about_info = supportDatabase.getInfoInLanguage("Activity_About", language);
		startManagingCursor(about_info);
		
		if(about_info.getCount()==1)
		{
			about_info.moveToFirst();
			aboutHeaderTextView.setText(about_info.getString(about_info.getColumnIndex("aboutHeader")));
		    versionTextView.setText(about_info.getString(about_info.getColumnIndex("version")));
	        copyrightTextView.setText(about_info.getString(about_info.getColumnIndex("copyright")));
	        creditsTextView.setText(about_info.getString(about_info.getColumnIndex("credits")));
			
		}
//TODO			
			supportDatabase.close();
		
	}
}

package it.dev;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class UpdateOptionActivity extends Activity 
{
	private CheckBox enableAutoUpdateCheckBox;
	private Spinner updateTimeSpinner;
	
	private Button undoSavePreferencesButton;
	private Button saveUpdatePreferencesButton;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.update_option);
        
        enableAutoUpdateCheckBox = (CheckBox) findViewById(R.id.enableAutoUpdateCheckBox);        
        updateTimeSpinner = (Spinner) findViewById(R.id.updateTimeSpinner);
        undoSavePreferencesButton = (Button) findViewById(R.id.undoSavePreferencesButton);
        saveUpdatePreferencesButton = (Button) findViewById(R.id.saveUpdatePreferencesButton);
        
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
	    	    	finish();	    	    	
	    	        break;
	    	    case R.id.saveUpdatePreferencesButton:
	    	    	//save preferences....
	    	    	finish();
	    	        break;  
	    	    }	
	    	  }
	    };
	    
	    undoSavePreferencesButton.setOnClickListener(gestore);
	    saveUpdatePreferencesButton.setOnClickListener(gestore);
    }
}

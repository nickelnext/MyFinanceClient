package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class BondDetailsActivity extends Activity 
{
	private TextView bondReferenceTextView;
	private String bondIsin;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bond_details);
		
		bondReferenceTextView = (TextView) findViewById(R.id.bondReferenceTextView);
		
		Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        bondIsin = (String) intent.getStringExtra(pkg+".bondIsin");        
    	bondReferenceTextView.setText(bondIsin);
    }
}

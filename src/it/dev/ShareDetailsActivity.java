package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShareDetailsActivity extends Activity 
{
	private TextView shareReferenceTextView;
	private String shareIsin;
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_details);
		
		shareReferenceTextView = (TextView) findViewById(R.id.shareReferenceTextView);
		
        Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        shareIsin = (String) intent.getStringExtra(pkg+".shareIsin");        
    	shareReferenceTextView.setText(shareIsin);
    }
}

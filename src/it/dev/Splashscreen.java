package it.dev;

import it.util.ConnectionUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class Splashscreen extends Activity 
{
	private Thread splashTread;
	private MyFinanceDatabase db;
	
   public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.splashscreen);
	   
	   db = new MyFinanceDatabase(this);
	   
	   final Splashscreen sPlashScreen = this;
	   
	   splashTread = new Thread() 
	   {
		   public void run() 
		   {
			   try 
			   {
				   synchronized (this) 
				   {
					   try {
							String jsonReq = null;
							System.out.println(""+jsonReq);
							
							String jsonResponse = ConnectionUtils.getSites(jsonReq);
							if(jsonResponse != null)
							{
								//quotCont = ResponseHandler.decodeQuotations(jsonResponse);
								
								//write in database sites/types....
								
								db.open();
								
								
								
								
								
								db.close();
								
								
							}
						} catch (Exception e) {
							System.out.println("connection ERROR");
							showMessage("Error", "No connection with Server.");						
						}
				   }
			   }
			   catch (Exception e) 
			   {
				   
			   }
			   finally 
			   {
				   finish();
				   Intent i = new Intent();
				   i.setClass(sPlashScreen, MyFinanceActivity.class);
				   startActivity(i);
			   }
		   }
	   };
	   
	   splashTread.start();
   }
   
   private void showMessage(String type, String message)
   {
	   AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
	   alert_builder.setTitle(type);
	   alert_builder.setMessage(message);
	   alert_builder.setCancelable(false);
	   alert_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
		   public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
		   }
	   });
	   AlertDialog message_empty = alert_builder.create();
	   message_empty.show();
	}
}


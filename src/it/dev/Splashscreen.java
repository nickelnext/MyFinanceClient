package it.dev;

import it.util.ConnectionUtils;
import it.util.ResponseHandler;

import java.util.ArrayList;

import other.TypeSiteObject;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class Splashscreen extends Activity 
{
	private int version;
	
	private Thread emptyRequestTread;
	private Thread versionRequestTread;
	
	private MyFinanceDatabase db;
	
   public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.splashscreen);
	   
	   db = new MyFinanceDatabase(this);
	   
	   final Splashscreen sPlashScreen = this;
	   
	   initializeThreads(sPlashScreen);
	   
	   db.open();
	   
	   Cursor version = db.getAllSitesForType();
	   startManagingCursor(version);
	   
	   System.out.println("numero di elementi VERSION: "+version.getCount());
	   
	   if(version.getCount()!=0)
	   {
		   version.moveToFirst();
		   this.version = version.getInt(version.getColumnIndex("versione"));
		   
		   //request con numero di versione...
		   System.out.println("Start VERSION REQUEST....");
		   
		   
		   versionRequestTread.start();
	   }
	   else
	   {
		   //request vuota...
		   System.out.println("Start EMPTY REQUEST....");		  
		   
		   emptyRequestTread.start();
	   }
	   db.close();
   }
   
   private void initializeThreads(Splashscreen splashscreen)
   {
	   final Splashscreen splash = splashscreen;
	   
	   emptyRequestTread = new Thread() 
	   {
		   public void run() 
		   {
			   try 
			   {
				   
				   
				   synchronized (this) 
				   {
					   //wait 2 secs...
					   wait(1500);
					   
					   try {
							String jsonReq = "empty";	
							System.out.println(""+jsonReq);
							
							String serverResponse = ConnectionUtils.getSites(jsonReq);
							if(serverResponse != null)
							{
								//int version = jsonResponse.hashCode();
								String[] arrResp = serverResponse.split("__PULITO__");
								String json = arrResp[1];
								int version = Integer.parseInt(arrResp[0]);
								System.out.println("version:"+version+" - json:"+json);
								ArrayList<TypeSiteObject> typeSiteList = ResponseHandler.decodeDBSiteType(json);
								
								System.out.println("mi appresto a scrivere nel db");
								//write in database sites/types....
								
								db.open();
								
								for (TypeSiteObject o : typeSiteList) 
								{
									for (String s : o.getSites()) 
									{
										//inserisci la coppia (o.getType(), s)
										
										db.addNewSiteForType(version, o.getType(), s);
										
									}
								}								
								
								db.close();
								
								
							}
						} catch (Exception e) {
							e.printStackTrace();
							//showMessage("Error", "No connection with Server.");						
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
				   i.setClass(splash, MyFinanceActivity.class);
				   startActivity(i);
			   }
		   }
	   };
	   
	   versionRequestTread = new Thread() 
	   {
		   public void run() 
		   {
			   try 
			   {
				   synchronized (this) 
				   {
					   //wait 2 secs...
					   wait(1500);
					   
					   try {
							String jsonReq = String.valueOf(version);	
							System.out.println(""+jsonReq);
							
							String serverResponse = ConnectionUtils.getSites(jsonReq);
							if(serverResponse != null)
							{
								
								System.out.println("risposta: <"+serverResponse+">");
								
								
								//version is up to date...
								if(serverResponse.startsWith("OK"))
								{
									//don't write in db...
								}
								else
								{
									System.out.println("scrivo nel DB, perché la versione del server è più aggiornata");
									
									//int version = jsonResponse.hashCode();
									String[] arrResp = serverResponse.split("__PULITO__");
									String json = arrResp[1];
									int version = Integer.parseInt(arrResp[0]);
									System.out.println("version:"+version+" - json:"+json);
									
									ArrayList<TypeSiteObject> typeSiteList = ResponseHandler.decodeDBSiteType(json);
									
									
									//write in database sites/types....
									
									db.open();
									
									//elimino tutti i record presenti...
									db.deleteAllSitesForTypes();
									
									for (TypeSiteObject o : typeSiteList) 
									{
										for (String s : o.getSites()) 
										{
											
											//inserisci la coppia (o.getType(), s)
											
											db.addNewSiteForType(version, o.getType(), s);
											
										}
									}								
									
									db.close();
								}
								
							}
							else
							{
								System.out.println("response null");
							}
						} catch (Exception e) {
							e.printStackTrace();
							//showMessage("Error", "No connection with Server.");						
						}
					   
					   
					   
					   
				   }
			   }
			   catch (Exception e) 
			   {
				   //showMessage("Error", "Error during comunication with server.");	
			   }
			   finally 
			   {
				   
				   finish();
				   Intent i = new Intent();
				   i.setClass(splash, MyFinanceActivity.class);
				   startActivity(i);
			   }
		   }
	   };
   }
   
//   private void showMessage(String type, String message)
//   {
//	   AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
//	   alert_builder.setTitle(type);
//	   alert_builder.setMessage(message);
//	   alert_builder.setCancelable(false);
//	   alert_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//			
//		   public void onClick(DialogInterface dialog, int id) {
//				dialog.cancel();
//		   }
//	   });
//	   AlertDialog message_empty = alert_builder.create();
//	   message_empty.show();
//	}
}


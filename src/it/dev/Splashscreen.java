package it.dev;

import it.util.ConnectionUtils;
import it.util.ResponseHandler;

import java.util.ArrayList;

import other.TypeSiteObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class Splashscreen extends Activity 
{
	private int version;
	
	private Thread emptyRequestTread;
	private Thread versionRequestTread;
	
	private MyFinanceDatabase db;
	
	private ProgressDialog prog;
	
   public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.splashscreen);
	   
	   db = new MyFinanceDatabase(this);
	   
	   final Splashscreen sPlashScreen = this;
	   
	   initializeThreads(sPlashScreen);
	   
	   db.open();
	   
	   Cursor version = db.getAllSitesForType();
	   startManagingCursor(version);
	   
	   if(version.getCount()!=0)
	   {
		   version.moveToFirst();
		   this.version = version.getInt(version.getColumnIndex("versione"));
		   
		   //request con numero di versione...
		   System.out.println("Start VERSION REQUEST....");
		   
		   prog = new ProgressDialog(sPlashScreen);
		   prog.setMessage("Loading...");
		   prog.show();
		   
		   versionRequestTread.start();
	   }
	   else
	   {
		   //request vuota...
		   System.out.println("Start EMPTY REQUEST....");
		   
		   prog = new ProgressDialog(sPlashScreen);
		   prog.setMessage("Loading...");
		   prog.show();
		   
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
				   if(prog.isShowing())
				   {
					   prog.dismiss();
				   }
				   
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
								//version is up to date...
								if(serverResponse.equals("OK"))
								{
									//don't write in db...
									System.out.println("okkkkkkkkkkkkkkkkkkk");
								}
								else
								{
									System.out.println("scrivo nel DB");
									
									//int version = jsonResponse.hashCode();
									String[] arrResp = serverResponse.split("__PULITO__");
									String json = arrResp[1];
									int version = Integer.parseInt(arrResp[0]);
									System.out.println("version:"+version+" - json:"+json);
									
									ArrayList<TypeSiteObject> typeSiteList = ResponseHandler.decodeDBSiteType(json);
									
									
									//write in database sites/types....
									
									db.open();
									
									for (TypeSiteObject o : typeSiteList) 
									{
										for (String s : o.getSites()) 
										{
											//elimino tutti i record presenti...
											db.deleteAllSitesForTypes();
											
											//inserisci la coppia (o.getType(), s)
											
											db.addNewSiteForType(version, o.getType(), s);
											
										}
									}								
									
									db.close();
								}
								
							}
						} catch (Exception e) {
							System.out.println("connection ERROR");
							showMessage("Error", "No connection with Server.");						
						}
					   
					   
					   
					   
				   }
			   }
			   catch (Exception e) 
			   {
				   showMessage("Error", "Error during comunication with server.");	
			   }
			   finally 
			   {
				   if(prog.isShowing())
				   {
					   prog.dismiss();
				   }
				   
				   finish();
				   Intent i = new Intent();
				   i.setClass(splash, MyFinanceActivity.class);
				   startActivity(i);
			   }
		   }
	   };
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


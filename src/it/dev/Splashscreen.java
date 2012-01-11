package it.dev;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ProgressBar;

public class Splashscreen extends Activity 
{
	private static Handler mHandler ;
    private static Handler mainHandler ;
    private ProgressBar mBar ;
    
    protected static final int CLOSE_SPLASH = 0 ;
    protected static final int SET_PROGRESS = 1 ;
    
   public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.splashscreen);
    
	   mBar = (ProgressBar)findViewById(R.id.splash_bar);
    
	   mHandler = new Handler()
	   {
		   public void handleMessage(Message msg)
		   {
			   switch(msg.what)
			   {
            		case CLOSE_SPLASH:
            			finish();
            			break;
            		case SET_PROGRESS:
            			mBar.setProgress((msg.arg1 / 10));
			   }
            
		   }
	   }; 
   }
   
   public void onStart()
   {
	   super.onStart();
	   if(mainHandler != null)
	   {
            mainHandler.sendEmptyMessage(MyFinanceActivity.START_LOAD);
	   }
   }

   public boolean onKeyDown (int keyCode, KeyEvent  event)
   {
	   if(keyCode == KeyEvent .KEYCODE_BACK)
	   {
		   mainHandler.sendEmptyMessage(MyFinanceActivity.ABORT_LOAD);
	   }
	   return super.onKeyDown(keyCode, event) ;
   }
   public static void setMainHandler(Handler h)
   {
	   mainHandler = h ;
   }
   
   public static void sendMessage(Message msg)
   {
	   mHandler.sendMessage(msg);
   }
   
   public static void sendMessage(int w){
	   mHandler.sendEmptyMessage(w);
   }
}

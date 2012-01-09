package it.util;

import it.dev.MyFinanceDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Quotes.QuotationContainer;
import Quotes.QuotationType;
import Requests.Request;
import android.database.Cursor;

import com.google.gson.Gson;

public class UpdateUtils {
	
	private MyFinanceDatabase db;
	private GregorianCalendar today;
	
	public UpdateUtils(){
		
		today = (GregorianCalendar) Calendar.getInstance();
		GregorianCalendar upDate = (GregorianCalendar) Calendar.getInstance();
		today.add(Calendar.MINUTE, -30);
		//Cursor c = db.getDetailsOfPortfolio(portfolioName);
//		String updateDate = c.getString(4);
//		String[] updateString	= updateDate.split("[/: ]");
//		upDate.set(Integer.parseInt(updateString[2]), Integer.parseInt(updateString[1])-1, Integer.parseInt(updateString[0]), Integer.parseInt(updateString[3]), Integer.parseInt(updateString[4]), Integer.parseInt(updateString[5]));
//		if(today.after(upDate)){
//		}
		
		
	}
	
	public QuotationContainer updatePortfolio(String portfolioName){
		
		QuotationContainer quotCont = new QuotationContainer();
		ArrayList<Request> array = new ArrayList<Request>();		
		
		Cursor c_bond = db.getAllBondOverviewInPortfolio(portfolioName);
		Cursor c_fund = db.getAllFundOverviewInPortfolio(portfolioName);
		Cursor c_share = db.getAllShareOverviewInPortfolio(portfolioName);
		
    	while(c_bond.moveToNext()){
    		
    		Cursor c = db.getBondDetails(c_bond.getString(1));
    		String date = c.getString(27);
    		String[] ud	= date.split("[/: ]");
//    		upDate.set(Integer.parseInt(ud[2]), Integer.parseInt(ud[1])-1, Integer.parseInt(ud[0]), Integer.parseInt(ud[3]), Integer.parseInt(ud[4]), Integer.parseInt(ud[5]));
//    		today.roll(4, -30);
//    		if(today.after(upDate)){
//    			Request req = new Request(c_bond.getColumnName(1), QuotationType.BOND, "prefsite");
//    			array.add(req);
//    		}
			
		}
    	
    	while(c_fund.moveToNext()){
    		
    		Cursor c = db.getFundDetails(c_fund.getString(1));
    		String date = c.getString(27);
    		//trasformo la data da String a Calendar (magari da fare in un metodo separato)
    		String[] ud	= date.split("[/: ]");
//    		upDate.set(Integer.parseInt(ud[2]), Integer.parseInt(ud[1])-1, Integer.parseInt(ud[0]), Integer.parseInt(ud[3]), Integer.parseInt(ud[4]), Integer.parseInt(ud[5]));
//    		//prendo la data di desso e torno indietro di 30min... spero funzioni
//    		today.roll(4, -30);
//    		
//    		if(today.after(upDate)){
//    			Request req = new Request(c_fund.getColumnName(1), QuotationType.FUND, "prefSite");
//    			array.add(req);
//    		}
    	}
    	
    	while(c_share.moveToNext()){
    		
    		Cursor c = db.getShareDetails(c_share.getString(1));
    		String date = c.getString(27);
    		String[] ud	= date.split("[/: ]");
//    		upDate.set(Integer.parseInt(ud[2]), Integer.parseInt(ud[1])-1, Integer.parseInt(ud[0]), Integer.parseInt(ud[3]), Integer.parseInt(ud[4]), Integer.parseInt(ud[5]));
//    		today.roll(4, -30);
//    		if(today.after(upDate)){
//    			Request req = new Request(c_share.getColumnName(1), QuotationType.SHARE, "prefSite");
//    			array.add(req);
//    		}
    	}
    	
    	Gson converter = new Gson();
		String jsonReq = converter.toJson(array);
		String jsonResponse = ConnectionUtils.postData(jsonReq);
		
		if(jsonResponse != null){
			quotCont = ResponseHandler.decodeQuotations(jsonResponse);
			return quotCont;
		}
		else return null;	
	}
	
	public QuotationContainer forcedUpdate(String isin, String type, ArrayList<String> blackList){
		
		QuotationContainer quotCont = new QuotationContainer();
		ArrayList<Request> array = new ArrayList<Request>();
		QuotationType qType = null;
		if(type == "bond") qType = QuotationType.BOND;
		if(type == "fund") qType = QuotationType.FUND;
		if(type == "share") qType = QuotationType.SHARE;
		
		Request req = new Request(isin, qType, blackList);
		array.add(req);
		Gson converter = new Gson();
		String jsonReq = converter.toJson(array);
		String jsonResponse = ConnectionUtils.postData(jsonReq);
		
		if(jsonResponse != null){
			quotCont = ResponseHandler.decodeQuotations(jsonResponse);
			return quotCont;
		}
		else return null;
	}
	
}

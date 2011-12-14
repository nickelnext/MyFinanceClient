package it.util;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.dev.MyFinanceDatabase;
import it.util.ConnectionUtils;

public class DbUpdate {
	
	private MyFinanceDatabase db;
	
	public DbUpdate(){

	}
	
	public int insert(String IDCOD) throws JSONException{
		
		Gson gson = new Gson();
		ArrayList<Request> list = new ArrayList<Request>();
		Request req = new Request(IDCOD);
		list.add(req);
		String string = gson.toJson(list);
		String risp = ConnectionUtils.postData(string);
		JSONObject json = new JSONObject(risp);
		
		db.open();
		
		if(json.getString("type")=="FOUND"){
			
			db.addNewFund(1, json.getString("ISIN"), name, manager, category, benchmark, lastPrize, lastPrizeDate, precPrize, currency, percVariation, variation, performance1Month, performance3Month, performance1Year, performance3Year, lastUpdate);
				
		}
		else if (json.getString("type")=="BOND"){
			
			db.addNewBond(1, isin, name, currency, market, marketPhase, lastContractPrice, percVariation, variation, lastContractDate, lastVolume, buyVolume, sellVolume, totalVolume, buyPrice, sellPrice, maxToday, minToday, maxYear, minYear, maxYearDate, minYearDate, lastClose, expirationDate, couponDate, coupon, minRoundLot, lastUpdate);

		}
		else{
			
			db.addNewShare(, code, name, minRoundLot, marketPhase, lastContractPrice, percVariation, variation, lastContractDate, buyPrice, sellPrice, lastAmount, buyAmount, sellAmount, totalAmount, maxToday, minToday, maxYear, minYear, maxYearDate, minYearDate, lastClose, lastUpdate);
		}
		
		db.close();
		
		return 0;
		
	}
	
	public int update(ArrayList<Request> requests) throws JSONException{
		
		Gson gson = new Gson();
		String string = gson.toJson(requests);
		String risp = ConnectionUtils.postData(string);
		JSONObject json = new JSONObject(risp);
		
		db.open();
		db.close();
		
		return 0;
		
	}
	
	public int forcedUpdate(String ISIN, ArrayList<String> sites){
		
		
		db.open();
		Request req = new Request(ISIN, "FOUND", sites);
		return 0;
		
	}
	

}

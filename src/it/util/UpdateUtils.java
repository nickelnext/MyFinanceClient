package it.util;

import it.dev.MyFinanceDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Quotes.QuotationContainer;
import Requests.Request;
import android.util.Log;

import com.google.gson.Gson;

public class UpdateUtils {
	
	private MyFinanceDatabase db;
	private Calendar today;
	
	public UpdateUtils(){
		
		today = Calendar.getInstance();
		
	}
	
	public QuotationContainer updateTools(List<String> isinList, List<String> whiteList, String blackList){
		
		if(blackList == null){
			
			if(whiteList.isEmpty()){
				
				//update normale con soli ISIN
				QuotationContainer quotCont = new QuotationContainer();
				ArrayList<Request> array = new ArrayList<Request>();
				Calendar upDate;
				
				for (int i = 0; i < isinList.size(); i++){
					
					String Date = db.getLastUpDate(isinList.get(i));//da fare
					String Year
					String Month
					today.roll(4, -30);
					
					if(today.after(upDate)){
						//passati pi� di 30 min, aggiorno
						array.add(new Request(isinList.get(i)));
						
						Gson converter = new Gson();
						String jsonReq = converter.toJson(array);
						String jsonResponse = ConnectionUtils.postData(jsonReq);
						
						if(jsonResponse != null){
							quotCont = ResponseHandler.decodeQuotations(jsonResponse);
							return quotCont;
						}
						else{
							return null;
						}
					}
					
				}
				
				
			}
			else{
				
				//update con ISIN e Lista siti preferiti
			}
		}
		else{
			
			//update con ISIN, ?lista preferiti? e sito corrotto -> forced update
		}
	}

}

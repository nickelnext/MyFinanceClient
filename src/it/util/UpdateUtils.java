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
	
	public QuotationContainer updateTools(List<String> isinList, String preferedSite, String badSite){
		
		if(badSite == null){
			
			if(preferedSite == null){
				
				//update normale con soli ISIN
				QuotationContainer quotCont = new QuotationContainer();
				ArrayList<Request> array = new ArrayList<Request>();
				Calendar upDate = Calendar.getInstance();
				
				for (int i = 0; i < isinList.size(); i++){
					
					String date = null;// = db.getLastUpDate(isinList.get(i));//da fare
					String[] ud	= date.split("// ::");//da fare espressione regolare
					upDate.set(Integer.valueOf(ud[2]), Integer.valueOf(ud[0]), Integer.valueOf(ud[1]), Integer.valueOf(ud[3]), Integer.valueOf(ud[4]), Integer.valueOf(ud[5]));
					today.roll(4, -30);
					
					if(today.after(upDate)){
						//passati più di 30 min, aggiorno
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
		return null;
	}

}

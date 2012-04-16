package it.util;

import java.lang.reflect.Type;
import java.util.ArrayList;

import other.TypeSiteObject;
import Quotes.HistoryContainer;
import Quotes.QuotationContainer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ResponseHandler {

	
	public static QuotationContainer decodeQuotations(String json) throws IllegalStateException{
		//TODO modificare a seconda di come decideremo di comprimere/convertire le  richieste
		QuotationContainer res;
		Gson converter = new Gson();	
		Type typeOfT = new TypeToken<QuotationContainer>(){}.getType();
		res = converter.fromJson(json, typeOfT);	
		return res;
	}


	public static ArrayList<TypeSiteObject> decodeDBSiteType(String json) throws IllegalStateException{
		//TODO modificare a seconda di come decideremo di comprimere/convertire le  richieste
		ArrayList<TypeSiteObject> res;
		Gson converter = new Gson();	
		Type typeOfT = new TypeToken<ArrayList<TypeSiteObject>>(){}.getType();
		res = converter.fromJson(json, typeOfT);	
		return res;
		}
	
	
	public static HistoryContainer decodeHistoryData(String json) throws IllegalStateException{
		//TODO modificare a seconda di come decideremo di comprimere/convertire le  richieste
		HistoryContainer res;
		Gson converter = new Gson();	
		Type typeOfT = new TypeToken<HistoryContainer>(){}.getType();
		res = converter.fromJson(json, typeOfT);	
		return res;
		}


}

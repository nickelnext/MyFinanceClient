package it.util;

import java.lang.reflect.Type;

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

}

package it.util;

import java.util.ArrayList;

import it.util.QuotationType;


public class Request {
	private String idCode;
	private RequestType reqType;
	
	private QuotationType quotType;
	
	private String preferredSite;
	
	private ArrayList<String> ignoredSites;


	
	//COSTRUTTORE CHE SETTA TUTTO
	// DA ELIMINARE SE SI LASCIANO QUELLI SOTTo
	public Request(String idCode, RequestType rType, QuotationType qType,
			String preferredSite, ArrayList<String> ignoredSites) { 
		this.idCode = idCode;
		this.reqType = rType;
		this.quotType = qType;
		this.preferredSite = preferredSite;
		this.ignoredSites = ignoredSites;
	}
	
	//overloading --> QUOTATION REQ
	public Request(String idCode) { 
		this.idCode = idCode;
		this.reqType = RequestType.QUOTATION;
		this.quotType = null;
		this.preferredSite = null;
		this.ignoredSites = null;
	}
	//overloading --> UPDATE REQ
	public Request(String idCode, QuotationType qType,
			String preferredSite) { 
		this.idCode = idCode;
		this.reqType = RequestType.UPDATE;
		this.quotType = qType;
		this.preferredSite = preferredSite;
		this.ignoredSites = null;
	}

	//overloading --> FORCED REQ
	public Request(String idCode, QuotationType qType,
			 ArrayList<String> ignoredSites) { 
		this.idCode = idCode;
		this.reqType = RequestType.FORCED;
		this.quotType = qType;
		this.preferredSite = null;
		this.ignoredSites = ignoredSites;
	}

	
	public String getIdCode() {
		return idCode;
	}

	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}

	public RequestType getReqType() {
		return reqType;
	}

	public void setReqType(RequestType reqType) {
		this.reqType = reqType;
	}

	public QuotationType getQuotType() {
		return quotType;
	}

	public void setQuotType(QuotationType quotType) {
		this.quotType = quotType;
	}

	public String getPreferredSite() {
		return preferredSite;
	}

	public void setPreferredSite(String preferredSite) {
		this.preferredSite = preferredSite;
	}

	public ArrayList<String> getIgnoredSites() {
		return ignoredSites;
	}

	public void setIgnoredSites(ArrayList<String> ignoredSites) {
		this.ignoredSites = ignoredSites;
	}


	
	
	
	
	
	
	
}
package Requests;

import java.util.ArrayList;
import java.util.Set;

import Quotes.QuotationType;

public class RequestContainer {
	
	private int id; //da definire: qualcosa che lo identifichi
	private Set<QuotationType> preferredTypes; //per definire eventuali preferenze
	private ArrayList<Request> reqList;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Set<QuotationType> getPreferredTypes() {
		return preferredTypes;
	}
	public void setPreferredTypes(Set<QuotationType> preferredTypes) {
		this.preferredTypes = preferredTypes;
	}
	public ArrayList<Request> getReqList() {
		return reqList;
	}
	public void setReqList(ArrayList<Request> reqList) {
		this.reqList = reqList;
	}
	
	

}

package Quotes;

import java.util.ArrayList;

public class HistoryContainer {
	
	private ArrayList<HistoricalData> historyList;
	private String comments;
	
	//se è INVALIDO --> HistoryList == null; (comments conterrà errori) 
	
	
	public HistoryContainer() {
		
	}



	public ArrayList<HistoricalData> getHistoryList() {
		return historyList;
	}



	public void setHistoryList(ArrayList<HistoricalData> historyList) {
		this.historyList = historyList;
	}



	public String getComments() {
		return comments;
	}



	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	
	
}

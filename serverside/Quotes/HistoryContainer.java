package Quotes;

import java.util.ArrayList;

public class HistoryContainer {
	
	private ArrayList<HistoricalData> historyListWeek;
	private ArrayList<HistoricalData> historyListMonth;
	private ArrayList<HistoricalData> historyListYear;
	private String comments;
	
	//se è INVALIDO --> HistoryList == null; (comments conterrà errori) 
	
	
	public HistoryContainer() {
		
	}




	public ArrayList<HistoricalData> getHistoryListWeek() {
		return historyListWeek;
	}




	public void setHistoryListWeek(ArrayList<HistoricalData> historyListWeek) {
		this.historyListWeek = historyListWeek;
	}




	public ArrayList<HistoricalData> getHistoryListMonth() {
		return historyListMonth;
	}




	public void setHistoryListMonth(ArrayList<HistoricalData> historyListMonth) {
		this.historyListMonth = historyListMonth;
	}




	public ArrayList<HistoricalData> getHistoryListYear() {
		return historyListYear;
	}




	public void setHistoryListYear(ArrayList<HistoricalData> historyListYear) {
		this.historyListYear = historyListYear;
	}




	public String getComments() {
		return comments;
	}



	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	
	
}

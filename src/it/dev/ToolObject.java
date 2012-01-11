package it.dev;

import java.util.ArrayList;

public class ToolObject 
{
	public ToolObject()
	{
		
	}
	
	private String ISIN;
	private String type;
	private String preferredSite;
	private ArrayList<String> ignoredSites;
	private String purchaseDate;
	private String purchasePrice;
	private String roundLot;
	
	
	public String getISIN() {
		return ISIN;
	}
	public void setISIN(String iSIN) {
		ISIN = iSIN;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public String getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public String getRoundLot() {
		return roundLot;
	}
	public void setRoundLot(String roundLot) {
		this.roundLot = roundLot;
	}
	
}

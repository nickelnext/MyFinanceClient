package Quotes;

import java.util.ArrayList;

public class QuotationContainer {

	private ArrayList<Quotation_Bond> bondList;
	private ArrayList<Quotation_Fund> fundList;
	private ArrayList<Quotation_Share> shareList;
	private ArrayList<Quotation_Invalid> invalidList;
	private String comments;

	
	
	public QuotationContainer() {
		
	}
	
	public ArrayList<Quotation_Bond> getBondList() {
		if(null == this.bondList){
			this.bondList = new ArrayList<Quotation_Bond>();
		}
		return bondList;
	}
	public void setBondList(ArrayList<Quotation_Bond> bondList) {
		this.bondList = bondList;
	}

	
	public ArrayList<Quotation_Fund> getFundList() {
		if(null == this.fundList){
			this.fundList = new ArrayList<Quotation_Fund>();
		}
		return fundList;
	}
	public void setFundList(ArrayList<Quotation_Fund> fundList) {
		this.fundList = fundList;
	}


	public ArrayList<Quotation_Share> getShareList() {
		if(null == this.shareList){
			this.shareList = new ArrayList<Quotation_Share>();
		}
		return shareList;
	}
	public void setShareList(ArrayList<Quotation_Share> shareList) {
		this.shareList = shareList;
	}

	public ArrayList<Quotation_Invalid> getInvalidList() {
		if(null == this.invalidList){
			this.invalidList = new ArrayList<Quotation_Invalid>();
		}
		return invalidList;
	}
	public void setInvalidList(ArrayList<Quotation_Invalid> invalidList) {
		this.invalidList = invalidList;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	


}

package it.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import com.google.gson.Gson;

import Quotes.QuotationContainer;
import Quotes.QuotationType;
import Quotes.Quotation_Bond;
import Quotes.Quotation_Fund;
import Quotes.Quotation_Share;
import Requests.Request;
import android.database.Cursor;
import it.dev.MyFinanceDatabase;
import it.util.UpdateUtils;

public class UpdateTimeTask extends TimerTask{
	
	private static ArrayList<String> portfolii = new ArrayList<String>();
	private QuotationContainer quotCont = new QuotationContainer();
	private ArrayList<String> shareIsinArrayList = new ArrayList<String>();
	private GregorianCalendar today, upDate;
	private MyFinanceDatabase db;
	//private UpdateUtils up;
	
	public UpdateTimeTask(){
		
	}
	
	public void run(){
		for(String s : portfolii){
			//up.updatePortfolio(s);
			today = (GregorianCalendar) Calendar.getInstance();
			upDate = (GregorianCalendar) Calendar.getInstance();
			today.add(Calendar.MINUTE, -30);
			Cursor c = db.getDetailsOfPortfolio(s);
			String updateDate = c.getString(4);
			String[] updateString	= updateDate.split("[/: ]");
			upDate.set(Integer.parseInt(updateString[2]), Integer.parseInt(updateString[1])-1, Integer.parseInt(updateString[0]), Integer.parseInt(updateString[3]), Integer.parseInt(updateString[4]), Integer.parseInt(updateString[5]));
			
			if(today.after(upDate)){
				
				ArrayList<Request> array = new ArrayList<Request>();		
				
				Cursor c_bond = db.getAllBondOverviewInPortfolio(s);
				Cursor c_fund = db.getAllFundOverviewInPortfolio(s);
				Cursor c_share = db.getAllShareOverviewInPortfolio(s);
			
    			while(c_bond.moveToNext()){
    				array.add(new Request(c_bond.getColumnName(1), QuotationType.BOND, "__NONE__"));
    			}
    			while(c_fund.moveToNext()){
    				array.add(new Request(c_fund.getColumnName(1), QuotationType.FUND, "__NONE__"));
    			}
    			while(c_share.moveToNext()){
    				array.add(new Request(c_share.getColumnName(1), QuotationType.SHARE, "__NONE__"));
    			}
    			
    			try {
    				   				
    				Gson converter = new Gson();
    				String jsonReq = converter.toJson(array);
    				String jsonResponse = ConnectionUtils.postData(jsonReq);
    				if(jsonResponse != null){
    					quotCont = ResponseHandler.decodeQuotations(jsonResponse);
    				}
    				else{
    					System.out.println("Empty jsonResponse");
    				}
    			} catch (Exception e) {
    				System.out.println("connection ERROR");
    			}
			}
			
			db.open();

			if(quotCont!=null)
			{
				
				int totalQuotationReturned = quotCont.getBondList().size() + quotCont.getFundList().size() + quotCont.getShareList().size();
				
				if(allIsinRequestedAreReturned(shareIsinArrayList, quotCont)){
					if(totalQuotationReturned != shareIsinArrayList.size()){
						//ne ho ricevuti di più rispetto a quelli richiesti....
						System.out.println("ne ho ricevuti di più rispetto a quelli richiesti....");
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(shareIsinArrayList, quotCont);
						for (int i = 0; i < listaIsinNotRequested.size(); i++){
							System.out.println(listaIsinNotRequested.get(i));							
						}
						
						//elimino quelli non richiesti dal container...
						System.out.println("elimino quelli non richiesti dal container...");
						for (int i = 0; i < listaIsinNotRequested.size(); i++){
							for(Quotation_Bond qb : quotCont.getBondList())	{
								if(qb.getISIN().equals(listaIsinNotRequested.get(i))){
									quotCont.getBondList().remove(qb);
								}
							}
							for(Quotation_Fund qf : quotCont.getFundList()){
								if(qf.getISIN().equals(listaIsinNotRequested.get(i))){
									quotCont.getFundList().remove(qf);
								}
							}
							for(Quotation_Share qs : quotCont.getShareList()){
								if(qs.getISIN().equals(listaIsinNotRequested.get(i))){
									quotCont.getShareList().remove(qs);
								}
							}
						}
					}
				}
				else
				{
					//alcuni di quelli richiesti non sono stati tornati....
					System.out.println("alcuni di quelli richiesti non sono stati tornati....");
					ArrayList<String> listaIsinNotReturned = searchIsinNotReturned(shareIsinArrayList, quotCont);
					for (int i = 0; i < listaIsinNotReturned.size(); i++){
						System.out.println(listaIsinNotReturned.get(i));
					}
				}
				
				//UPDATE IN DATABASE <BOND/FUND/SHARE> OF 'container'
				for(Quotation_Bond qb : quotCont.getBondList())	{
					try {
						db.updateSelectedBondByQuotationObject(qb, getTodaysDate());
					} catch (Exception e) {
						System.out.println("Database update error");
					}
				}
				
				//4. for all FUND returned...
				for(Quotation_Fund qf : quotCont.getFundList()){
					//4.1 control if fund already exist in database --> UPDATE
					//UPDATE
					try {
						db.updateSelectedFundByQuotationObject(qf, getTodaysDate());
					} catch (Exception e) {
						System.out.println("Database update error");
					}
				}
				
				//5. for all SHARE returned...
				for(Quotation_Share qs : quotCont.getShareList())
				{
					//5.1 control if share already exist in database --> UPDATE
					//UPDATE
					try {
						db.updateSelectedShareByQuotationObject(qs, getTodaysDate());
					} catch (Exception e) {
						System.out.println("Database update error");
					}
				}
				
				//update portfolio lastupdate field...
				db.updateSelectedPortfolioLastUpdate(s, getTodaysDate());
				
			}
			else{
				//connection error!
				System.out.println("Connection error");
				
			}
						
			db.close();
			
		}
    }
	
	public void add(String portfolioName){
		portfolii.add(portfolioName);
	}
	
	private boolean allIsinRequestedAreReturned(ArrayList<String> isinList, QuotationContainer container){
		boolean result = false;		
		ArrayList<String> support = new ArrayList<String>();
		for(Quotation_Bond qb : container.getBondList()){
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList()){
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList()){
			support.add(qs.getISIN());
		}		
		for (int i = 0; i < isinList.size(); i++){
			if(support.contains(isinList.get(i)))result = true;
		}
		return result;
	}
	
	private ArrayList<String> searchIsinNotRequested(ArrayList<String> isinList, QuotationContainer container){
		ArrayList<String> result = new ArrayList<String>();		
		ArrayList<String> support = new ArrayList<String>();
		for(Quotation_Bond qb : container.getBondList()){
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList()){
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList()){
			support.add(qs.getISIN());
		}
		for (int i = 0; i < support.size(); i++){
			if(!isinList.contains(support.get(i)))result.add(support.get(i));
		}		
		return result;
	}
	
	private ArrayList<String> searchIsinNotReturned(ArrayList<String> isinList, QuotationContainer container){
		ArrayList<String> result = new ArrayList<String>();		
		ArrayList<String> support = new ArrayList<String>();
		for(Quotation_Bond qb : container.getBondList()){
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList()){
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList()){
			support.add(qs.getISIN());
		}
		
		for (int i = 0; i < isinList.size(); i++){
			if(!support.contains(isinList.get(i)))result.add(isinList.get(i));
		}		
		return result;
	}
	
	private String getTodaysDate(){
	    final GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
	    return(new StringBuilder()
	            .append(c.get(Calendar.MONTH) + 1).append("/")
	            .append(c.get(Calendar.DAY_OF_MONTH)).append("/")
	            .append(c.get(Calendar.YEAR)).append(" ")
	            .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
	            .append(c.get(Calendar.MINUTE)).append(":")
	            .append(c.get(Calendar.SECOND)).append(" ")).toString();
	}		
}


/**
da inserire nel codice:

UpdateTimeTask upTask = new UpdateTimeTask();
Timer timer = new Timer();

//quando apro un portafolgio e quindi devo ricordarmi di aggiornarlo
upTask.add(portfolioName);

//faccio partire il timer
timer.schedule(new UpdateTask(), 100, 200);

//cancello il timer(e quindi l'update automatico)
timer.cancel();
**/
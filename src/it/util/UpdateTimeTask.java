package it.util;

import it.dev.MyFinanceDatabase;
import it.dev.ToolObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import Quotes.QuotationContainer;
import Quotes.QuotationType;
import Quotes.Quotation_Bond;
import Quotes.Quotation_Fund;
import Quotes.Quotation_Share;
import Requests.Request;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;

public class UpdateTimeTask extends TimerTask
{
	
	private static ArrayList<String> portfolii = new ArrayList<String>();
	
	private MyFinanceDatabase db;
	
	private QuotationContainer quotCont = new QuotationContainer();
	
	private Context ctx;
	
	public UpdateTimeTask(Context ctx)
	{
		this.ctx = ctx;
		db = new MyFinanceDatabase(this.ctx);	
	}
	
	public void run()
	{
		db.open();
		
		System.out.println("AUTOMATIC UPDATE RUN!");
		
		for(String s : portfolii)
		{
			ArrayList<ToolObject> toolListForPortfolioS = new ArrayList<ToolObject>();
			ArrayList<Request> array = new ArrayList<Request>();		
			
			System.out.println("Start automatic update of portfolio: "+s);
			
			
		//1.//cursors to get all tools in selected portfolio....
			Cursor c_bond = db.getAllBondOverviewInPortfolio(s);
			Cursor c_fund = db.getAllFundOverviewInPortfolio(s);
			Cursor c_share = db.getAllShareOverviewInPortfolio(s);
			
			
			
		//2.//get all tools in selected portfolio and save them....
			if(c_bond.getCount()!=0)
			{
				c_bond.moveToFirst();
				do {
					toolListForPortfolioS.add(new ToolObject(c_bond.getString(2), "bond", c_bond.getString(3), String.valueOf(c_bond.getFloat(4)), String.valueOf(c_bond.getInt(5))));
				} while (c_bond.moveToNext());
			}
			
			if(c_fund.getCount()!=0)
			{
				c_fund.moveToFirst();
				do {
					toolListForPortfolioS.add(new ToolObject(c_fund.getString(2), "fund", c_fund.getString(3), String.valueOf(c_fund.getFloat(4)), String.valueOf(c_fund.getInt(5))));
				} while (c_fund.moveToNext());
			}
			
			if(c_share.getCount()!=0)
			{
				c_share.moveToFirst();
				do {
					toolListForPortfolioS.add(new ToolObject(c_share.getString(2), "share", c_share.getString(3), String.valueOf(c_share.getFloat(4)), String.valueOf(c_share.getInt(5))));
				} while (c_share.moveToNext());
			}
			
			
			
			
		//3.//close cursors....
			c_bond.close();
			c_fund.close();
			c_share.close();
			
			
		//4.//for all tools saved get preferred site and ignored sites....
			
			Cursor details = null;
			
			ArrayList<String> ignoredSites = new ArrayList<String>();
			
			for (int i = 0; i < toolListForPortfolioS.size(); i++) 
			{
				ignoredSites.clear();
				
				if(toolListForPortfolioS.get(i).getType().equals("bond"))
				{
					details = db.getBondDetails(toolListForPortfolioS.get(i).getISIN());
				}
				else if(toolListForPortfolioS.get(i).getType().equals("fund"))
				{
					details = db.getFundDetails(toolListForPortfolioS.get(i).getISIN());
				}
				else if(toolListForPortfolioS.get(i).getType().equals("share"))
				{
					details = db.getShareDetails(toolListForPortfolioS.get(i).getISIN());
				}
				
				
				
				if(details!=null)
				{
					details.moveToFirst();
					toolListForPortfolioS.get(i).setPreferredSite(details.getString(details.getColumnIndex("sitoPreferito")));
					String[] arraySplit = details.getString(details.getColumnIndex("sitiIgnorati")).split(" ");
					
					for (String string : arraySplit) 
					{
						ignoredSites.add(string);
					}
					
					toolListForPortfolioS.get(i).setIgnoredSites(ignoredSites);
				}
			}
			
			//save quotation types....
			ArrayList<QuotationType> typeArray = new ArrayList<QuotationType>();
			for (int i = 0; i < toolListForPortfolioS.size(); i++) 
			{
				if(toolListForPortfolioS.get(i).getType().equals("bond"))
				{
					typeArray.add(QuotationType.BOND);
				}
				else if(toolListForPortfolioS.get(i).getType().equals("fund"))
				{
					typeArray.add(QuotationType.FUND);
				}
				else if(toolListForPortfolioS.get(i).getType().equals("share"))
				{
					typeArray.add(QuotationType.SHARE);
				}
				else
				{
					System.out.println("Type error.");
				}
			}
			
			//prepare Array of Request........
			for (int i = 0; i < toolListForPortfolioS.size(); i++) 
			{
				array.add(new Request(toolListForPortfolioS.get(i).getISIN(), typeArray.get(i), toolListForPortfolioS.get(i).getPreferredSite(), toolListForPortfolioS.get(i).getIgnoredSites()));
			}
			
			//call server....
			try 
			{
				Gson converter = new Gson();
				String jsonReq = converter.toJson(array);
				System.out.println("RICHIESTA: "+jsonReq);
				String jsonResponse = ConnectionUtils.postData(jsonReq);
				if(jsonResponse != null)
				{
					quotCont = ResponseHandler.decodeQuotations(jsonResponse);
				}
				else
				{
					System.out.println("Empty jsonResponse");
				}
			}
			catch (Exception e) 
			{
				System.out.println("connection ERROR");
			}

			if(quotCont!=null)
			{
			
				int totalQuotationReturned = quotCont.getBondList().size() + quotCont.getFundList().size() + quotCont.getShareList().size();
			
				if(allIsinRequestedAreReturned(toolListForPortfolioS, quotCont))
				{
					if(totalQuotationReturned != toolListForPortfolioS.size())
					{
						//ne ho ricevuti di più rispetto a quelli richiesti....
						System.out.println("ne ho ricevuti di più rispetto a quelli richiesti....");
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(toolListForPortfolioS, quotCont);
						for (int i = 0; i < listaIsinNotRequested.size(); i++)
						{
							System.out.println(listaIsinNotRequested.get(i)+" NOT REQUESTED");							
						}
					
						//elimino quelli non richiesti dal container...
						System.out.println("elimino quelli non richiesti dal container...");
						for (int i = 0; i < listaIsinNotRequested.size(); i++)
						{
							for(Quotation_Bond qb : quotCont.getBondList())	
							{
								if(qb.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									quotCont.getBondList().remove(qb);
								}
							}
							for(Quotation_Fund qf : quotCont.getFundList())
							{
								if(qf.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									quotCont.getFundList().remove(qf);
								}
							}
							for(Quotation_Share qs : quotCont.getShareList())
							{
								if(qs.getISIN().equals(listaIsinNotRequested.get(i)))
								{
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
					ArrayList<ToolObject> listaIsinNotReturned = searchIsinNotReturned(toolListForPortfolioS, quotCont);
					for (int i = 0; i < listaIsinNotReturned.size(); i++)
					{
						System.out.println(listaIsinNotReturned.get(i).getISIN()+" is not returned by Server");
					}
				}
			
				//UPDATE IN DATABASE <BOND/FUND/SHARE> OF 'container'
				for(Quotation_Bond qb : quotCont.getBondList())	
				{
					try {
						db.updateSelectedBondByQuotationObject(qb, getTodaysDate());
					} catch (Exception e) {
						System.out.println("Database update error");
					}
				}
			
				//4. for all FUND returned...
				for(Quotation_Fund qf : quotCont.getFundList())
				{
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
			else
			{
				//connection error!
				System.out.println("Connection error");
			}
		}		
		db.close();
    }
	
	public static void add(String portfolioName)
	{
		if(!portfolii.contains(portfolioName)) 
		{
			portfolii.add(portfolioName);
		}
	}
	
	//function that control if all the isin requested are returned...
	private boolean allIsinRequestedAreReturned(ArrayList<ToolObject> toolList, QuotationContainer container)
	{
		boolean result = false;

		ArrayList<String> support = new ArrayList<String>();
		ArrayList<String> isinList = new ArrayList<String>();

		for(ToolObject obj : toolList)
		{
			isinList.add(obj.getISIN());
		}

		for(Quotation_Bond qb : container.getBondList())
		{
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList())
		{
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList())
		{
			support.add(qs.getISIN());
		}

		for (int i = 0; i < isinList.size(); i++) 
		{
			if(support.contains(isinList.get(i)))
			{
				result = true;
			}
		}

		return result;
	}
	
	//function that returns an array list of the Isin returned by server but not requested by client...
	private ArrayList<String> searchIsinNotRequested(ArrayList<ToolObject> toolList, QuotationContainer container)
	{
		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> support = new ArrayList<String>();
		ArrayList<String> isinList = new ArrayList<String>();

		for(ToolObject obj : toolList)
		{
			isinList.add(obj.getISIN());
		}

		for(Quotation_Bond qb : container.getBondList())
		{
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList())
		{
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList())
		{
			support.add(qs.getISIN());
		}

		for (int i = 0; i < support.size(); i++) 
		{
			if(!isinList.contains(support.get(i)))
			{
				result.add(support.get(i));
			}
		}

		return result;
	}
	
	//function that returns an array list of the Isin not returned by server but requested by client...
	private ArrayList<ToolObject> searchIsinNotReturned(ArrayList<ToolObject> toolList, QuotationContainer container)
	{
		ArrayList<ToolObject> result = new ArrayList<ToolObject>();

		ArrayList<String> support = new ArrayList<String>();


		for(Quotation_Bond qb : container.getBondList())
		{
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList())
		{
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList())
		{
			support.add(qs.getISIN());
		}

		for (int i = 0; i < toolList.size(); i++) 
		{
			if(!support.contains(toolList.get(i).getISIN()))
			{
				result.add(toolList.get(i));
			}
		}

		return result;
	}
	
	private String getTodaysDate()
	{
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
package myUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class UtilFuncs {

	public static final String ISIN_REPLACE = "__ISIN__HERE__";
	//	public static final String ISIN_NOT_FOUND ="ISIN_NOT_FOUND";
	public static final String[] datePatterns = {
		"dd/MM/yyyy",
		"dd/MM/yyyy HH.mm",
		"dd/MM/yyyy HH.mm.ss",
		"dd/MM/yyyy HH:mm",
		"dd/MM/yyyy HH:mm:ss",
		//
		"dd/MM/yyyy - HH.mm",
		"dd/MM/yyyy - HH.mm.ss",
		"dd/MM/yyyy - HH:mm",
		"dd/MM/yyyy - HH:mm:ss",
		//
		"dd-MM-yyyy",
		"dd-MM-yyyy HH.mm",
		"dd-MM-yyyy HH.mm.ss",
		"dd-MM-yyyy HH:mm",
		"dd-MM-yyyy HH:mm:ss",
		//
		"HH.mm",
		"HH.mm.ss",
		//
		"HH:mm",
		"HH:mm:ss",
	};
	public static final String[] datePatternsEng = {
		"MM/dd/yyyy",
		"MM/dd/yyyy HH.mm",
		"MM/dd/yyyy HH.mm.ss",
		"MM/dd/yyyy HH:mm",
		"MM/dd/yyyy HH:mm:ss",
		//
		"MM/dd/yyyy - HH.mm",
		"MM/dd/yyyy - HH.mm.ss",
		"MM/dd/yyyy - HH:mm",
		"MM/dd/yyyy - HH:mm:ss",
		//
		"MM-dd-yyyy",
		"MM-dd-yyyy HH.mm",
		"MM-dd-yyyy HH.mm.ss",
		"MM-dd-yyyy HH:mm",
		"MM-dd-yyyy HH:mm:ss",
		//
		"HH.mm",
		"HH.mm.ss",
		//
		"HH:mm",
		"HH:mm:ss",
	};

	public static final String countryUs = "us";
	public static final String countryIt = "it";
	public static final String countryDefault = "it";



	public static String getString(NodeList nodes, int n)
	{
		if(!nodes.item(n).hasChildNodes() || nodes.item(n).getFirstChild().getNodeValue() == null)
			return "";
		return nodes.item(n).getFirstChild().getNodeValue();
	}
	public static String getString(Document doc, String nodeName)
	{
			return doc.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
	}


	public static boolean isISIN(String s)
	{
		if(s.length()==12 && s.matches("[A-Z]{2}\\d{10}"))
			return true;
		return false;
	}
	public static float repFloat(String string, String country) {
		Locale l;
		
		if (country == UtilFuncs.countryUs){
			l = Locale.US;
		}else if(country == UtilFuncs.countryIt){
			l = Locale.ITALY;
		}else{
			l = Locale.ITALY;
		}
		
		if(string == "")
			return 0;
		if(string.matches("\\D+"))
			return 0;
		try 
		{
			Number number = NumberFormat.getNumberInstance(l).parse(string);
			return number.floatValue();
		} 
		catch (ParseException e) 
		{
			if(string.matches("\\D+"))
			{
				System.out.println("ho matchato NA");
				return 0;
			}
		string = string.replace(",", ".");
		string = string.replace("%","");
		return Float.valueOf(string);
		}
	}
	public static int repInteger(String string) {

		if(string == "")
			return 0;
		try 
		{
			Number number = NumberFormat.getNumberInstance(Locale.ITALY).parse(string);
			return number.intValue();
		} 
		catch (ParseException e) 
		{
			if(string.matches("\\D+"))
			{
				System.out.println("ho matchato NA");
				return 0;
			}
			string = string.replace(".", "");
			string = string.replace(",", "");
			return Integer.valueOf(string);
		}
	}

	//	01/11/11 - 11.33.42


	public static Date formatDate(String s, String country) {
		String[] datepattern;
		if (country == UtilFuncs.countryUs){
			datepattern = datePatternsEng;
		}else if(country == UtilFuncs.countryIt){
			datepattern = datePatterns;
		}else{
			datepattern = datePatterns;
		}
		
		
		Date date = null;
		int i=0;
		DateFormat formatter;
		boolean found=false;
		while(!found && i< datepattern.length)
		{
			formatter = new SimpleDateFormat(datepattern[i]);
			try {
				//
				date = formatter.parse(s);
				if(!datepattern[i].contains("/") && !datepattern[i].contains("-"))
				{
					//only time of the day i set, no date, so we need to add it using calendar
					GregorianCalendar today = new GregorianCalendar();
					GregorianCalendar cal = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 
							today.get(Calendar.DAY_OF_MONTH), date.getHours(), date.getMinutes(), date.getSeconds());
					date = cal.getTime();
				}
				found = true;
			}
			catch (ParseException e) {
				i++;
			}
		}
		return date;
	}
	
	
	

}

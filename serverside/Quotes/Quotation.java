package Quotes;

import java.lang.reflect.Field;
import Quotes.QuotationType;
import Utils.UtilFuncs;

public abstract class Quotation {

	protected String name;
	protected String ISIN;
	protected QuotationType type;
	protected String site;
	protected String siteUrl;
	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	//this field is used to know which language the website is, so we can parse the dates using dd/MM/yyyy or MM/dd/yyyy.
	private String country;
	
	
	public Quotation() {
		super();
		country = UtilFuncs.countryDefault;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getISIN() {
		return this.ISIN;
	}
	public void setISIN(String isin) {
		this.ISIN = isin;
	}
	public QuotationType getType() {
		return type;
	}
	public void setType(QuotationType type) {
		this.type = type;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();
        
        //print field names paired with their values
        for ( Field field : fields  ) {
          result.append("  ");
          try {
            result.append( field.getName() );
            result.append(": ");
            //requires access to private field:
            result.append( field.get(this));
          } catch ( IllegalAccessException ex ) {
          }
          result.append(newLine);
        }
        result.append("}");

        return result.toString();
      }

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	


	
}

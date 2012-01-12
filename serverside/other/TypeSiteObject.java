package other;

import java.util.ArrayList;

public class TypeSiteObject {
	String type;
	ArrayList<String> sites;

	
	public TypeSiteObject(String type, ArrayList<String> sites) {
		super();
		this.type = type;
		this.sites = sites;
	}
	
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<String> getSites() {
		return sites;
	}
	public void setSites(ArrayList<String> sites) {
		this.sites = sites;
	}
	
	
}

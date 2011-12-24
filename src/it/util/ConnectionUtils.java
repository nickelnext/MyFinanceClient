package it.util;




import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;



public class ConnectionUtils {
	
//	private static String URL = "http://192.168.0.2:8083/Pinellas/MainServlet";
//	private static String URL = "http://bbcentrale.dyndns-server.com:8083/Pinellas/MainServlet";
	private static String JSONREQ = "[{\"idCode\":\"IT0004572910\",\"reqType\":\"UPDATE\",\"quotType\":\"BOND\",\"preferredSite\":\"__NONE__\"}]";

	//only for testing purpose
	public static void main(String[] args) {
		//System.out.println(getInputStreamFromUrl(JSONREQ));
		postData(JSONREQ);
		readFile();
	
	}
	
	
	public ConnectionUtils(){

	}
	

	// INPUT: json request 
	//OUTPUT: json response
	
	public static String postData(String jsonReq) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(readFile());
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//	        nameValuePairs.add(new BasicNameValuePair("username", "12345"));
//	        nameValuePairs.add(new BasicNameValuePair("password", "PINO"));
	        nameValuePairs.add(new BasicNameValuePair("json", jsonReq));	        	        
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        // Execute HTTP Post Request	        
	        HttpResponse response = httpclient.execute(httppost);
	        
	        InputStream content = response.getEntity().getContent();
	        String result = convertStreamToString(content);
	        System.out.println(result);
	        return result;
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	return null;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	return null;
	    }
	} 
	
	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}


	public static String readFile() {
//	    String path = "C:/Users/El Pine/Documents/UNIPINE/oliveto-giani-client/myFinance/url.txt";
	  String path = "/data/app/url.txt";
	  
	    String res = null;
	    try {
	      File file = new File(path);
	      FileReader fr = new FileReader(file);
	      BufferedReader br = new BufferedReader(fr);
	      res = br.readLine();
	      br.close();
	      System.out.println(res);
	    }
	    catch(IOException e) { 
	      e.printStackTrace();
	    }
	    return res;
	  }
}


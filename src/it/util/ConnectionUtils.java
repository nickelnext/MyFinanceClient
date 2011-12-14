package it.util;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
//import org.json.JSONObject;



public class ConnectionUtils {
	
	private static String URL = "http://192.168.1.134:8081/Pinellas/MainServlet";
	private static String JSONREQ = "[{\"idCode\":\"IT0004572910\",\"reqType\":\"UPDATE\",\"quotType\":\"BOND\",\"preferredSite\":\"__NONE__\"},{\"idCode\":\"IT0004719297\",\"reqType\":\"UPDATE\",\"quotType\":\"BOND\",\"preferredSite\":\"Borsaitaliana_it\"},{\"idCode\":\"IT0004220627\",\"reqType\":\"QUOTATION\"},{\"idCode\":\"IT0003926547\",\"reqType\":\"QUOTATION\"},{\"idCode\":\"IT0001233417\",\"reqType\":\"QUOTATION\"},{\"idCode\":\"LU0336083497\",\"reqType\":\"QUOTATION\"},{\"idCode\":\"US38259P5089\",\"reqType\":\"QUOTATION\"},{\"idCode\":\"IT0003406334\",\"reqType\":\"QUOTATION\"},{\"idCode\":\"IT0004168826\",\"reqType\":\"QUOTATION\"},{\"idCode\":\"IT0000382983\",\"reqType\":\"QUOTATION\"}]";

	public static void main(String[] args) {
		//System.out.println(getInputStreamFromUrl(JSONREQ));
		postData(JSONREQ);
		System.out.println("DONE");
	}
	
	
	public ConnectionUtils(){

	}
	

	// INPUT: json request 
	//OUTPUT: json response
	
	public static String postData(String jsonReq) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(URL);

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
}


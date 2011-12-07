package it.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

public class Conn {
	
	private String URL = "www.tuamamma.it";
	private InputStream content = null;
	
	public Conn(){

	}
	
	public JSONObject getInputStreamFromUrl(JSONObject json){
		
		try {
      		HttpClient httpclient = new DefaultHttpClient();
      		HttpPost httpPost = new HttpPost(URL);
      		StringEntity entity = new StringEntity(json.toString());
      		entity.setContentEncoding("UTF-8");
      		entity.setContentType("application/json");				
      		httpPost.setEntity(entity);
      		// Execute HTTP Post Request
      		HttpResponse response = httpclient.execute(httpPost);
      		content = response.getEntity().getContent();
      		String result = convertStreamToString(content);
      		JSONObject jres = new JSONObject(result);
      		return jres;
    }	
	catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
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
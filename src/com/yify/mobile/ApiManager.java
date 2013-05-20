package com.yify.mobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ApiManager {
	
	private String baseURL = "http://yify-torrents.com/api/";
	
	public ApiManager() {
	}
	
	public ArrayList<UpcomingObject> getUpcoming() {
		
		ArrayList<UpcomingObject> results = new ArrayList<UpcomingObject>();
		
		try {
			
			URL url = new URL(baseURL + "upcoming.json");
			String result = this.callApi(url, "GET");
			
			
			JSONArray data = null;
			
			try {
				if(result.contains("\"error\" : ")) {
					return results;
				}
				
				data = new JSONArray(result);
				
				if((data != null) && (data.length() != 0)) {
					
					for(int i = 0; i < data.length(); i++) {
						
						JSONObject entry = data.optJSONObject(i);
						
						if(entry != null) {
							
							UpcomingObject ob = new UpcomingObject();
							
							ob.setMovieTitle(entry.optString("MovieTitle"));
							ob.setMovieCover(entry.optString("MovieCover"));
							ob.setImdbCode(entry.optString("ImdbCode"));
							ob.setImdbCode(entry.optString("ImdbLink"));
							ob.setUploader(entry.optString("Uploader"));
							ob.setDateAdded(entry.optString("DateAdded"));
							
							results.add(ob);
						}
						
					}
					
				}
				
			} catch(JSONException e) {return results;}
			
		}catch(Exception e) {return results;}
		
		return results;
		
	}
	
	
	private String callApi(URL url, String method) {
		String result = "";
		
		Log.d("URL", url.toExternalForm());
		
		try {
			
			if(url != null) {
				
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setDoInput(true);
				conn.setRequestMethod(method);
				conn.connect();
				
				InputStream is = null;
				BufferedReader reader = null;
				
				int response = conn.getResponseCode();
				Log.d("response", "response code is: " + response);
				
				StringBuilder builder = new StringBuilder();
				
				if(response == 200) {
					
					is = conn.getInputStream();
					if(is != null) {
						
						String line = "";
						reader = new BufferedReader(new InputStreamReader(is));
						
						while((line = reader.readLine()) != null) {
							builder.append(line);
						}
						
						result = builder.toString();
					}
					
				}
				
			}
			
		} catch (Exception e) {Log.d("ERROR", e.getMessage());return "";}
		
		return result;
	}
	
	
}

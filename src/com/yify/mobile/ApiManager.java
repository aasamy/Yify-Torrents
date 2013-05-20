package com.yify.mobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
	
	public ArrayList<ListObject> getList(String keywords, String quality, String genre,
			int rating, int limit, int set, String sort, String order) {
		
		ArrayList<ListObject> data = new ArrayList<ListObject>();
		
		/* define defaults if any of the search parameters are null */
		
		String end_url = "";
		
		try {
		
		end_url += (keywords != null) ? "keywords=" + URLEncoder.encode(keywords, "UTF-8") + "&" : "keywords=&";
		end_url += (quality != null) ? "quality=" + quality + "&" : "quality=ALL&";
		end_url += (genre != null) ? "genre=" + URLEncoder.encode(genre, "UTF-8") + "&" : "genre=ALL&";
		end_url += "rating=" + rating + "&";
		end_url += "limit=" + limit + "&";
		end_url += "set=" + set + "&";
		end_url += (sort != null) ? "sort=" + sort + "&" : "sort=date&";
		end_url += (order != null) ? "order=" + order + "&" : "order=desc";
		
		URL url = new URL(this.baseURL + "list.json?" + end_url);
		
		String response = this.callApi(url, "GET");
		
		if(response.contains("\"error\" : ")) {
			return data;
		}
		
		JSONArray array = new JSONArray(response);
		
		if((array != null) && (array.length() != 0)) {
			
			for(int i = 0; i < array.length(); i++) {
				
				JSONObject entry = array.optJSONObject(i);
				
				if(entry != null) {
				
					ListObject lo = new ListObject();
					lo.setMovieID(entry.optInt("MovieID"));
					lo.setMovieURL(entry.optString("MovieURL"));
					lo.setMovieTitle(entry.optString("MovieTitle"));
					lo.setDateAdded(entry.optString("DateUploaded"));
					lo.setQuality(entry.optString("Quantity"));
					lo.setMovieCover(entry.optString("CoverImage"));
					lo.setImdbCode(entry.optString("ImdbCode"));
					lo.setImdbLink(entry.optString("ImdbLink"));
					lo.setFilesize(entry.optString("Size"));
					lo.setMovieRating(entry.optString("MovieRating"));
					lo.setGenre(entry.optString("Genre"));
					lo.setTorrentSeeds(entry.optString("TorrentSeeds"));
					lo.setDownloaded(entry.optInt("Downloaded"));
					lo.setTorrentPeers(entry.optInt("TorrentPeers"));
					lo.setTorrentURL(entry.optString("TorrentURL"));
					lo.setTorrentHash(entry.optString("TorrentHash"));
					lo.setTorrentMagnetURL(entry.optString("TorrentMagnetURL"));
					
					data.add(lo);
					
				}
				
			}
			
		}
		
		} catch(Exception e){return new ArrayList<ListObject>(); /*<-- return empty arraylist*/}
		
		return data;
		
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

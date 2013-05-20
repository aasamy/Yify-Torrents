package com.yify.mobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
			String result = this.callApi(url, "GET", null);
			
			
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
		
		String response = this.callApi(url, "GET", null);
		
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
					lo.setMovieURL(entry.optString("MovieUrl"));
					lo.setMovieTitle(entry.optString("MovieTitle"));
					lo.setDateAdded(entry.optString("DateUploaded"));
					lo.setQuality(entry.optString("Quality"));
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
	
	public ItemObject getMovieDetails(int id) {
		
		ItemObject lo = null;
		
		try {
			
			URL url = new URL(this.baseURL + "movie.json?id=" + id);
			String response = this.callApi(url, "GET", null);
			
			
			JSONObject entry = new JSONObject(response);
			
			String error = entry.optString("error");
			
			if((error != null) && (!error.equals(""))) {
				return null;
			}
			
			if(entry != null) {
				
				lo = new ItemObject();
				lo.setMovieID(entry.optInt("MovieID"));
				lo.setMovieURL(entry.optString("MovieUrl"));
				lo.setMovieTitle(entry.optString("MovieTitle"));
				lo.setDateAdded(entry.optString("DateUploaded"));
				lo.setQuality(entry.optString("Quality"));
				lo.setMovieCover(entry.optString("CoverImage"));
				lo.setImdbCode(entry.optString("ImdbCode"));
				lo.setImdbLink(entry.optString("ImdbLink"));
				lo.setFilesize(entry.optString("Size"));
				lo.setMovieRating(entry.optString("MovieRating"));
				lo.setGenre(entry.optString("Genre1"));
				lo.setTorrentSeeds(entry.optString("TorrentSeeds"));
				lo.setDownloaded(entry.optInt("Downloaded"));
				lo.setTorrentPeers(entry.optInt("TorrentPeers"));
				lo.setTorrentURL(entry.optString("TorrentURL"));
				lo.setTorrentHash(entry.optString("TorrentHash"));
				lo.setTorrentMagnetURL(entry.optString("TorrentMagnetURL"));
				lo.setUploader(entry.optString("Uploader"));
				lo.setUploaderNotes(entry.optString("UploaderNotes"));
				lo.setResolution(entry.optString("Resolution"));
				lo.setRuntime(entry.optString("MovieRuntime"));
				lo.setFrameRate(entry.optString("FrameRate"));
				lo.setLanguage(entry.optString("Language"));
				lo.setSubtitles(entry.optString("Subtitles"));
				lo.setYoutubeID(entry.optString("YoutubeTrailerID"));
				lo.setYoutubeURL(entry.optString("YoutubeTrailerURL"));
				lo.setAgeRating(entry.optString("AgeRating"));
				lo.setSubGenre(entry.optString("Genre2"));
				lo.setShortDescription(entry.optString("ShortDescription"));
				lo.setLongDescription(entry.optString("LongDescription"));
				
				HashMap<String, String> screenshots = new HashMap<String, String>();
				
				screenshots.put("med1", entry.optString("MediumScreebshot1"));
				screenshots.put("med2", entry.optString("MediumScreebshot2"));
				screenshots.put("med3", entry.optString("MediumScreebshot3"));
				screenshots.put("lrg1", entry.optString("LargeScreebshot1"));
				screenshots.put("lrg2", entry.optString("LargeScreebshot2"));
				screenshots.put("lrg3", entry.optString("LargeScreebshot2"));
				
				lo.setScreenshots(screenshots);
			}
			
		}catch(Exception e){return null;}
		
		return lo;
	}
	
	
	private String callApi(URL url, String method, String params) {
		String result = "";
		
		Log.d("URL", url.toExternalForm());
		
		try {
			
			if(url != null) {
				
				if(method.equalsIgnoreCase("get")) {
				
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
				} else if(method.equalsIgnoreCase("post")) {
					
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setInstanceFollowRedirects(false);
					conn.setRequestMethod(method);
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.setRequestProperty("charset", "UTF-8");
					conn.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
					conn.setUseCaches(false);
					conn.connect();
					
					DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
					wr.writeBytes(params);
					wr.flush();
					wr.close();
					
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
					conn.disconnect();
				}
				
			}
			
		} catch (Exception e) {Log.d("ERROR", e.getMessage());return "";}
		
		return result;
	}
	
	public String register(String username, String password, String email) {
		
		String result = "";
		
		URL url = null;
		
		try {
			url = new URL(this.baseURL + "register.json");
		} catch (MalformedURLException e) {
			return "There has been an error processing your request, please try again";
		}
		
		if(url != null) {
			
			String response = null;
			try {
				response = this.callApi(url, "POST", "email=" + email + "&" +
					      "password=" + URLEncoder.encode(password, "UTF-8") + "&" + 
					      "username=" + URLEncoder.encode(username, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			JSONObject data = null;
			
			try {
				data = new JSONObject(response);
			} catch (JSONException e) {
				return "There has been an error processing your request, please try again";
			}
			
			if(data != null) {
				
				String e = data.optString("error");
				
				if(!e.equals("")) {
					return e;
				}
				
				String s = data.optString("success");
				
				result = s;
				
			}
			
		}
		
		return result;
		
	}
	
	public String registerConfirm(String verificationCode) {
		
		String result = "";
		
		URL url = null;
		
		try {
			url = new URL(this.baseURL + "registerconfirm.json");
		} catch (MalformedURLException e) {
			return "There has been an error processing your request, please try again";
		}
		
		if(url != null) {
			
			String response = this.callApi(url, "POST", "code=" + verificationCode);
			
			JSONObject data = null;
			
			try {
				data = new JSONObject(response);
			} catch (JSONException e) {
				return "There has been an error processing your request, please try again";
			}
			
			if(data != null) {
				
				String e = data.optString("error");
				
				if(!e.equals("")) {
					return e;
				}
				
				String s = data.optString("success");
				
				result = s;
				
			}
			
		}
		
		return result;
		
	}
	
	public String[] login(String username, String password) {
		
		URL url = null;
		String[] result = null;
		
		try {
			url = new URL(this.baseURL + "login.json");
		} catch (MalformedURLException e) {
			return new String[] {"An Error Occured with your request."};
		} 
		
		if(url != null) {
			
			String response = this.callApi(url, "POST", "username=" + username + "&password=" + password);
			
			JSONObject json = null;
			
			try {
				json = new JSONObject(response);
				
				String e = json.optString("error");
				
				if(!e.equals("")) {
					return new String[] {e};
				}
				result = new String[] {json.optString("hash"), json.optString("userID"), json.optString("username")};
				
			} catch (JSONException e) {
				return new String[] {"An error occured with your request."};
			}
		}
		
		return result;
		
	}
	
	public UserObject getUserFromID(int userID) {
		
		UserObject user = null;
		URL url = null;
		
		try {
			url = new URL(this.baseURL + "user.json?id=" + userID);
		} catch(MalformedURLException e) {
			return null;
		}
		
		if(url != null) {
			
			String response = this.callApi(url, "GET", null);
			
			try {
				
				JSONObject u = new JSONObject(response);
				
				String error = u.optString("error");
				
				if(!error.equals("")) {
					return null;
				}
				
				user = new UserObject();
				user.setUserID(u.optInt("UserID"));
				user.setUsername(u.optString("UserName"));
				user.setJoinDate(u.optString("JoinDated"));
				user.setLastSeenDate(u.optString("LastSeenDate"));
				user.setTorrentsDownloaded(u.optInt("TorrentsDownloadedCount"));
				user.setMoviesRequested(u.optInt("MoviesRequestedCount"));
				user.setCommentCount(u.optInt("CommentCount"));
				user.setChatTimeSeconds(u.optInt("ChatTimeSeconds"));
				user.setAvatar(u.optString("Avatar"));
				user.setAbout(u.optString("About"));
				
				
			} catch (JSONException e) {
				return null;
			}
		}
		return user;
		
	}
	
	public ArrayList<CommentObject> getMovieComments(int movieID) {
		
		ArrayList<CommentObject> data = new ArrayList<CommentObject>();
		URL url = null;
		try {
			url = new URL(this.baseURL + "comments.json?movieid=" + movieID);
		} catch (MalformedURLException e) {
			
			return data;
			
		}
		
		if(url != null) {
			
			String response = this.callApi(url, "GET", null);
			
			try {
				JSONObject error = new JSONObject(response);
				
				String err = error.optString("error");
				
				if(err != "") {
					
					return data;
					
				}
				
			} catch (JSONException e) {
				
				/* misuse of JSONException but hey... */
				try {
					
					JSONArray array = new JSONArray(response);
					
					if((array != null) && (array.length()!= 0)) {
						
						for(int i = 0; i < array.length(); i++) {
							
							JSONObject object = array.optJSONObject(i);
							
							if(object != null) {
								
								CommentObject co = new CommentObject();
								
								co.setCommentID(object.optInt("CommentID"));
								co.setText(object.optString("CommentText"));
								co.setUserID(object.optInt("UserID"));
								co.setParentCommentID(object.optString("ParentCommentID", "Not a reply."));
								co.setUsername(object.optString("UserName"));
								co.setUserGroup(object.optString("UserGroup"));
								co.setDateAdded(object.optString("DateAdded"));
								
								data.add(co);
							}
						}
						
						return data;
						
					}
					
				} catch (JSONException e1) {
					return data;
				}
				
			}
			
		}
		return data;
	}
	
	
}

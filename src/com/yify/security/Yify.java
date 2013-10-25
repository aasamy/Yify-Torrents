package com.yify.security;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yify.data.Core;
import com.yify.data.Trakt;
import com.yify.entity.Movie;
import com.yify.entity.Request;
import com.yify.object.UpcomingObject;

public class Yify extends Core {
	
	private String baseURL = "http://yify-torrents.com/api/";
	
	/**
	 * this functions acts as a virtual 'login' function, and returns a unique 'hash' to use to
	 * do account related tasks. The hash is valid for as long as it is kept. May have to put my own validation 
	 * on the hash to make the user re login after a certain time.
	 * @param username the username your assingned when you registered.
	 * @param password the password for your account.
	 * @return String[] a string array with an error message on failure, an array of account data on success, including the hash, userID and 
	 * username of the account holder.
	 */
	protected String[] login(String username, String password) {
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
	
	/**
	 * this method is essentially the base search method for the whole application, you can return All movies or filter it down
	 * based on the params provided.
	 * @param keywords the keywords to search for.
	 * @param quality the quality to filter to can be either 1080p, 720p or 3D.
	 * @param genre the genre to filter to visit  {@link http://www.imdb.com/genres/} for valid genres.
	 * @param rating the rating to filter to, can between 0-9
	 * @param limit the amount of results to limit to, default is 20.
	 * @param set the startIndex for the returned results. i.e set 2 on limit 20 will return results 21-40
	 * @param sort what to sort the results by, can be either date, seeds, peers, size, alphabet, rating, downloaded.
	 * @param order whether to return the results asc or desc.
	 * @return Request the array of movie data.
	 * @version 1.1 - updated grab method based on the new result structure  in the list.json method.
	 * @version 1.2 - this function now returns a parcelable request object containing all of the movie information
	 * along with the total results found. This function also grabs data from the Trakt api to get more complete and
	 * frankly better information on movies. The results from this method are Movie.TYPE_PARTIAL, meaning that the
	 * data in incomplete, and only provides the information needed to display this information as a thumbnail or list,
	 * the Trakt.getMovieDetails() should be used to complete a movie. 
	 */
	public Request getList(String keywords, String quality,
			String genre, int rating, int limit, int set, String sort,
			String order) {

		Request data = null;
		/* define defaults if any of the search parameters are null */
		String end_url = "";
		try {
			end_url += (keywords != null) ? "keywords="
					+ URLEncoder.encode(keywords, "UTF-8") + "&" : "keywords=&";
			end_url += (quality != null) ? "quality=" + quality + "&"
					: "quality=ALL&";
			end_url += (genre != null) ? "genre="
					+ URLEncoder.encode(genre, "UTF-8") + "&" : "genre=ALL&";
			end_url += "rating=" + rating + "&";
			end_url += "limit=" + limit + "&";
			end_url += "set=" + set + "&";
			end_url += (sort != null) ? "sort=" + sort + "&" : "sort=date&";
			end_url += (order != null) ? "order=" + order + "&" : "order=desc";
			URL url = new URL(this.baseURL + "list.json?" + end_url);
			String response = this.callApi(url, "GET", null);
			JSONObject json = new JSONObject(response);
			String err = json.optString("error");
			if (!err.equals("")) {
				return new Request();
			}
			JSONArray movies = json.optJSONArray("MovieList");
			int count = json.optInt("MovieCount");
			ArrayList<Movie> d = new ArrayList<Movie>();
			HashMap<String, String> f = new HashMap<String, String>();
			if (movies != null) {
				for (int i = 0; i < movies.length(); i++) {
					JSONObject entry = movies.getJSONObject(i);
					if (entry != null) {
						f.put(entry.optString("ImdbCode"), entry.optString("TorrentMagnetUrl"));
					}
				}
			}
			/* carry out a trakt request for partial data */
			Trakt t = new Trakt();
			d = t.getMovieDetailsBulk(f);
			data = new Request(d, count);
			
		} catch (Exception e) {
			return new Request();
		}
		return data;
	}
	
	/**
	 * grab information of upcoming titles on Yify-Torrents.
	 * @return returns a request object for the upcoming films,
	 * the upcoming api does not return a result count, so this will always be zero.
	 */
	public Request getUpcoming() {
		try {
			URL url = new URL(baseURL + "upcoming.json");
			String result = this.callApi(url, "GET", null);
			JSONArray data = null;
			try {
				JSONObject er = new JSONObject(result);
				String err = er.optString("error");
				if(!err.equals("")) {
					return new Request();
				}
			} catch (JSONException e) {
			try {
				data = new JSONArray(result);
				if((data != null) && (data.length() != 0)) {
					String[] yd = new String[data.length()];
					for(int i = 0; i < data.length(); i++) {
						JSONObject entry = data.optJSONObject(i);
						if(entry != null) {
							/* create a string array of imdb_ids */
							yd[i] = entry.optString("ImdbCode");
						}	
					}
					/* get the partial data from trakt */
					Trakt t = new Trakt();
					ArrayList<Movie> d = t.getUpcoming(yd);
					return new Request(d, 0);
				}
			} catch(JSONException e1) {return new Request();}
			}
		}catch(Exception e) {return new Request();}
			return new Request();	
		}
	}

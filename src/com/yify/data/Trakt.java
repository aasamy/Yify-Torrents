package com.yify.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yify.entity.Movie;

public class Trakt extends Core {
	
	private String baseURL = "http://api.trakt.tv/movie/";
	private String apiKey = "5e05ac83dca83bd9c8ed80282004f217";
	
	public static final int MODE_NORMAL = 0;
	public static final int MODE_FULL = 1;
	
	/**
	 * uses the summaries api function from trakt to get details for a list of movies,
	 * perfect for grabbing partial information i,e used for search results and scrollviews
	 * also good to partially display movie data on main movie page while loading rest from server.
	 * @param yifyData the hashmap with key = imdb_id and value = magnetUrl.
	 * @return an arraylist of partially generated movie objects.
	 */
	public ArrayList<Movie> getMovieDetailsBulk(HashMap<String, String> yifyData) {
		int mode = MODE_NORMAL;
		URL url = null; ArrayList<Movie> movie = null;
		String m = (mode == MODE_NORMAL) ? "normal" : "full";
		String ims = "";
		for(Map.Entry<String, String> entry : yifyData.entrySet()) {
			ims = ims + entry.getKey() + ",";
		}
		try {
			url = new URL(baseURL + "summaries.json/" + apiKey + "/" + ims + "/" + m);
		} catch (MalformedURLException e) {
			return null;
		}
		try {
			String response = this.callApi(url, "get", null);
			JSONObject json = new JSONObject(response);
			String err = json.optString("error");
			if (!err.equals("")) {
				return null;
			}
			JSONArray result = new JSONArray(response);
			if(result != null) {
				movie = new ArrayList<Movie>();
				for(int i = 0; i < result.length(); i++) {
					JSONObject o = result.optJSONObject(i);
					movie.add(new Movie(o, yifyData.get(o.optString("imdb_id")), Movie.TYPE_PARTIAL));
				}
			}
		} catch (JSONException e) {
			return null;
		}
		return movie;
	}
	
	/**
	 * returns the partial information needed for upcoming films.
	 * these film entities will never be TYPE_FULL as they don't have a main page.
	 * @param imdbIds the list of imdb_ids to get partial data for.
	 * @return an arraylist of partial movie entities.
	 */
	public ArrayList<Movie> getUpcoming(String[] imdbIds) {
		HashMap<String, String> data = new HashMap<String, String>();
		for(int i = 0; i < imdbIds.length; i++) {
			data.put(imdbIds[i], "");
		}
		return this.getMovieDetailsBulk(data);
	}
	
	/**
	 * completes a list of movies.
	 * @param currentMovies the list of movies.
	 */
	public void getMovieDetails(ArrayList<Movie> currentMovies) {
		for(Movie m : currentMovies) {
			m.completeMovie(getMovie(m));
		}
	}
	
	/**
	 * completes a single movie instance.
	 * @param movie the movie to complete.
	 */
	public void getMovieDetails(Movie movie) {
		movie.completeMovie(getMovie(movie));
	}
	
	/**
	 * performs a summary trakt api call to complete a movies information.
	 * @param movie the movie to complete.
	 * @return a JSONObject with the movies data.
	 */
	private JSONObject getMovie(Movie movie) {
		URL url = null; JSONObject result = null;
		try {
			url = new URL(baseURL + "summary.json/" + apiKey + "/" + movie.getImdbId());
		} catch (MalformedURLException e) {
			return null;
		}
		try {
			String response = this.callApi(url, "get", null);
			JSONObject json = new JSONObject(response);
			String err = json.optString("error");
			if (!err.equals("")) {
				return null;
			}
			result = new JSONObject(response);
		} catch(JSONException e) {
			return null;
		}
		return result;
	}
	
}

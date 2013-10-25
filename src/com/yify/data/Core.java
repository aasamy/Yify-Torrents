package com.yify.data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Core {
	
	/**
	 * complete API calling method can handle GET or POST requests.
	 * @param url the URL object to start the connection with.
	 * @param method the method of variable transport, i,e POST or GET
	 * @param params if the method is POST the variable string has to be defined here, if get this can be NULL as
	 * parameters are appended to the end of the initial URL.
	 * @return String the response from the server in its raw string form.
	 */
	protected String callApi(URL url, String method, String params) {
		String result = "";
		
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
			
		} catch (IOException e) {return e.getMessage();}
		
		return result;
	}

}

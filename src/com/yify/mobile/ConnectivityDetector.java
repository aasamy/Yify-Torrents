package com.yify.mobile;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class ConnectivityDetector {
	
	private ConnectivityManager manager;
	private LocationManager lManager;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	/**
	 * Constructor - explicitly initialises the manager without setting locationManager.
	 * - used for just testing connectionstatus functions, location based functions will always return null, 
	 * unless setLocationManager() is used.
	 * @param manager - the ConnectivityManager to determine the connection status.
	 */
	public ConnectivityDetector(ConnectivityManager manager) {
		this.manager = manager;
	}
	/**
	 * Constructor - uses the Activity Context to grab the system ConnectivityManager & LocationManager.
	 * this is the best Contructor to have both system services available.
	 * @param context - the Activity requesting connection status.
	 */
	public ConnectivityDetector(Context context) {
		manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		lManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	/**
	 * tests to see if the device is currently connected to the internet using either mobile of WIFI.
	 * @return boolean true, if connected, false if not.
	 */
	public boolean isConnectionAvailable() {
		
		boolean connected = false;
		if(manager instanceof ConnectivityManager && manager != null) {
			NetworkInfo info = manager.getActiveNetworkInfo();
			if(info != null && info.isConnected()) {
				connected = true;
			}
		}
		return connected;
		
	}
	/**
	 * sets the location Manager to use with this class, optional, only needed if location is needed.
	 * @param manager- the location manager.
	 * @return true on success.
	 */
	public boolean setLocationManager(LocationManager manager) {
		
		this.lManager = manager;
		return true;
		
	}
	/**
	 * tests to see if GPS is enabled.
	 * @param boolean autoStart flag to test to auto start options menu activity.
	 * @param Context the parent activity to start the options activity.
	 * @return true if it is, false if not.
	 */
	public boolean isGPSEnabled(boolean autoStart, Context context) {
		boolean enabled = false;
		if(lManager instanceof LocationManager && lManager != null) {
			 if(lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				 enabled = true;
				 if(autoStart) {
					 if(context != null) {
						 this.enableLocationSettings(context);
					 }
				 }
			 }
		}
		return enabled;
	}
	
	public void enableLocationSettings(Context context) {
		
		Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(settings);
	}
	
	/**
	 * returns a default location listener to use.
	 * @return the LocationListener to use.
	 */
	public LocationListener getLocationListener() {
		
		LocationListener listener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				Log.d("CurrentLocation", "lat : " + location.getLatitude() + ", lng : " + location.getLongitude());
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}

			
		};
	
		return listener;
	}
	/**
	 * tests to see if one location is better than the other.
	 * @param location - the new location
	 * @param currentBestLocation - the current best location
	 * @return true if better, false if not.
	 */
	public boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}

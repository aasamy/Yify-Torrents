package com.yify.mobile;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notifsenabled = prefs.getBoolean("pref_gen_notif", false);
		String[] timeString = prefs.getString("pref_gen_notif_refresh", "24:00").split(":");
		
		int hours = Integer.parseInt(timeString[0]);
		int minutes = Integer.parseInt(timeString[1]);
		
		int totalMinutes = (hours*60) + minutes;
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, NotificationService.class);
		
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		
		am.cancel(pi);
		
		if(notifsenabled) {
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
					SystemClock.elapsedRealtime() + totalMinutes*60*1000, totalMinutes*60*1000, pi);
		}

	}

}

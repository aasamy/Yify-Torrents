package com.yify.manager;

import java.util.ArrayList;

import com.yify.mobile.ConnectivityDetector;
import com.yify.mobile.MainActivity;
import com.yify.mobile.R;
import com.yify.object.ListObject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NotificationService extends Service {
	
	private WakeLock wakeLock;

	@Override
	public IBinder onBind(Intent intent) {return null;}
	
	private void handleIntent(Intent intent) {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
		wakeLock.acquire();
		
		ConnectivityDetector cm = new ConnectivityDetector(this);
		
		if(!cm.isConnectionAvailable()) {
			stopSelf();
			return;
		}
		
		new LatestMovies(new DatabaseManager(this)).execute();
	}
	
	private class LatestMovies extends AsyncTask<Void, Void, Integer> {
		
		private DatabaseManager dm;
		private final int nID = 1;
		
		public LatestMovies(DatabaseManager dm) {
			this.dm = dm;
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			
			ApiManager manager = new ApiManager();
			ArrayList<ListObject> list = manager.getList(null, null, null, 0, 10, 1, "date", "desc");
			
			ArrayList<Integer> movieids = new ArrayList<Integer>();
			
			for(ListObject ob : list) {
				movieids.add(ob.getMovieID());
			}
			
			return this.dm.getNewFilmCount(movieids, true);

		}
		
		@Override
		protected void onPostExecute(Integer response) {
			
			if(response != 0) {
			
				NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.logo)
				.setContentTitle("New Movies")
				.setContentText("There are new movies available to download.")
				.setNumber(response);
				
				Intent resultIntent = new Intent(NotificationService.this, MainActivity.class);
				
				TaskStackBuilder stack = TaskStackBuilder.create(getApplicationContext());
				stack.addParentStack(MainActivity.class);
				
				stack.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = 
						stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				
				builder.setContentIntent(resultPendingIntent);
				NotificationManager manager = (NotificationManager) 
						NotificationService.this.getSystemService(Context.NOTIFICATION_SERVICE);
				
				manager.notify(nID, builder.build());
				
			}
			
			Toast.makeText(getApplicationContext(), "ServiceFinished", Toast.LENGTH_SHORT).show();
			Log.d("Service.com.yify.notification", "Notification Service Completed, response is " + response);
			
			stopSelf();
			
		}
		
	}
	
	@Override
	public void onStart(Intent intent, int startID) {
		handleIntent(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleIntent(intent);
		return START_NOT_STICKY;
	}
	
	public void onDestroy() {
		super.onDestroy();
		wakeLock.release();
	}

}

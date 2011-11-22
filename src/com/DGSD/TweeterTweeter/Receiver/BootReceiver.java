package com.DGSD.TweeterTweeter.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import com.DGSD.TweeterTweeter.Service.DownloadService;
import com.DGSD.TweeterTweeter.TTApplication;

/*
 * Used to start various services when the phone boots.
 */
public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = BootReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent callingIntent) {

		// Check if we should do anything at boot at all
		long interval = ((TTApplication) context.getApplicationContext()).getUpdateInterval();
		
		if (interval == TTApplication.UPDATE_INTERVAL_NEVER) {
			return;
		}

		// Create the pending intent
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(DownloadService.TYPE, DownloadService.Data.ALL_DATA);
		
		PendingIntent pendingIntent = PendingIntent.getService(context, -1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT); 

		// Setup alarm service to wake up and start service periodically
		AlarmManager alarmManager = (AlarmManager) context
			.getSystemService(Context.ALARM_SERVICE); 
		
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, System
				.currentTimeMillis(), interval, pendingIntent);

		Log.d(TAG, "onReceived");
	}

}
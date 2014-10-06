package com.castro.runtracker;

import com.castro.runtracker.RunDatabaseHelper.RunCursor;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class RunManager {
	private static final String TAG = "RunManager";
    private static final String PREFS_FILE  = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.class";
	
	public static final String ACTION_LOCATION = "com.castro.android.runtracker.ACTION_LOCATION";
	public static final String TEST_PROVIDER = "TEST_PROVIDER";
	private static RunManager sRunManager;
	private Context mAppContext;
	private LocationManager mLocationManager;
	private RunDatabaseHelper mHelper;
	private SharedPreferences mPrefs;
	private long mCurrentRunId;

	// Private constuctor that forces users to use RunManager.get(Context)
	private RunManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager) mAppContext
				.getSystemService(Context.LOCATION_SERVICE);
		mHelper = new RunDatabaseHelper(mAppContext);
		mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
		mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
	}

	public static RunManager get(Context c) {
		if (sRunManager == null) {
			// Use application contect to avoid leak activites
			sRunManager = new RunManager(c.getApplicationContext());
		}
		return sRunManager;
	}

	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
	}

	public void startLocationUpdates() {
		String provider = LocationManager.GPS_PROVIDER;

		// Using test provider if its enabled
		if (mLocationManager.getProvider(TEST_PROVIDER) != null
				&& mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
			provider = TEST_PROVIDER;
		}

		Log.d(TAG, "using provider " + provider);

		// Get the last known location and broadcast it if you have one
		Location lastKnown = mLocationManager.getLastKnownLocation(provider);
		if (lastKnown != null) {
			// Reset the time to now
			lastKnown.setTime(System.currentTimeMillis());
			broadcastLocation(lastKnown);
		}

		// Start updates from location manager
		PendingIntent pi = getLocationPendingIntent(true);
		mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
		pi.cancel();
	}

	private void broadcastLocation(Location location) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
		mAppContext.sendBroadcast(broadcast);
	}
	
	public Run startNewRun() {
		//Insert new run into DB
		Run run = insertRun();
		//Start tracking the run
		startTrackingRun (run);
		return run;
	}



	private void startTrackingRun(Run run) {
		//Keep the Id
		mCurrentRunId = run.getId();
		//Store it in Shared Prefs
		mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();
		//Start location updates
		startLocationUpdates();
		
	}

	private Run insertRun() {
		Run run  = new Run();
		run.setId(mHelper.insertRun(run));
		return run;
	}
	
	public void stopRun() {
		stopLocationUpdates();
		mCurrentRunId = -1;
		mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
	}
	public void stopLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}

	public boolean isTrackingRun() {
		return getLocationPendingIntent(false) != null;
	}

	public void insertLocation (Location loc){
		if (mCurrentRunId != -1) {
			mHelper.insertLocation(mCurrentRunId, loc);
		} else {
			Log.e(TAG, "Location recieved with no tracking tun; ign");
		}
	}
	
	public RunCursor queryRuns(){
		return mHelper.queryRuns();
	}
}

package com.castro.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationReciever extends BroadcastReceiver {

	private static final String TAG = "LocationReciever";

	@Override
	public void onReceive(Context context, Intent intent) {

		Location location = intent
				.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		if (location != null) {
			onLocationRecieved(context, location);
			return;
		}

		if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
			boolean enabled = intent.getBooleanExtra(
					LocationManager.KEY_PROVIDER_ENABLED, false);
			onProviderEnabledChanged(enabled);
		}
	}

	private void onProviderEnabledChanged(boolean enabled) {
		Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));

	}

	private void onLocationRecieved(Context context, Location location) {
		Log.d(TAG, this + " Got location from " + location.getProvider() + ": "
				+ location.getLatitude() + ", " + location.getLongitude());
	}

}

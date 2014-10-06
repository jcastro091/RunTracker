package com.castro.runtracker;

import android.content.Context;
import android.location.Location;

public class TrackingLocationReciever extends LocationReciever {

	protected void onLocationRecieved(Context c, Location loc){
		RunManager.get(c).insertLocation(loc);
	}
}

package com.castro.runtracker;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class RunDatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	private static final String DB_NAME = "runs.sqlite";
	private static final String TABLE_RUN = "run";
	private static final String COLUMN_RUN_START_DATE = "start_date";
	private static final String COLUMN_RUN_ID = "_id";

	private static final String TABLE_LOCATION = "location";
	private static final String COLUMN_LOCATION_LATITUDE = "latitude";
	private static final String COLUMN_LOCATION_LONGITUDE = "longitude";
	private static final String COLUMN_LOCATION_ALTITUDE = "altitude";
	private static final String COLUMN_LOCATION_TIMESTAMP = "timestamp";
	private static final String COLUMN_LOCATION_PROVIDER = "provider";
	private static final String COLUMN_LOCATION_RUN_ID = "run_id";

	public RunDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create a "run" table
		db.execSQL("create table run ( "
				+ "_id integer primary key autoincrement, start_date integer)");
		// Create a "locations" table
		db.execSQL("create a table location ( "
				+ " timestamp integer, latitude real, longitude real, altitude real,"
				+ "provider varchar(100), run_id integer references run(_id))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Implement schema changes

	}

	public long insertRun(Run run) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_RUN_START_DATE, run.getStartDate().getTime());
		return getWritableDatabase().insert(TABLE_RUN, null, cv);
	}

	public long insertLocation(long runId, Location location) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LOCATION_LATITUDE, location.getLatitude());
		cv.put(COLUMN_LOCATION_LONGITUDE, location.getLongitude());
		cv.put(COLUMN_LOCATION_ALTITUDE, location.getAltitude());
		cv.put(COLUMN_LOCATION_TIMESTAMP, location.getTime());
		cv.put(COLUMN_LOCATION_PROVIDER, location.getProvider());
		cv.put(COLUMN_LOCATION_RUN_ID, runId);
		return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
	}

	public RunCursor queryRuns() {
		// equivalent to "select * from run order by start_date asc"
		Cursor wrapped = getReadableDatabase().query(TABLE_RUN, null, null,
				null, null, null, COLUMN_RUN_START_DATE + " asc");
		return new RunCursor(wrapped);
	}

	public static class RunCursor extends CursorWrapper {

		public RunCursor(Cursor c) {
			super(c);
		}

		public Run getRun() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			Run run = new Run();
			long runId = getLong(getColumnIndex(COLUMN_RUN_ID));
			run.setId(runId);
			long startDate = getLong(getColumnIndex(COLUMN_RUN_START_DATE));
			run.setStartDate(new Date(startDate));
			return run;

		}
	}

}

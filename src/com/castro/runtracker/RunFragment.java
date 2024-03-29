package com.castro.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RunFragment extends Fragment {

	private Button mStartButton, mStopButton;
	private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView,
			mAltitudeTextView, mDurationTextView;

	private RunManager mRunManager;
	private Run mRun;

	private BroadcastReceiver mLocatoinRecieved = new LocationReciever();
	private Location mLastLocation;

	protected void onLocationRevied(Context context, Location location) {
		mLastLocation = location;
		if (isVisible())
			updateUI();
	}

	protected void onProviderEnabledChanged(boolean enabled) {
		int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
		Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mRunManager = RunManager.get(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_run, parent, false);

		mStartedTextView = (TextView) v.findViewById(R.id.run_startedTextView);
		mLatitudeTextView = (TextView) v
				.findViewById(R.id.run_latitudeTextView);
		mLongitudeTextView = (TextView) v
				.findViewById(R.id.run_longitudeTextView);
		mAltitudeTextView = (TextView) v
				.findViewById(R.id.run_altitudeTextView);
		mDurationTextView = (TextView) v
				.findViewById(R.id.run_durationTextView);

		mStartButton = (Button) v.findViewById(R.id.run_startButton);
		mStartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
/*				mRunManager.startLocationUpdates();
				mRun = new Run();*/
				mRun = mRunManager.startNewRun();
				updateUI();
			}

		});

		mStopButton = (Button) v.findViewById(R.id.run_stopButton);
		mStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
/*				mRunManager.stopLocationUpdates();*/
				mRunManager.stopRun();
				updateUI();
			}

		});
		updateUI();

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(mLocatoinRecieved,
				new IntentFilter(RunManager.ACTION_LOCATION));
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocatoinRecieved);
		super.onStop();
	}

	private void updateUI() {
		boolean started = mRunManager.isTrackingRun();

		if (mRun != null)
			mStartedTextView.setText(mRun.getStartDate().toString());

		int durationSeconds = 0;
		if (mLastLocation != null) {
			durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
			mLatitudeTextView.setText(Double.toString(mLastLocation
					.getLatitude()));
			mLongitudeTextView.setText(Double.toString(mLastLocation
					.getLongitude()));
			mAltitudeTextView.setText(Double.toString(mLastLocation
					.getAltitude()));
		}
		mDurationTextView.setText(Run.formatDuration(durationSeconds));

		mStartButton.setEnabled(!started);
		mStopButton.setEnabled(started);
	}

}

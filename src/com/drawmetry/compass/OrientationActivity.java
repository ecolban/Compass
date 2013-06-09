package com.drawmetry.compass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class OrientationActivity extends Activity implements
		SensorEventListener {

	SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;

	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;

	TextView readingAzimuth; // , readingPitch, readingRoll;
	Compass myCompass;
	private float azimuth;

	// private double pitch;
	// private double roll;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		readingAzimuth = (TextView) findViewById(R.id.azimuth);
		// readingPitch = (TextView) findViewById(R.id.pitch);
		// readingRoll = (TextView) findViewById(R.id.roll);

		myCompass = (Compass) findViewById(R.id.mycompass);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];
	}

	@Override
	protected void onResume() {

		sensorManager.registerListener(this, sensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}

	@Override
	protected void onPause() {

		sensorManager.unregisterListener(this, sensorAccelerometer);
		sensorManager.unregisterListener(this, sensorMagneticField);
		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			for (int i = 0; i < 3; i++) {
				valuesAccelerometer[i] = event.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++) {
				valuesMagneticField[i] = event.values[i];
			}
			break;
		}

		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
				valuesAccelerometer, valuesMagneticField);

		if (success) {
			SensorManager.getOrientation(matrixR, matrixValues);

			float azimuthPrime = matrixValues[0];
			if (azimuthPrime < azimuth && azimuth - azimuthPrime > Math.PI) {
				azimuth -= 2 * Math.PI;
			} else if (azimuthPrime > azimuth
					&& azimuthPrime - azimuth > Math.PI) {
				azimuth += 2 * Math.PI;
			}
			azimuth = 0.8F * azimuth + 0.2F * azimuthPrime;
			// pitch = Math.toDegrees(matrixValues[1]);
			// roll = Math.toDegrees(matrixValues[2]);
			int heading = (int) Math.rint(Math.toDegrees(azimuth));
			readingAzimuth.setText(heading < 0 ? "W " + -heading : "E " + heading);
			// readingPitch.setText("Pitch: " + String.valueOf(pitch));
			// readingRoll.setText("Roll: " + String.valueOf(roll));

			myCompass.update(azimuth);
		}
	}

}

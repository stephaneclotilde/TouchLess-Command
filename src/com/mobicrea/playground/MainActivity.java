package com.mobicrea.playground;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {

	SensorManager mSensorManager;
	Sensor mProximitySensor;
	SensorEventListener mProximitySensorEventListener;
	
	private static final int REQUEST_CODE = 1337, UNLOCK = 0, LOCK = 1;
    
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mProximitySensor =  mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		
		mProximitySensorEventListener = new SensorEventListener(){
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) 
			{
				Toast.makeText(MainActivity.this,"proximity sensor "+accuracy, Toast.LENGTH_SHORT).show();
			}
		
			@Override
			public void onSensorChanged(SensorEvent event) 
			{
				if(event.sensor.getType()==Sensor.TYPE_PROXIMITY)
				{
					Toast.makeText(MainActivity.this, "proximity sensor "+event.values[0], Toast.LENGTH_SHORT).show();
					Window window = getWindow();
				    window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				            		| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				            		| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				            		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				    
				    if (isGoogleVoiceRecognizerInstalled()) 
				    	startVoiceRecognitionActivity();
				    
				    Handler handler = new Handler();
				    handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							Window window = getWindow();
						    window.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						            		| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						            		| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						            		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						}
					}, 3000);
				}
			}
	    };
	    
	    mSensorManager.registerListener(mProximitySensorEventListener, mProximitySensor, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void unlockOrLockDevice(int b)
	{
		if (b==UNLOCK)
		{
			
		}
		else//LOCK
		{
			
		}
	}
	
	private boolean isGoogleVoiceRecognizerInstalled()
	{
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	    if (activities.size() == 0)
	    {
	        return false;
	    }
	    
	    return true;
	}
	
	   /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    }

}

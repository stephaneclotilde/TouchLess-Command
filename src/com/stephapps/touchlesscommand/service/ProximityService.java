package com.stephapps.touchlesscommand.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.stephapps.touchlesscommand.MainActivity;
import com.stephapps.touchlesscommand.R;
import com.stephapps.touchlesscommand.VoiceCommandActivity;
import com.stephapps.touchlesscommand.misc.Preferences;

public class ProximityService extends Service implements SensorEventListener{
	
	public static final String TAG = ProximityService.class.getName();
	public static final int SCREEN_OFF_RECEIVER_DELAY = 500, UNLOCK = 0, LOCK = 1 , 
							DEVICE_BOOT=1000, APPLY_BUTTON_PUSHED=1001;
	Sensor proxSensor;
	SensorManager sm;
	private SensorManager mSensorManager = null;
	private WakeLock mWakeLock = null;

	@Override
	public void onCreate() 
	{
		Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        
        Editor edit = getSharedPreferences(Preferences.PREFS_NAME,MODE_PRIVATE).edit();
        edit.putBoolean("isServiceRunning", true);
        edit.commit();
	}
	
	
	
	@Override
	public void onDestroy() 
	{//here u should unregister sensor
	    Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
	    unregisterReceiver(mReceiver);
	    sm.unregisterListener(this);
	    mWakeLock.release();
        stopForeground(true);
        
        Editor edit = getSharedPreferences(Preferences.PREFS_NAME,MODE_PRIVATE).edit();
        edit.putBoolean("isServiceRunning", false);
        edit.commit();
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
//	    Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//	    sm=(SensorManager)getSystemService(SENSOR_SERVICE);
//	    proxSensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//	    sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
//
//	    //here u should make your service foreground so it will keep working even if app closed
//
//	    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//	    Intent bIntent = new Intent(ProximityService.this, MainActivity.class);       
//	    PendingIntent pbIntent = PendingIntent.getActivity(ProximityService.this, 0 , bIntent, 0);
//	    NotificationCompat.Builder bBuilder =
//	            new NotificationCompat.Builder(this)
//	                .setSmallIcon(R.drawable.ic_launcher)
//	                .setContentTitle("TouchLess-Command")
//	                .setContentText("")
//	                .setAutoCancel(true)
//	                .setOngoing(true)
//	                .setContentIntent(pbIntent);
//	    Notification barNotif = bBuilder.build();
//	    this.startForeground(1, barNotif);
//	    
	    startForeground(android.os.Process.myPid(), new Notification());
        registerListener();
        mWakeLock.acquire();

	    //then you should return sticky
	    return Service.START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public BroadcastReceiver mReceiver = new BroadcastReceiver() 
	{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }
             
            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };
    
    private void registerListener() 
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterListener() 
    {
        mSensorManager.unregisterListener(this);
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
	    // TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		if(event.sensor.getType()==Sensor.TYPE_PROXIMITY)
		{
			Toast.makeText(getApplicationContext(), "proximity sensor "+event.values[0], Toast.LENGTH_SHORT).show();
			if (event.values[0]<1)
			{
				SharedPreferences settings = getSharedPreferences(Preferences.PREFS_NAME,MODE_PRIVATE);
		    	long lastLaunchTime = settings.getLong(Preferences.VOICE_ACTION_LAUNCH_TIME, 0);
		    	
		    	if ((System.currentTimeMillis()-lastLaunchTime)>5000)
		    	{
					Intent intent = new Intent(getApplicationContext(),VoiceCommandActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(intent);
		    	}
			}
			
		}
	}
}

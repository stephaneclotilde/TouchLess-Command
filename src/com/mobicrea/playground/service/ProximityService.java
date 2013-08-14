package com.mobicrea.playground.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.mobicrea.playground.MainActivity;
import com.mobicrea.playground.R;

public class ProximityService extends Service implements SensorEventListener{
	
	Sensor proxSensor;
	SensorManager sm;
	
	@Override
	public void onCreate() {//onCreat shouldn't be used for sensor u should use onStartCommand
	    Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	    // TODO Auto-generated method stub
	    //here u should use the following code otherwise your sensor will be crazy
	    if(event.values[0] == 0){
	    //Intent panel = new Intent(this, Panel.class);
	    //startActivity(panel);
	    }
	}
	@Override
	public void onDestroy() {//here u should unregister sensor
	    Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
	    sm.unregisterListener(this);
	}

	@Override//here u should register sensor and write onStartCommand not onStart
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
	    sm=(SensorManager)getSystemService(SENSOR_SERVICE);
	    proxSensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	    sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);

	    //here u should make your service foreground so it will keep working even if app closed

	    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    Intent bIntent = new Intent(ProximityService.this, MainActivity.class);       
	    PendingIntent pbIntent = PendingIntent.getActivity(ProximityService.this, 0 , bIntent, 0);
	    NotificationCompat.Builder bBuilder =
	            new NotificationCompat.Builder(this)
	                .setSmallIcon(R.drawable.ic_launcher)
	                .setContentTitle("Title")
	                .setContentText("Subtitle")
	                .setAutoCancel(true)
	                .setOngoing(true)
	                .setContentIntent(pbIntent);
	    Notification barNotif = bBuilder.build();
	    this.startForeground(1, barNotif);

	        //then you should return sticky
	        return Service.START_STICKY;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}

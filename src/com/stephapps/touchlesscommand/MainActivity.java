package com.stephapps.touchlesscommand;

import java.util.Calendar;
import java.util.List;

import com.stephapps.touchlesscommand.misc.Preferences;
import com.stephapps.touchlesscommand.service.ProximityService;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button mApply;
	private boolean mIsServiceRunning = false;
    
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mApply 	= (Button)findViewById(R.id.btnApply);
		mApply.setOnClickListener(startListener);
        
		
		SharedPreferences settings = getSharedPreferences(Preferences.PREFS_NAME, MODE_PRIVATE);
		mIsServiceRunning = settings.getBoolean(Preferences.SERVICE_IS_RUNNING, false);
		if (mIsServiceRunning)
		{
			mApply.setText(R.string.btnDeactivate);
		}
		else
		{
			mApply.setText(R.string.btnActivate);
		}
		
		if ( settings.getBoolean("firstTime",true) == true )
		{
			Editor editor = settings.edit();
			editor.putBoolean("firstTime", false);
			editor.commit();
    		if (mIsServiceRunning==false) mApply.performClick();
    	}
		
		startService(new Intent(this,ProximityService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private OnClickListener startListener = new OnClickListener() 
	{
        public void onClick(View v)
        {
        	if (mIsServiceRunning)
        	{
        		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            	builder.setMessage(R.string.exitText)
            	       .setCancelable(false)
            	       .setPositiveButton(R.string.confirmExit, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
    	        	        	
            	        	    stopService(new Intent(MainActivity.this,ProximityService.class));
    	        	           	mApply.setText(R.string.btnActivate);
    	        	           	Editor edit= getSharedPreferences(Preferences.PREFS_NAME, 0).edit();
    	        	           	edit.putBoolean("startAtBootActivated", false);
    	        	           	edit.commit();
    	        	           	finish();
            	           }
            	       })
            	       .setNegativeButton(R.string.cancelExit, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	AlertDialog alert = builder.create();
            	alert.show(); 
        	}
        	else
        	{
        		Intent serviceIntent= new Intent(MainActivity.this,ProximityService.class);
        		serviceIntent.putExtra("com.stephapps.deskbar.typeOfIntent",ProximityService.APPLY_BUTTON_PUSHED);
        		startService(serviceIntent); 
        		Editor edit= getSharedPreferences(Preferences.PREFS_NAME, 0).edit();
        		edit.putBoolean("startAtBootActivated", true);
        		edit.commit();
        		
        		mApply.setText(R.string.btnDeactivate);
	        	
        	}
        }
	};
	
	public void modifyHoldTime( View v) 
	{
		TimePickerDialog tp;
	    if (v.getId()==R.id.btnModifyStartTime)
	    {
	    	 tp = new TimePickerDialog(this, mTimeListener, 21, 00, true);
	    	 
	    }
	    else //R.id.btnModifyEndTime
	    {
	    	tp = new TimePickerDialog(this, mTimeListener, 9, 00, true);
	    	 
	    }
	    tp.show();
	}
	
	private TimePickerDialog.OnTimeSetListener mTimeListener =  new TimePickerDialog.OnTimeSetListener() 
	{
        public void onTimeSet(TimePicker view, int hour, int minute) 
        {
           Toast.makeText(getApplicationContext(), "hour:"+hour+" minute:"+minute,Toast.LENGTH_SHORT).show();
        }
	};
	
	

}

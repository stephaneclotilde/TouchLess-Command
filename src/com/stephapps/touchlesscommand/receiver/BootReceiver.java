package com.stephapps.touchlesscommand.receiver;

import com.stephapps.touchlesscommand.misc.Preferences;
import com.stephapps.touchlesscommand.service.ProximityService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver  extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	SharedPreferences settings = context.getSharedPreferences(Preferences.PREFS_NAME, 0);
		
		if (( settings.getBoolean("isEnabledOnBoot",true)==true)&&(settings.getBoolean("startAtBootActivated", true)==true))
		{
	    	Intent startServiceIntent = new Intent(context, ProximityService.class);
	    	startServiceIntent.putExtra("com.stephapps.touchless-command.typeOfIntent",ProximityService.DEVICE_BOOT);
	        context.startService(startServiceIntent);
		}
    }

		
	

}

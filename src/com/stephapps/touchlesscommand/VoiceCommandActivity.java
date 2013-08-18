package com.stephapps.touchlesscommand;

import java.util.List;

import com.stephapps.touchlesscommand.misc.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class VoiceCommandActivity extends Activity{

	private static final int UNLOCK = 0, LOCK = 1, REQUEST_CODE = 1337;
	private boolean mVoiceCommandHasBeenLaunched=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicecommand);
		
		unlockOrLockDevice(UNLOCK);
		
		if (isGoogleVoiceRecognizerInstalled()) 
	    	startVoiceRecognitionActivity();
	    
	    Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {			
			@Override
			public void run() 
			{
				unlockOrLockDevice(LOCK);
			}
		}, 3000);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		if (mVoiceCommandHasBeenLaunched) 
			finish();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		mVoiceCommandHasBeenLaunched = true;
	}
	
	private void unlockOrLockDevice(int b)
	{
		Window window = getWindow();
		if (b==LOCK)
		{
			window.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
							  | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
							  | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
							  | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		else//UNLOCK
		{
			window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
							| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
							| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
							| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
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
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
//        startActivityForResult(intent, REQUEST_CODE);
    	
//    	Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);    
    	launchGoogleNowVoice();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if (requestCode == REQUEST_CODE) 
        {
            if (resultCode == RESULT_OK) {
                
            }
        }
    }
    
    private void launchGoogleNowVoice()
    {
    	Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {	
			@Override
			public void run() 
			{
				Intent intent = new Intent();
		    	intent.setPackage("com.google.android.googlequicksearchbox");
		    	intent.setAction("android.intent.action.SEARCH_LONG_PRESS");
		    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    	startActivity(intent);
		    	
		    	Editor edit = getSharedPreferences(Preferences.PREFS_NAME,MODE_PRIVATE).edit();
		    	edit.putLong(Preferences.VOICE_ACTION_LAUNCH_TIME, System.currentTimeMillis());
		    	edit.commit();
			}
		}, 500);
    }

}

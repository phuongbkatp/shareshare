package com.techmax.shareforshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    private static final String START_TIME="Countdown_timer";
    private static final String MAX_TIME="Countdown_max";
    private SharedPreferences mPreferences;

    public PrefUtils(Context context){
        mPreferences= PreferenceManager.getDefaultSharedPreferences(context);
    }


    public int getStartedTime(){
        return mPreferences.getInt(START_TIME,0);
    }

    public void setStartedTime(int startedTime){
        SharedPreferences.Editor editor=mPreferences.edit();
        editor.putInt(START_TIME,startedTime);
        editor.apply();
    }

    public int getMaxTime(){
        return mPreferences.getInt(MAX_TIME,0);
    }

    public void setMaxTime(int startedTime){
        SharedPreferences.Editor editor=mPreferences.edit();
        editor.putInt(MAX_TIME,startedTime);
        editor.apply();
    }
}

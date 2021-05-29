package com.velocityappsdj.gallerycleaner;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {

    private Context mContext;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
        sharedPref = mContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void saveSortPref(String pref) {
        editor.putString("SORT_TYPE", pref);
        editor.commit();
    }

    public String getSortPref() {
        return sharedPref.getString("SORT_TYPE", "");
    }

    public void setShowCaseDone(boolean done) {
        editor.putBoolean("SHOWCASE", done);
        editor.commit();
    }

    public boolean getShowCaseDone() {
        return sharedPref.getBoolean("SHOWCASE", false);
    }

    public void setCleaningDone(boolean done) {
        editor.putBoolean("CLEANING_DONE", done);
        editor.commit();
    }

    public boolean getCleaningDone() {
        return sharedPref.getBoolean("CLEANING_DONE", false);
    }

    public void setLastReviewAskedTime(Long lastReviewAskedTime) {
        editor.putLong("LAST_REVIEW", lastReviewAskedTime);
        editor.commit();
    }

    public Long getLastReviewASledTime() {
        return sharedPref.getLong("LAST_REVIEW", 0);
    }
}

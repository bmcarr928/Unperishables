package com.bmcarr.unperishable.util;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Provides miscellaneous utility methods to be used throughout the application
 */
public class Utilities {

    private static final String TAG = "Utilities";
    private static Context context;

    /**
     * Returns the application context when one isn't readily available, as in with testing
     *
     * @return A mock context object obtained through reflection
     */
    public static Context getContext() {
        if( Utilities.context != null ) {
            try {
                final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                final Method method = activityThreadClass.getMethod("currentApplication");
                Utilities.context = (Context) method.invoke(null, (Object[]) null);
                return Utilities.context;
            } catch( Exception e ) {
                Log.e(TAG, "Error while attempting to retrieve current application", e);
                return null;
            }
        } else {
            return context;
        }
    }

    /**
     * Deletes a database from the device's filesystem
     *
     * @param dbName Database to be deleted
     */
    public static void deleteDatabase(String dbName) {
        Log.d(TAG, "Database path: " + getContext().getDatabasePath(dbName));
        getContext().deleteDatabase(dbName);
    }

}

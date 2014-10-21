package com.bmcarr.unperishable.util;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by bmc on 10/21/14.
 */
public class Utilities {

    private static final String TAG = "Utilities";
    private static Context context;

    /**
     * Returns the android storage directory
     * @return A file pointing to the location where the database files are stored
     */
    public static Context getContext(Context context) {
        if( context == null ) {
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

    public static Context getContext() {
        if( Utilities.context != null ) {
            return Utilities.context;
        } else {
            return getContext(null);
        }
    }

    public static void deleteDatabase(String dbName) {
        Log.d(TAG, "Database path: " + getContext().getDatabasePath(dbName));
        getContext().deleteDatabase(dbName);
    }

}

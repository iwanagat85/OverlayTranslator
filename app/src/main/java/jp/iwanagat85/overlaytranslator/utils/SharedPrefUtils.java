package jp.iwanagat85.overlaytranslator.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public class SharedPrefUtils {

    private SharedPrefUtils() {
    }

    public static String getString(@NonNull Context context, @NonNull String key) {
        String result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            return preferences.getString(key, result);
        }
        return result;
    }

    public static boolean setString(@NonNull Context context, @NonNull String key, String value) {
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static int getInt(@NonNull Context context, @NonNull String key) {
        int result = 0;
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            return preferences.getInt(key, result);
        }
        return result;
    }

    public static boolean setInt(@NonNull Context context, @NonNull String key, int value) {
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    public static long getLong(@NonNull Context context, @NonNull String key) {
        long result = 0L;
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            return preferences.getLong(key, result);
        }
        return result;
    }

    public static boolean setLong(@NonNull Context context, @NonNull String key, long value) {
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean getBoolean(@NonNull Context context, @NonNull String key) {
        boolean result = false;
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            return preferences.getBoolean(key, result);
        }
        return result;
    }

    public static boolean setBoolean(@NonNull Context context, @NonNull String key, boolean value) {
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}

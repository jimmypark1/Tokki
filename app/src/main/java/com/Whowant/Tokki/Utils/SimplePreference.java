package com.Whowant.Tokki.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SimplePreference {

    public static void setPreference(Context context, String key, Object value) {
        if (context == null) return;

        setPreference(context, context.getPackageName(), key, value);
    }

    public static void setPreference(Context context, String name, String key, Object value) {
        if (context == null) return;
        nullCheckKey(key);
        nullCheckValue(value);

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (editor.equals(key)) {
            removePreference(context, key);
        }

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }

        editor.commit();
    }

    public static void removePreference(Context context, String key) {
        if (context == null) return;

        removePreference(context, context.getPackageName(), key);
    }

    public static void removePreference(Context context, String name, String key) {
        nullCheckKey(key);

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove(key);
        editor.commit();
    }

    public static boolean getBooleanPreference(Context context, String key) {
        return getBooleanPreference(context, context.getPackageName(), key, false);
    }


    public static boolean getBooleanPreference(Context context, String name, String key, boolean defValue) {
        nullCheckKey(key);

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getBoolean(key, defValue);
    }

    public static String getStringPreference(Context context, String key) {
        return getStringPreference(context, context.getPackageName(), key, "");
    }

    public static String getStringPreference(Context context, String name, String key, String defValue) {
        nullCheckKey(key);

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getString(key, defValue);
    }

    public static int getIntegerPreference(Context context, String key) {
        return getIntegerPreference(context, context.getPackageName(), key, -1);
    }

    public static int getIntegerPreference(Context context, String name, String key, int defValue) {
        nullCheckKey(key);

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getInt(key, defValue);
    }

    public static float getFloatPreference(Context context, String key) {
        return getFloatPreference(context, context.getPackageName(), key, -1f);
    }

    public static float getFloatPreference(Context context, String name, String key, float defValue) {
        nullCheckKey(key);

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getFloat(key, defValue);
    }

    public static long getLongPreference(Context context, String key) {
        return getLongPreference(context, context.getPackageName(), key, -1L);
    }

    public static long getLongPreference(Context context, String name, String key, long defValue) {
        nullCheckKey(key);

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getLong(key, defValue);
    }

    private static void nullCheckKey(String key) {
        if (key == null)
            throw new NullPointerException("key is not null (key value = " + key + ")");
    }

    private static void nullCheckValue(Object value) {
        if (value == null)
            throw new NullPointerException("value is not null (value = " + value + ")");
    }
}

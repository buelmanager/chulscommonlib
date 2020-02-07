package com.orhanobut.logger;

import android.util.Log;

/**
 * Created by blue7 on 2018-05-09.
    log.set(Common.PACKAGE_NAME, IS_DEBUGGING);
 */
public class log {

    private static String TAG = "";
    public log() {
        super();
    }

    private static FormatStrategy formatStrategy;
    private static Boolean isDebug = false;

    public static void set(String tag, boolean bool) {
        if (formatStrategy != null) return;
        formatStrategy = PrettyFormatStrategy.newBuilder()
                .methodCount(3)
                .showThreadInfo(false)      // (Optional) Whether to show thread info or not. Default true
                .tag(tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        TAG = tag;
        isDebug = bool;
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    public static void i(String title, String s) {
        Log.i(TAG, "");
        Log.i(TAG, "┌────── [ " + title + " ] ──────┐");
        Logger.i(s);
        Log.i(TAG, "");
        SaveLog.INSTANCE.save(SaveLog.LogType.I , s);
    }

    public static void s(String title, String s) {
        if (isDebug)
            Logger.i("[ " + title + " ]- " + s);

        SaveLog.INSTANCE.save(SaveLog.LogType.D , s);
    }

    public static void i(String s) {
        Logger.i(s);
        SaveLog.INSTANCE.save(SaveLog.LogType.I , s);
    }

    public static void v(String title, String s) {
        if (isDebug) {
            Log.v(TAG, "");
            Log.v(TAG, "┌────── [ " + title + " ] ──────┐");
            Logger.v(s);
            Log.v(TAG, "");
            SaveLog.INSTANCE.save(SaveLog.LogType.V , s);
        }
    }

    public static void v(String s) {
        if (isDebug)
            Logger.v(s);

        SaveLog.INSTANCE.save(SaveLog.LogType.V , s);
    }

    public static void d(String title, Object obj) {
        Log.d(TAG, "");
        Log.d(TAG, "┌────── [ " + title + " ] ──────┐");
        Logger.d(obj);
        Log.d(TAG, "");

        SaveLog.INSTANCE.save(SaveLog.LogType.D , obj.toString());
    }

    public static void d(Object obj) {
        Logger.d(obj);
        SaveLog.INSTANCE.save(SaveLog.LogType.D , obj.toString());
    }

    public static void e(String title, String str) {
        Log.e(TAG, "");
        Log.e(TAG, "┌────── [ " + title + " ] ──────┐");
        Logger.e(str);
        Log.e(TAG, "");
        SaveLog.INSTANCE.save(SaveLog.LogType.E , str);
    }

    public static void e(String str) {
        Logger.e(str);
        SaveLog.INSTANCE.save(SaveLog.LogType.E , str);
    }

    public static void setIsDebug(Boolean debug) {
        isDebug = debug;
    }
}

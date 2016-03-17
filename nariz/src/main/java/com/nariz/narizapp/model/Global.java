package com.nariz.narizapp.model;

import android.app.Application;
import android.os.Environment;

/**
 * Created by ACsatillo on 20/10/2015.
 */
public class Global extends Application {
    public static String url_serv = "http://10.188.26.136/WSNariz/";
    public static boolean online = false;
    public static String dirApp = Environment.getExternalStorageDirectory() + "/NarizApp/";
    public static String dirPq = dirApp + "/FotoPeques/";
    public static String dirPqthumb = dirPq + "/thumbnail/";

    private static Global singleton;

    public static Global getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

}
